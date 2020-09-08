package com.kancy.delay.message.db.scheduler.interceptor;

import com.kancy.delay.message.db.config.TraceContextResolver;
import com.kancy.delay.message.db.interceptor.DelayMessageSchedulerInterceptor;
import com.kancy.delay.message.db.message.TraceDelayMessage;
import com.kancy.delay.message.db.service.DelayMessage;
import com.kancy.delay.message.db.service.DelayMessageConfig;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * <p>
 * TraceInterceptor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 19:12
 **/
@Component
@ConditionalOnBean(TraceContextResolver.class)
public class TraceLinkInterceptor implements DelayMessageSchedulerInterceptor {

    @Autowired
    private TraceContextResolver traceContextResolver;

    /**
     * 调度主线程所持的traceId
     */
    private static final ThreadLocal<String> mainTraceId = new ThreadLocal<>();

    /**
     * 处理消息之前
     *
     * @param entity
     * @param config
     */
    @Override
    public void handleMessageBefore(DelayMessage entity, DelayMessageConfig config) {
        // 切换到客户端消息的traceId
        traceContextResolver.setTraceId(entity.getTraceId());
    }

    /**
     * 处理消息完成
     *
     * @param entity
     * @param config
     * @param exception
     */
    @Override
    public void handleMessageCompleted(DelayMessage entity, DelayMessageConfig config, Exception exception) {
        // 还原到主线程的traceId
        MDC.put(TraceDelayMessage.TRACE_ID_KEY, mainTraceId.get());
    }

    @Override
    public void scheduleBefore(String tableName, int limit) {
        mainTraceId.set(traceContextResolver.getTraceId());
    }

    @Override
    public void scheduleCompleted(String tableName, int limit, int scheduleSize) {
        mainTraceId.remove();
    }

}
