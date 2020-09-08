package com.kancy.delay.message.data.jdbc.dao;

import java.util.Date;

/**
 * DelayMessage
 *
 * @author kancy
 * @date 2020/7/15 20:35
 */
public class DelayMessageEntity {
    /**
     * 主键
     */
    private String id;
    /**
     * 消息key/消息类型
     */
    private String messageKey;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 消息状态
     * @see com.kancy.delay.message.db.message.MessageStatus
     */
    private Integer messageStatus;
    /**
     * 扫描次数
     */
    private Integer scanTimes;
    /**
     * 到期时间
     */
    private Date expiredTime;
    /**
     * 链路ID
     */
    private String traceId;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新时间
     */
    private Date updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public Integer getScanTimes() {
        return scanTimes;
    }

    public void setScanTimes(Integer scanTimes) {
        this.scanTimes = scanTimes;
    }

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("id=").append(id);
        sb.append(", messageKey='").append(messageKey).append('\'');
        sb.append(", dataId='").append(dataId).append('\'');
        sb.append(", messageStatus=").append(messageStatus);
        sb.append(", scanTimes=").append(scanTimes);
        sb.append(", expireTime=").append(expiredTime);
        sb.append(", traceId='").append(traceId).append('\'');
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append('}');
        return sb.toString();
    }
}
