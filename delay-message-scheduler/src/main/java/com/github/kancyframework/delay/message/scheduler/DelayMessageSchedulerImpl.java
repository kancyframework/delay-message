package com.github.kancyframework.delay.message.scheduler;

import com.github.kancyframework.delay.message.cache.DelayMessageConfigCache;
import com.github.kancyframework.delay.message.config.DelayMessageTaskExecutor;
import com.github.kancyframework.delay.message.message.MessageStatus;
import com.github.kancyframework.delay.message.service.DelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import com.github.kancyframework.delay.message.service.DelayMessageConfigService;
import com.github.kancyframework.delay.message.service.DelayMessageService;
import com.github.kancyframework.delay.message.interceptor.DelayMessageSchedulerInterceptor;
import com.github.kancyframework.delay.message.scheduler.handler.DelayMessageHandler;
import com.github.kancyframework.delay.message.scheduler.properties.DelayMessageSchedulerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DelayMessageSchedulerImpl
 *
 * @author kancy
 * @date 2020/7/15 22:18
 */
public class DelayMessageSchedulerImpl implements DelayMessageScheduler {

    private static final Logger log = LoggerFactory.getLogger(DelayMessageSchedulerImpl.class);

    @Autowired(required = false)
    private List<DelayMessageSchedulerInterceptor> interceptors = Collections.emptyList();

    @Autowired
    private DelayMessageSchedulerProperties schedulerProperties;
    @Autowired
    private DelayMessageScheduleOptimizer optimizer;
    @Resource
    private DelayMessageHandler delayMessageHandler;
    @Autowired
    private DelayMessageTaskExecutor delayMessageTaskExecutor;
    @Autowired
    private DelayMessageConfigCache delayMessageConfigCache;
    @Autowired
    private DelayMessageConfigService delayMessageConfigService;
    @Autowired
    private DelayMessageService delayMessageService;


    private static final AtomicInteger SCHEDULER_COUNT = new AtomicInteger(0);

    private static final Map<String, AtomicBoolean> SCHEDULER_EXEC_STATUS_MAP = new HashMap<>();

    @Override
    public int schedule(String tableName) {
        return schedule(tableName, schedulerProperties.getMaxPrefetchSize());
    }

    @Override
    public int schedule(String tableName, int limit) {
        return schedule(tableName, limit, null);
    }

    /**
     * 消费延时消息
     *
     * @param tableName
     * @param limit
     * @param minScanExpiredTime
     * @return
     */
    @Override
    public int schedule(String tableName, int limit, Date minScanExpiredTime) {
        return schedule(tableName, limit, minScanExpiredTime, this.delayMessageHandler);
    }

    /**
     * 调度实现
     *
     * @param tableName
     * @param limit
     * @param delayMessageHandler
     */
    private int schedule(String tableName, int limit, @Nullable Date minScanExpiredTime, DelayMessageHandler delayMessageHandler) {
        AtomicBoolean execFlag = null;
        synchronized (SCHEDULER_EXEC_STATUS_MAP) {
            execFlag = SCHEDULER_EXEC_STATUS_MAP.computeIfAbsent(tableName, (key) -> new AtomicBoolean(false));
        }

        if (execFlag.compareAndSet(false, true)) {
           return canSchedule(tableName, limit, minScanExpiredTime, delayMessageHandler, execFlag);
        } else {
            // 对于任务重叠的调度，直接舍弃
            log.warn("delay message table {} scheduler will give up schedule , " +
                    "because the last task [{}] has not been completed！", tableName, limit);
        }
        return 0;
    }

    /**
     * 可以消费消息
     *
     * @param tableName
     * @param limit
     * @param minScanExpiredTime
     * @param delayMessageHandler
     * @param execFlag
     * @return
     */
    private int canSchedule(String tableName, int limit, @Nullable Date minScanExpiredTime,
                             DelayMessageHandler delayMessageHandler, AtomicBoolean execFlag) {
        int scheduleSize = 0;
        try {
            interceptors.forEach(interceptor -> interceptor.scheduleBefore(tableName, limit));
            scheduleSize = doSchedule(tableName, limit, minScanExpiredTime, delayMessageHandler);
        } finally {
            SCHEDULER_COUNT.decrementAndGet();
            execFlag.set(false);
            for (DelayMessageSchedulerInterceptor interceptor : interceptors) {
                interceptor.scheduleCompleted(tableName, limit, scheduleSize);
            }
        }
        return scheduleSize;
    }

