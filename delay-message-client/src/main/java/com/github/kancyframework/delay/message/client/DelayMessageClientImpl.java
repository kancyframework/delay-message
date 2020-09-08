package com.github.kancyframework.delay.message.client;

import com.github.kancyframework.delay.message.cache.DelayMessageConfigCache;
import com.github.kancyframework.delay.message.client.properties.DelayMessageClientProperties;
import com.github.kancyframework.delay.message.config.SnowIdGenerator;
import com.github.kancyframework.delay.message.exception.SendDelayMessageException;
import com.github.kancyframework.delay.message.interceptor.DelayMessageClientInterceptor;
import com.github.kancyframework.delay.message.message.DelayMessageAware;
import com.github.kancyframework.delay.message.message.IDelayMessage;
import com.github.kancyframework.delay.message.message.MessageStatus;
import com.github.kancyframework.delay.message.message.TraceDelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import com.github.kancyframework.delay.message.service.DelayMessageService;
import net.dreamlu.mica.core.utils.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * DelayMessageClientImpl
 *
 * @author kancy
 * @date 2020/7/15 20:30
 */
public class DelayMessageClientImpl implements DelayMessageClient {

    private static final Logger log = LoggerFactory.getLogger(DelayMessageClientImpl.class);

    @Autowired
    private SnowIdGenerator snowIdGenerator;
    @Autowired
    private DelayMessageClientProperties clientProperties;
    @Autowired
    private DelayMessageConfigCache delayMessageConfigCache;
    @Autowired
    private DelayMessageService delayMessageService;

    @Autowired(required = false)
    private List<DelayMessageClientInterceptor> interceptors = Collections.emptyList();

    @Override
    public boolean send(IDelayMessage message) {
        Assert.notNull(message, "message is null.");
        Assert.notNull(message.getPayload(), "message payload is null.");
        Assert.hasText(message.getMessageKey(), "message is null.");
        Assert.notNull(message.getDelay(), "delay is null.");
        Assert.state(message.getDelay().toMillis() > 0, "delay must greater than zero.");

        // 查询延时消息配置，并进行校验
        String messageKey = message.getMessageKey();
        DelayMessageConfig config = getDelayMessageConfig(messageKey);
        Assert.notNull(config, String.format("not support delay message type : %s ", messageKey));
        Assert.hasText(config.getTableName(), "table name is empty.");

        boolean result = false;
        try {
            interceptors.forEach(interceptor -> interceptor.sendBefore(message));
            result = doSend(message, config);
            interceptors.forEach(interceptor -> interceptor.sendAfter(message));
        } finally {
            try {
                for (DelayMessageClientInterceptor interceptor : interceptors) {
                    interceptor.sendCompleted(message, config, result);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    private boolean doSend(IDelayMessage message, DelayMessageConfig config) {
        String messageKey = message.getMessageKey();
        String tableName = config.getTableName();
        Class<?> payloadClass = message.getPayload().getClass();
        // 判断是否需要缓存消息内容
        String dataId = null;
        String payload = null;
        if (config.isUseCache()){
            try {
                Assert.state(!ClassUtils.isPrimitiveOrWrapper(payloadClass),
                        String.format("delay message [%s] use cache, payload type can't be %s.", messageKey, payloadClass.getName()));
                if (Objects.equals(String.class, payloadClass)){
                    payload = $.toJson($.readJsonAsMap(message.getPayload().toString(), String.class, Object.class));
                }else {
                    payload = $.toJson(message.getPayload());
                }
            } catch (Exception e) {
                throw new SendDelayMessageException(String.format(
                        "delay message [%s] use cache , but payload cannot be converted to json map format.", messageKey), e);
            }
            dataId = createDataId();
        } else {
            Assert.state(isBaseClassType(payloadClass),
                    String.format("delay message [%s] not use cache, but payload dataId must be primitive or wrapper type.", messageKey));
            dataId = String.valueOf(message.getPayload());
        }

        // 保存延时记录
        try {
            DelayMessage entity = initDelayMessage(message, config);
            entity.setDataId(dataId);
            entity.setPayload(payload);
            delayMessageService.storeDelayMessage(config.getTableName(), entity);
            // 回填属性
            if (message instanceof DelayMessageAware){
                ((DelayMessageAware)message).setDelayMessageId(entity.getId());
                ((DelayMessageAware)message).setDataId(dataId);
                ((DelayMessageAware)message).setTableName(tableName);
            }
            log.info("send delay message [{},{}] success : {}", tableName, messageKey, entity.getId());
        } catch (Exception e) {
            throw new SendDelayMessageException(String.format("send delay message [%s,%s] fail : %s",
                    tableName, messageKey, e.getMessage()), e);
        }
        return true;
    }

    /**
     * 获取DelayMessageConfig
     * @param messageKey
     * @return
     */
    private DelayMessageConfig getDelayMessageConfig(String messageKey) {
        return delayMessageConfigCache.queryConfigByMessageKey(messageKey);
    }

    /**
     * 初始化DelayMessage对象
     * @param message
     * @param config
     * @return
     */
    private DelayMessage initDelayMessage(IDelayMessage message, DelayMessageConfig config) {
        Date createdTime = new Date();
        DelayMessage entity = new DelayMessage();
        entity.setId(createMessageId());
        entity.setCreatedTime(createdTime);
        entity.setUpdatedTime(createdTime);
        entity.setExpiredTime(new Date(createdTime.getTime()
                + message.getDelay().toMillis()
                + clientProperties.getWasteTime()));
        entity.setDeletedTime(calcDeletedTime(entity, config));
        entity.setMessageKey(message.getMessageKey());
        entity.setMessageStatus(MessageStatus.WAITING.ordinal());
        entity.setDelay(message.getDelay());
        entity.setScanTimes(0);
        if (message instanceof TraceDelayMessage){
            entity.setTraceId(((TraceDelayMessage)message).getTraceId());
        }
        return entity;
    }

    /**
     * 创建数据ID
     * @return
     */
    private String createDataId() {
        return clientProperties.isUseSnowDataId() ?
                String.valueOf(snowIdGenerator.nextId()) :
                UUID.randomUUID().toString();
    }
    /**
     * 创建消息ID
     * @return
     */
    private String createMessageId() {
        return clientProperties.isUseSnowMessageId() ?
                String.valueOf(snowIdGenerator.nextId()) :
                UUID.randomUUID().toString();
    }

    private boolean isBaseClassType(Class<?> payloadClass) {
        return ClassUtils.isPrimitiveOrWrapper(payloadClass) || Objects.equals(String.class, payloadClass);
    }

    private Date calcDeletedTime(DelayMessage entity, DelayMessageConfig config) {
        Duration aliveTime = Objects.isNull(config.getAliveTime()) ?
                clientProperties.getMessageAliveTime() :
                DurationStyle.detectAndParse(config.getAliveTime());
        Date date = new Date(
                Duration.ofMillis(entity.getExpiredTime().getTime()).plus(aliveTime).toMillis());
        int hours = date.getHours();
        if (hours > 6){
            // 顺延到第二天的0-6点进行移除
            long gap = (24 - hours) * 3600000 ;
            date = new Date(ThreadLocalRandom.current()
                    .nextLong(0,  3600000 * 5) + gap + date.getTime());
        }
        return date;
    }

}
