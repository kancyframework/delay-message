package com.github.kancyframework.delay.message.message;

import org.slf4j.MDC;

import java.time.Duration;
import java.util.Objects;

/**
 * CustomDelayMessage
 *
 * @author kancy
 * @date 2020/7/15 22:57
 */
public class CustomTraceDelayMessage<T> extends CustomDelayMessage<T> implements TraceDelayMessage {

    /**
     * 链路ID
     */
    private String traceId;

    public CustomTraceDelayMessage() {
        setTraceId(MDC.get(TRACE_ID_KEY));
    }

    public CustomTraceDelayMessage(String messageKey, Duration delay, T payload) {
        super(messageKey, delay, payload);
        setTraceId(MDC.get(TRACE_ID_KEY));
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        if (Objects.isNull(traceId)) {
            return super.toString();
        }
        final StringBuilder sb = new StringBuilder("{");
        sb.append("messageKey='").append(getMessageKey()).append('\'');
        sb.append(", delay=").append(getDelay());
        sb.append(", payload=").append(getPayload());
        sb.append("traceId='").append(traceId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
