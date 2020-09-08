package com.kancy.delay.message.db.client;

import com.kancy.delay.message.db.message.IDelayMessage;

/**
 * DelayMessageClient
 *
 * @author kancy
 * @date 2020/7/15 20:28
 */
public interface DelayMessageClient {
    /**
     * 生产延时消息
     * @param message
     * @return
     */
    boolean send(IDelayMessage message);
}
