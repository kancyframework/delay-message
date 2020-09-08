package com.kancy.delay.message.db.scheduler;

import java.time.Duration;

/**
 * DelayMessageRef
 *
 * @author kancy
 * @date 2020/7/18 0:49
 */
public class DelayMessageRef<T> {
    /**
     * 延时消息ID
     */
    private String delayMessageId;
    /**
     * 消息key
     */
    private String messageKey;
    /**
     * 延时时间
     */
    private Duration delay;
    /**
     * 消息体
     */
    private T payload;
    /**
     * 链路ID
     */
    private String traceId;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 通知方式
     */
    private String noticeType;
    /**
     * 通知地址（url/channel/beanName）
     */
    private String noticeAddress;
    /**
     * 使用缓存
     */
    private boolean useCache;
    /**
     * 扫描次数
     */
    private int scanTimes;
    /**
     * 最大扫描次数
     */
    private int maxScanTimes;

    public void setDelayMessageId(String delayMessageId) {
        this.delayMessageId = delayMessageId;
    }

    public String getDelayMessageId() {
        return delayMessageId;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
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

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
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

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public int getScanTimes() {
        return scanTimes;
    }

    public void setScanTimes(int scanTimes) {
        this.scanTimes = scanTimes;
    }

    public int getMaxScanTimes() {
        return maxScanTimes;
    }

    public void setMaxScanTimes(int maxScanTimes) {
        this.maxScanTimes = maxScanTimes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("delayMessageId=").append(delayMessageId);
        sb.append(", messageKey='").append(messageKey).append('\'');
        sb.append(", delay=").append(delay);
        sb.append(", payload=").append(payload);
        sb.append(", traceId='").append(traceId).append('\'');
        sb.append(", messageType='").append(messageType).append('\'');
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", dataId='").append(dataId).append('\'');
        sb.append(", noticeType='").append(noticeType).append('\'');
        sb.append(", noticeAddress='").append(noticeAddress).append('\'');
        sb.append(", useCache=").append(useCache);
        sb.append(", scanTimes=").append(scanTimes);
        sb.append(", maxScanTimes=").append(maxScanTimes);
        sb.append('}');
        return sb.toString();
    }
}
