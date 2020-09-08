package com.github.kancyframework.delay.message.message;

/**
 * TraceDelayMessage
 *
 * @author kancy
 * @date 2020/7/17 22:26
 */
public interface TraceDelayMessage extends IDelayMessage {
    String TRACE_ID_KEY = "X-B3-TraceId";

    /**
     * 追踪ID
     *
     * @return
     */
    String getTraceId();
}
