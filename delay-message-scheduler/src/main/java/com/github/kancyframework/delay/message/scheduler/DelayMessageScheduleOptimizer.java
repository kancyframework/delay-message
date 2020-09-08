package com.github.kancyframework.delay.message.scheduler;

import com.github.kancyframework.delay.message.message.MessageStatus;
import com.github.kancyframework.delay.message.service.DelayMessageService;
import com.github.kancyframework.delay.message.scheduler.properties.DelayMessageSchedulerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DelayMessageScheduleOptimizer
 *
 * @author kancy
 * @date 2020/7/18 8:59
 */
public class DelayMessageScheduleOptimizer implements InitializingBean , DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DelayMessageScheduleOptimizer.class);

    private static final Map<String, Long> EXPIRE_TIME_MAP = new HashMap<>();

    private final ScheduledExecutorService scheduledExecutorService;

    private final DelayMessageSchedulerProperties schedulerProperties;

    private final DelayMessageService delayMessageService;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public DelayMessageScheduleOptimizer(DelayMessageSchedulerProperties schedulerProperties,
                                         DelayMessageService delayMessageService) {
        this.schedulerProperties = schedulerProperties;
        this.delayMessageService = delayMessageService;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() + 1,
                new CustomizableThreadFactory("delay-scheduler-"));
    }

    public long getMinExpireTime(String tableName) {
        return EXPIRE_TIME_MAP.getOrDefault(tableName, getDefaultMinExpireTime());
    }

    public void setMinExpireTime(String tableName, long timestamp) {
        EXPIRE_TIME_MAP.put(tableName, timestamp);
    }

    /**
     * 开启后台调度优化任务
     */
    private void start() {
        // 启动一个定时调度，用来重置长时间处于处理中的数据
        Assert.state(schedulerProperties.getMaxResetMessageStatusTime().compareTo(schedulerProperties.getMinResetMessageStatusTime()) >= 0,
                "maxResetMessageStatusTime must greater or equal to minResetMessageStatusTime.");
        long random1 = ThreadLocalRandom.current().nextLong(schedulerProperties.getMinResetMessageStatusTime().toMillis(),
                schedulerProperties.getMaxResetMessageStatusTime().toMillis() + 1);
        scheduledExecutorService.scheduleAtFixedRate(this::resetMessageStatusOnProcessing, random1, random1, TimeUnit.MILLISECONDS);
        long random2 = ThreadLocalRandom.current().nextLong(schedulerProperties.getMinRefreshMinExpiredTime().toMillis(),
                schedulerProperties.getMaxRefreshMinExpiredTime().toMillis() + 1);
        scheduledExecutorService.scheduleAtFixedRate(this::refreshMinExpireTime, random2, random2, TimeUnit.MILLISECONDS);
        log.info("DelayMessageScheduler startup.");
    }

    /**
     * 刷新最小的到期时间
     * 查找出最近已过期的范围数据中
     */
    private void refreshMinExpireTime() {
        Set<String> tableNames = Collections.unmodifiableSet(EXPIRE_TIME_MAP.keySet());

        if (!CollectionUtils.isEmpty(tableNames)) {
            for (String tbName : tableNames) {
                final String tableName = tbName;
                scheduledExecutorService.execute(() -> {
                    Date minExpiredTime = delayMessageService.findMinExpiredTime(tableName,
                            schedulerProperties.getMaxExpireDiscrepancyIntervalTime().getSeconds());
                    if (Objects.nonNull(minExpiredTime)) {
                        EXPIRE_TIME_MAP.put(tableName, minExpiredTime.getTime());
                        log.info("table [{}] refreshExpireTime : {}", tableName, new Timestamp(minExpiredTime.getTime()));
                    }
                });
            }
        }
    }

    /**
     * 重置长时间处于处理中的数据
     */
    private void resetMessageStatusOnProcessing() {
        Set<String> tableNames = Collections.unmodifiableSet(EXPIRE_TIME_MAP.keySet());
        if (!CollectionUtils.isEmpty(tableNames)) {
            for (String tableName : tableNames) {
                scheduledExecutorService.execute(() -> {
                    try {
                        // 消息在处理中状态持续时间超过3分钟，重置他的状态
                        List<String> ids = delayMessageService.findAllExecuteTimeoutMessageIds(tableName,
                                Duration.ofSeconds(schedulerProperties.getProcessingTimeout()), getRecentSeconds());
                        if (CollectionUtils.isEmpty(ids)) {
                            return;
                        }
                        delayMessageService.batchUpdateStatus(tableName, ids, MessageStatus.RUNNING.ordinal(), MessageStatus.TIMEOUT.ordinal());
                        log.info(" table [{}] resetMessageStatusOnProcessing success : {}", tableName, ids);
                    } catch (Exception e) {
                        log.error(" table [{}] resetMessageStatusOnProcessing fail : {}", tableName, e.getMessage());
                    }
                });
            }
        }
    }

    private long getRecentSeconds() {
       long recentSeconds = schedulerProperties.getMaxResetMessageStatusTime().getSeconds() * 10;
       long minSeconds = Duration.ofHours(2).getSeconds();
       return Math.max(recentSeconds, minSeconds);
    }

    private Long getDefaultMinExpireTime() {
        return System.currentTimeMillis() - schedulerProperties.getMaxExpireDiscrepancyIntervalTime().toMillis();
    }

    @Override
    public void afterPropertiesSet() {
        if (started.compareAndSet(false, true)){
            start();
        }
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     */
    @Override
    public void destroy() {
        if (Objects.nonNull(scheduledExecutorService) && started.get()
                && !scheduledExecutorService.isShutdown()){
            scheduledExecutorService.shutdown();
        }
    }
}
