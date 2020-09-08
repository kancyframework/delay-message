package com.kancy.delay.message.db.config;

import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import zipkin2.internal.HexCodec;

import java.util.Objects;

/**
 * TraceContextResolver
 *
 * @author kancy
 * @date 2020/7/18 12:02
 */
public class TraceContextResolver {
    private static final Logger log = LoggerFactory.getLogger(TraceContextResolver.class);

    @Autowired
    private CurrentTraceContext traceContext;

    public void setTraceId(String traceId){
        if (Objects.isNull(traceId)){
            return;
        }
        try {
            TraceContext trace = this.traceContext.get();
            TraceContext newTrace = TraceContext.newBuilder().traceId(HexCodec.lowerHexToUnsignedLong(traceId))
                    .spanId(trace.spanId())
                    .traceIdHigh(trace.traceIdHigh())
                    .parentId(trace.parentId())
                    .debug(trace.debug())
                    .shared(trace.shared())
                    .extra(trace.extra())
                    .sampled(trace.sampled())
                    .sampledLocal(trace.sampledLocal())
                    .build();
            traceContext.newScope(newTrace);
        } catch (Exception e) {
            log.warn("Reset message traceId fail : {}", e.getMessage());
        }
    }

    public String getTraceId(){
        TraceContext context = this.traceContext.get();
        if (Objects.nonNull(context)){
            return String.valueOf(this.traceContext.get().traceIdString());
        }
        return null;
    }
}
