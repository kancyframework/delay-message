package com.github.kancyframework.delay.message.scheduler.handler;

import com.github.kancyframework.delay.message.scheduler.DelayMessageRef;

/**
 * BeanHandler
 *
 * @author kancy
 * @date 2020/7/18 0:15
 */
public interface DelayMessageBeanProcessor<T> {
    /**
     * 处理
     * @param ref
     */
    void process(DelayMessageRef<T> ref);
}
