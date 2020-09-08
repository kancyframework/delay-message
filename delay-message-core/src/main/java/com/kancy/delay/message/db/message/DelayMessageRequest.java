package com.kancy.delay.message.db.message;

import java.io.Serializable;

/**
 * DelayMessageRequest
 *
 * @author kancy
 * @date 2020/7/22 21:22
 */
public class DelayMessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 延时消息ID
     */
    private String delayMessageId;
    /**
     * 延时消息表名
     */
    private String delayMessageTableName;
    /**
     * 延时消息KEY
     */
    private String delayMessageKey;
    /**
     * 延时消息数据ID
     */
    private String delayMessageDataId;
    /**
     * 延时消息类型
     */
    private String delayMessageMessageType;

    public String getDelayMessageId() {
        return delayMessageId;
    }

    public void setDelayMessageId(String delayMessageId) {
        this.delayMessageId = delayMessageId;
    }

    public String getDelayMessageTableName() {
        return delayMessageTableName;
    }

    public void setDelayMessageTableName(String delayMessageTableName) {
        this.delayMessageTableName = delayMessageTableName;
    }

    public String getDelayMessageKey() {
        return delayMessageKey;
    }

    public void setDelayMessageKey(String delayMessageKey) {
        this.delayMessageKey = delayMessageKey;
    }

    public String getDelayMessageDataId() {
        return delayMessageDataId;
    }

    public void setDelayMessageDataId(String delayMessageDataId) {
        this.delayMessageDataId = delayMessageDataId;
    }

    public String getDelayMessageMessageType() {
        return delayMessageMessageType;
    }

    public void setDelayMessageMessageType(String delayMessageMessageType) {
        this.delayMessageMessageType = delayMessageMessageType;
    }
}