    /**
     * 消费消息
     *
     * @param tableName
     * @param limit
     * @param minScanExpiredTime
     * @param handler
     * @return
     */
    private int doSchedule(String tableName, int limit, @Nullable Date minScanExpiredTime, DelayMessageHandler handler) {
        final long startTime = System.currentTimeMillis();
        final boolean asyncExec = (SCHEDULER_COUNT.incrementAndGet() == 1);

        Assert.notNull(handler, "delayMessageHandler is null.");
        Assert.hasText(tableName, "tableName is empty.");
        Assert.state(limit > 0 && limit <= schedulerProperties.getMaxPrefetchSize(), "limit range is error.");

        // 查询到期的消息
        List<DelayMessage> entityList = scanExpiredDelayMessages(tableName, minScanExpiredTime, limit);
        if (CollectionUtils.isEmpty(entityList)) {
            log.debug("延时消息表[{}]暂无到期消息", tableName);
            return 0;
        }
        log.info("从延时消息表[{}]中取出到期的消息：{}", tableName, entityList.size());

        List<DelayMessageConfig> configs = delayMessageConfigService.queryConfigByMessageKeys(
                entityList.stream().map(DelayMessage::getMessageKey).collect(Collectors.toSet()));
        if (CollectionUtils.isEmpty(configs)) {
            log.error("delay message table [{}] not found any message key.", tableName);
            entityList.clear();
            return 0;
        }

        // 匹配更新消息状态为处理中
        delayMessageService.batchUpdateOnProcessing(tableName,
                entityList.stream().map(DelayMessage::getId).collect(Collectors.toList()));

        // 循环处理
        final int messageSize = entityList.size();
        final Map<String, DelayMessageConfig> configMap =
                configs.stream().collect(Collectors.toMap(DelayMessageConfig::getMessageKey, a -> a, (k1, k2) -> k1));
        // 失败计数器
        final AtomicInteger failCount = new AtomicInteger(0);
        // 并发控制器
        final CountDownLatch countDownLatch = new CountDownLatch(entityList.size());
        for (DelayMessage entity : entityList) {
            final DelayMessageConfig config = getDelayMessageConfig(configMap, entity);
            if (Objects.isNull(config)) {
                // 更新为失败，无法处理
                log.error("delay message [{}] config not found : {}", entity.getMessageKey(), entity.getId());
                delayMessageService.updateStatus(tableName, String.valueOf(entity.getId()), MessageStatus.FAIL.ordinal());
                failCount.incrementAndGet();
                continue;
            }
            // 数据库配置中表名有可能发生变化，还原该消息真实在的表
            config.setTableName(tableName);

            if (asyncExec || SCHEDULER_COUNT.get() == 1) {
                // 异步执行
                delayMessageTaskExecutor.execute(() -> {
                    try {
                        handleDelayMessage(handler, entity, config, failCount);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            } else {
                // 同步执行
                try {
                    handleDelayMessage(handler, entity, config, failCount);
                } finally {
                    countDownLatch.countDown();
                }
            }
        }

        try {
            // 等待所有消息执行结束
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            log.info("本次[{}]调度共处理 {} 条延时消息 , 耗时：{} , 其中成功处理：{}， 失败处理：{} ，异步：{}",
                    tableName, messageSize, System.currentTimeMillis() - startTime, messageSize - failCount.get(), failCount, asyncExec);

            for (DelayMessageSchedulerInterceptor interceptor : interceptors) {
                interceptor.scheduleAfter(tableName, limit, startTime, entityList, asyncExec, failCount.get());
            }

            // help gc
            entityList.clear();
            configs.clear();
        }
        return messageSize;
    }

    /**
     * 处理到期的延时消息
     *
     * @param handler
     * @param entity
     * @param config
     * @param failCount
     */
    private void handleDelayMessage(DelayMessageHandler handler, DelayMessage entity,
                                    DelayMessageConfig config, AtomicInteger failCount) {
        DelayMessageRef delayMessageRef = null;
        Exception exception = null;
        try {
            String messageId = entity.getId();
            delayMessageRef = createDelayMessageRef(entity, config);

            for (DelayMessageSchedulerInterceptor interceptor : interceptors) {
                interceptor.handleMessageBefore(entity, config);
            }
            handler.handle(delayMessageRef);
            delayMessageService.updateStatus(config.getTableName(), messageId, MessageStatus.SUCCESS.ordinal());
            log.info("延时消息表[{}]的消息处理成功: {}", config.getTableName(), messageId);
        } catch (Exception e) {
            exception = e;
            failCount.incrementAndGet();
            Object logData = Objects.isNull(delayMessageRef) ? entity : delayMessageRef;
            log.error("handle delay message fail : {} , {}", e.getMessage(), logData);
            handleConsumeException(config.getTableName(), entity, config);
        } finally {
            try {
                for (DelayMessageSchedulerInterceptor interceptor : interceptors) {
                    interceptor.handleMessageCompleted(entity, config, exception);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 处理消费异常
     *
     * @param tableName
     * @param entity
     * @param config
     */
    private void handleConsumeException(String tableName, DelayMessage entity, DelayMessageConfig config) {
        try {
            int maxScanTimes = config.getMaxScanTimes() <= 0 ? schedulerProperties.getMaxScanTimes() : config.getMaxScanTimes();
            if (entity.getScanTimes() >= maxScanTimes) {
                delayMessageService.updateStatus(tableName, entity.getId(), MessageStatus.FAIL.ordinal());
                log.info("更新延时消息表[{}]的消息状态为处理失败：{}", tableName, entity.getId());
            } else {
                delayMessageService.updateStatus(tableName, entity.getId(), MessageStatus.WAITING.ordinal());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取配置
     *
     * @param configMap
     * @param entity
     * @return
     */
    private DelayMessageConfig getDelayMessageConfig(Map<String, DelayMessageConfig> configMap,
                                                     DelayMessage entity) {
        return schedulerProperties.isUseConfigCache() ?
                delayMessageConfigCache.queryConfigByMessageKey(entity.getMessageKey()) :
                configMap.get(entity.getMessageKey());
    }

    /**
     * 创建DelayMessage数据引用对象
     *
     * @param entity
     * @param config
     * @return
     */
    private DelayMessageRef createDelayMessageRef(DelayMessage entity, DelayMessageConfig config) {
        DelayMessageRef ref = new DelayMessageRef();
        if (Objects.nonNull(entity.getPayload())) {
            ref.setPayload(entity.getPayload());
            ref.setUseCache(true);
        } else {
            ref.setPayload(entity.getDataId());
            ref.setUseCache(false);
        }
        ref.setDelay(entity.getDelay());
        ref.setTraceId(entity.getTraceId());
        ref.setDelayMessageId(entity.getId());
        ref.setTableName(config.getTableName());
        ref.setMessageKey(entity.getMessageKey());
        ref.setMessageType(config.getMessageType());
        ref.setDataId(entity.getDataId());
        ref.setMaxScanTimes(config.getMaxScanTimes());
        ref.setScanTimes(entity.getScanTimes());
        ref.setNoticeAddress(config.getNoticeAddress());
        ref.setNoticeType(config.getNoticeType());
        return ref;
    }

    private List<DelayMessage> scanExpiredDelayMessages(String tableName, @Nullable Date minScanExpiredTime, int limit) {
        final long minExpireTime = Objects.isNull(minScanExpiredTime) ?
                optimizer.getMinExpireTime(tableName) : minScanExpiredTime.getTime();
        final long startTime = System.currentTimeMillis();
        interceptors.forEach(interceptor -> interceptor.scheduleBefore(tableName, limit));
        final List<DelayMessage> delayMessageEntities = delayMessageService.scanExpiredMessage(tableName, minExpireTime, limit);
        interceptors.forEach(interceptor -> interceptor.scanCompleted(tableName, limit, minExpireTime, startTime, delayMessageEntities));
        return delayMessageEntities;
    }

}
