package com.kancy.delay.message.db.scheduler.interceptor;

import com.kancy.delay.message.db.actuator.DelayMessageMetrics;
import com.kancy.delay.message.db.config.DelayMessageMetricsResolver;
import com.kancy.delay.message.db.interceptor.DelayMessageSchedulerInterceptor;
import com.kancy.delay.message.db.service.DelayMessage;
import com.kancy.delay.message.db.service.DelayMessageConfig;
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
