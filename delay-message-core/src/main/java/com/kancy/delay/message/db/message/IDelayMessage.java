package com.kancy.delay.message.db.message;

import java.time.Duration;

/**
 * DelayMessage
 *
 * @author kancy
 * @date 2020/7/15 21:35
 */
public interface IDelayMessage {

    String DEFAULT_TABLE_NAME = "t_delay_message";
    String TABLE_NAME = "delayMessageTableName";
    String MESSAGE_KEY = "delayMessageKey";
    String MESSAGE_ID = "delayMessageId";
    String DATA_ID = "delayMessageDataId";
    String MESSAGE_TYPE = "delayMessageMessageType";

    /**
     * 消息key/消息类型
     *
     * @return
     */
    String getMessageKey();

    /**
     * 延时时间
     *
     * @return
     */
    Duration getDelay();

    /**
     * 数据体
     *
     * @return
     */
    Object getPayload();
}
