package com.github.kancyframework.delay.message.service;

import java.time.Duration;

/**
 * DelayMessageConfig
 *
 * @author kancy
 * @date 2020/7/25 10:36
 */
public class DelayMessageConfig {
    /**
     * 消息key/消息类型
     */
    private String messageKey;

    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 通知方式
     * HTTP/STREAM/BEAN
     */
    private String noticeType;
    /**
     * 通知地址（url/channel destination/beanName）
     */
    private String noticeAddress;

    /**
     * 使用缓存
     */
    private boolean useCache;

    /**
     * 存活时间
     */
    private String aliveTime;

    /**
     * 最大扫描次数
     */
    private int maxScanTimes;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeAddress() {
        return noticeAddress;
    }

    public void setNoticeAddress(String noticeAddress) {
        this.noticeAddress = noticeAddress;
    }

    public int getMaxScanTimes() {
        return maxScanTimes;
    }

    public void setMaxScanTimes(int maxScanTimes) {
        this.maxScanTimes = maxScanTimes;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public String getAliveTime() {
        return aliveTime;
    }

    public void setAliveTime(String aliveTime) {
        this.aliveTime = aliveTime;
    }
}
