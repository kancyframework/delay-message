package com.github.kancyframework.delay.message.client.interceptor;

import com.github.kancyframework.delay.message.config.DelayMessageMetricsResolver;
import com.github.kancyframework.delay.message.message.IDelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import com.github.kancyframework.delay.message.interceptor.DelayMessageClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * SchedulerMetricsInterceptor
 *
 * @author kancy
 * @date 2020/7/20 23:26
 */
@ConditionalOnBean(DelayMessageMetricsResolver.class)
@Component
public class ClientMetricsInterceptor implements DelayMessageClientInterceptor {

    @Autowired
    private DelayMessageMetricsResolver delayMessageMetricsResolver;

    /**
     * 发送消息之前
     *
     * @param message
     */
    @Override
    public void sendBefore(IDelayMessage message) {
        delayMessageMetricsResolver.putMessageSendOrHandleStartClock();
    }

    /**
     * 发送消息完成
     *
     * @param message
     * @param config
     * @param success
     */
    @Override
    public void sendCompleted(IDelayMessage message, DelayMessageConfig config, boolean success) {
        delayMessageMetricsResolver.sendCompleted(config.getTableName(), message.getMessageKey(), success);
    }

}
