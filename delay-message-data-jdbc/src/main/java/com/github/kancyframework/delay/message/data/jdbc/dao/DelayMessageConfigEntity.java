package com.github.kancyframework.delay.message.data.jdbc.dao;

import java.time.Duration;

/**
 * DelayMessageConfigEntity
 *
 * @author kancy
 * @date 2020/7/15 20:35
 */
public class DelayMessageConfigEntity {
    /**
     * 主键
     */
    private Long id;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public int getMaxScanTimes() {
        return maxScanTimes;
    }

    public void setMaxScanTimes(int maxScanTimes) {
        this.maxScanTimes = maxScanTimes;
    }

    public String getAliveTime() {
        return aliveTime;
    }

    public void setAliveTime(String aliveTime) {
        this.aliveTime = aliveTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("id=").append(id);
        sb.append(", messageKey='").append(messageKey).append('\'');
        sb.append(", messageType='").append(messageType).append('\'');
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", noticeType='").append(noticeType).append('\'');
        sb.append(", noticeAddress='").append(noticeAddress).append('\'');
        sb.append(", useCache=").append(useCache);
        sb.append(", maxScanTimes=").append(maxScanTimes);
        sb.append('}');
        return sb.toString();
    }
}
