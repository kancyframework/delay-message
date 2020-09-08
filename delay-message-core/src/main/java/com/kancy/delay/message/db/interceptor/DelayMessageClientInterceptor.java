package com.kancy.delay.message.db.interceptor;

import com.kancy.delay.message.db.message.IDelayMessage;
import com.kancy.delay.message.db.service.DelayMessageConfig;

/**
 * <p>
 * DelayMessageInterceptor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 13:45
 **/

public interface DelayMessageClientInterceptor {
    /**
     * 发送消息之前
     * @param message
     */
    default void sendBefore(IDelayMessage message){

    }

    /**
     * 发送消息之前
     * @param message
     */
    default void sendAfter(IDelayMessage message){

    }

    /**
     * 发送消息完成
     * @param message
     * @param config
     * @param success
     */
    default void sendCompleted(IDelayMessage message, DelayMessageConfig config, boolean success){

    }
}
