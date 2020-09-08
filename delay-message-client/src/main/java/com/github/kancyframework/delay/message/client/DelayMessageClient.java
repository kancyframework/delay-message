package com.github.kancyframework.delay.message.client;

import com.github.kancyframework.delay.message.message.IDelayMessage;

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
