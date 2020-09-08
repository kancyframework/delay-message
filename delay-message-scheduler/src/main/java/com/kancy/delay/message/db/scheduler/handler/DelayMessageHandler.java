package com.kancy.delay.message.db.scheduler.handler;

import com.kancy.delay.message.db.scheduler.DelayMessageRef;

/**
 * DelayMessageHandler
 *
 * @author kancy
 * @date 2020/7/15 23:03
 */
public interface DelayMessageHandler<T> {

    /**
     * 处理消息
     * @param ref
     */
    default void handle(DelayMessageRef<T> ref) {

    }
}
