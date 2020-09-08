package com.kancy.delay.message.data.mongo.repository;

import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DelayMessageEntity
 *
 * @author kancy
 * @date 2020/7/25 13:56
 */
public class DelayMessageEntity {

    /**
     * 主键
     */
    @Id
    private String id;
    /**
     * 消息key
     */
    private String messageKey;
    /**
     * 延时时间
     */
    private Duration delay;
    /**
     * 数据ID
     */
    private String dataId;
    /**
     * 数据
     */
    private String payload;
    /**
     * 追踪ID
     */
    private String traceId;
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
     * 过期时间
     */
    private Date expiredTime;
    /**
     * 删除时间
     */
    private Date deletedTime;
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

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
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

    public Date getDeletedTime() {
        return deletedTime;
    }

    public void setDeletedTime(Date deletedTime) {
        this.deletedTime = deletedTime;
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
}
