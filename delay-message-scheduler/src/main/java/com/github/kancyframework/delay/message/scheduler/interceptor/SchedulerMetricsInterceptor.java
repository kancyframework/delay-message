package com.github.kancyframework.delay.message.scheduler.interceptor;

import com.github.kancyframework.delay.message.actuator.DelayMessageMetrics;
import com.github.kancyframework.delay.message.config.DelayMessageMetricsResolver;
import com.github.kancyframework.delay.message.interceptor.DelayMessageSchedulerInterceptor;
import com.github.kancyframework.delay.message.service.DelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * SchedulerMetricsInterceptor
 *
 * @author kancy
 * @date 2020/7/20 23:26
 */
@ConditionalOnBean(DelayMessageMetrics.class)
@Component
public class SchedulerMetricsInterceptor implements DelayMessageSchedulerInterceptor {

    @Autowired
    private DelayMessageMetricsResolver delayMessageMetricsResolver;

    @Override
    public void scheduleBefore(String tableName, int limit) {
        delayMessageMetricsResolver.putSchedulerScheduleStartClock();
    }

    @Override
    public void scheduleCompleted(String tableName, int limit, int scheduleSize) {
        delayMessageMetricsResolver.scheduler(tableName);
    }

    @Override
    public void handleMessageBefore(DelayMessage entity, DelayMessageConfig config) {
        delayMessageMetricsResolver.putMessageSendOrHandleStartClock();
    }

    @Override
    public void handleMessageCompleted(DelayMessage entity, DelayMessageConfig config, Exception exception) {
        delayMessageMetricsResolver.scheduled(config.getTableName(), config.getMessageKey(), Objects.isNull(exception));
    }
}
