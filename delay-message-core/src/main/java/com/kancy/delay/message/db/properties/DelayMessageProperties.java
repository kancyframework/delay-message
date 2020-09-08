package com.kancy.delay.message.db.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

/**
 * <p>
 * DelayMessageProperties
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 15:26
 **/
@RefreshScope
@ConfigurationProperties(prefix = "delay.message")
public class DelayMessageProperties {
    /**
     * 工作ID
     */
    long workerId = -1L;
    /**
     * 数据仓库ID
     */
    long datacenterId = -1L;

    /**
     * 配置缓存最大个数
     */
    private int configCacheMaxSize = 100;

    /**
     * 配置缓存时间
     */
    private Duration configCacheTime = Duration.ofSeconds(5);

    /**
     * metrics监控
     */
    private boolean metrics = false;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }

    public int getConfigCacheMaxSize() {
        return configCacheMaxSize;
    }

    public void setConfigCacheMaxSize(int configCacheMaxSize) {
        this.configCacheMaxSize = configCacheMaxSize;
    }

    public Duration getConfigCacheTime() {
        return configCacheTime;
    }

    public void setConfigCacheTime(Duration configCacheTime) {
        this.configCacheTime = configCacheTime;
    }

    public boolean isMetrics() {
        return metrics;
    }

    public void setMetrics(boolean metrics) {
        this.metrics = metrics;
    }

}
