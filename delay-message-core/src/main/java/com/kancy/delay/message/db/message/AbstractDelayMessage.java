package com.kancy.delay.message.db.message;

import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DelayMessage
 *
 * @author kancy
 * @date 2020/7/15 20:29
 */
abstract class AbstractDelayMessage implements TraceDelayMessage, DelayMessageAware {

    /**
     * 缓存这个值，避免每次解析
     */
    private static final Map<Class<? extends AbstractDelayMessage>, DelayConfig> CACHE = new HashMap<>();

    /**
     * 延时时间
     */
    private Duration delay;
    /**
     * 消息体
     */
    private Object payload;
    /**
     * 链路ID
     */
    private String traceId;
    /**
     * 延时消息ID
     */
    private String delayMessageId;
    /**
     * 延时消息KEY
     */
    private String messageKey;
    /**
     * 数据ID
     */
    private String dataId;

    /**
     * 消息所在表
     */
    private String tableName;

    @Override
    public String getMessageKey() {
        if (Objects.isNull(this.messageKey)){
            return getConfig().getKey();
        }
        return this.messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public Duration getDelay() {
        if (Objects.isNull(delay)) {
            setDelay(getConfig().getDelay());
        }
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public void setExpireTime(Date expireTime) {
        this.delay = Duration.ofMillis(expireTime.getTime() - System.currentTimeMillis());
    }

    public String getDelayMessageId() {
        return delayMessageId;
    }

    @Override
    public void setDelayMessageId(String delayMessageId) {
        this.delayMessageId = delayMessageId;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getDataId() {
        return dataId;
    }

    @Override
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getTraceId() {
        if (Objects.isNull(traceId)) {
            traceId = MDC.get(TraceDelayMessage.TRACE_ID_KEY);
        }
        return traceId;
    }

    private DelayConfig getConfig() {
        if (!CACHE.containsKey(this.getClass())) {
            synchronized (CACHE) {
                if (!CACHE.containsKey(this.getClass())) {
                    CACHE.put(this.getClass(), initConfig());
                }
            }
        }
        return CACHE.get(this.getClass());
    }

    /**
     * 初始化
     */
    private DelayConfig initConfig() {
        // 用注解的方式
        DelayMessage annotation = AnnotationUtils.findAnnotation(this.getClass(), DelayMessage.class);
        Assert.notNull(annotation, String.format("%s not delay message.", this.getClass().getName()));
        Assert.hasText(annotation.value(), String.format("delay message [%s] key is empty", this.getClass().getName()));

        DelayConfig config = new DelayConfig();
        config.setKey(annotation.value());
        config.setDelay(parseDelayString(annotation.delay()));
        CACHE.put(this.getClass(), config);
        return config;
    }

    private Duration parseDelayString(String delayString) {
        if (delayString.isEmpty()) {
            return null;
        }
        try {
            return Duration.parse(delayString);
        } catch (Exception e) {
            return null;
        }
    }

    static class DelayConfig {
        private String key;
        private Duration delay;

        String getKey() {
            return key;
        }

        void setKey(String key) {
            this.key = key;
        }

        public Duration getDelay() {
            return delay;
        }

        public void setDelay(Duration delay) {
            this.delay = delay;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("messageKey='").append(getMessageKey()).append('\'');
        sb.append(", delay=").append(delay);
        sb.append(", delayMessageId=").append(delayMessageId);
        sb.append(", dataId=").append(dataId);
        sb.append(", payload=").append(payload);
        sb.append('}');
        return sb.toString();
    }
}
