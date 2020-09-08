package com.github.kancyframework.delay.message.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

/**
 * DelayMessageClientProperties
 *
 * @author kancy
 * @date 2020/7/17 0:12
 */
@RefreshScope
@ConfigurationProperties(prefix = "delay.message.client")
public class DelayMessageClientProperties {
    /**
     * 消息保留的时间
     */
    private Duration messageAliveTime = Duration.ofDays(30);

    /**
     * 网络消耗浪费的时间
     */
    private long wasteTime = 0L;

    /**
     * 是否使用配置缓存
     */
    private boolean useConfigCache = true;

    /**
     * 使用雪花算法生成数据ID，否则使用UUID
     */
    private boolean useSnowDataId = true;
    /**
     * 使用雪花算法生成消息ID，否则使用UUID
     */
    private boolean useSnowMessageId = true;

    public Duration getMessageAliveTime() {
        return messageAliveTime;
    }

    public void setMessageAliveTime(Duration messageAliveTime) {
        this.messageAliveTime = messageAliveTime;
    }

    public long getWasteTime() {
        return wasteTime;
    }

    public void setWasteTime(long wasteTime) {
        this.wasteTime = wasteTime;
    }

    public boolean isUseConfigCache() {
        return useConfigCache;
    }

    public void setUseConfigCache(boolean useConfigCache) {
        this.useConfigCache = useConfigCache;
    }

    public boolean isUseSnowDataId() {
        return useSnowDataId;
    }

    public void setUseSnowDataId(boolean useSnowDataId) {
        this.useSnowDataId = useSnowDataId;
    }

    public boolean isUseSnowMessageId() {
        return useSnowMessageId;
    }

    public void setUseSnowMessageId(boolean useSnowMessageId) {
        this.useSnowMessageId = useSnowMessageId;
    }
}
