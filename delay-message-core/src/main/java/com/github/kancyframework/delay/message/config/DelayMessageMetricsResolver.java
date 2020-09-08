package com.github.kancyframework.delay.message.config;

import com.github.kancyframework.delay.message.actuator.DelayMessageMetrics;
import com.github.kancyframework.delay.message.properties.DelayMessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * <p>
 * DelayMessageMetricsResolver
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/19 11:56
 **/

public class DelayMessageMetricsResolver {
    private static final Logger log = LoggerFactory.getLogger(DelayMessageMetricsResolver.class);

    @Autowired(required = false)
    private DelayMessageMetrics delayMessageMetrics;

    @Autowired
    private DelayMessageProperties properties;


    public void putMessageSendOrHandleStartClock(){
        if (isMetrics()){
            delayMessageMetrics.putMessageSendOrHandleStartClock();
        }
    }

    public void putSchedulerScheduleStartClock(){
        if (isMetrics()){
            delayMessageMetrics.putSchedulerScheduleStartClock();
        }
    }

    public void scheduler(String tableName){
        if (isMetrics()){
            try {
                delayMessageMetrics.scheduler(tableName);
            } catch (Exception e) {
                log.warn("delay message scheduler actuator , {}", e.getMessage());
            }
        }
    }

    public void scheduled(String tableName, String messageKey, boolean result){
        if (isMetrics()){
            try {
                delayMessageMetrics.scheduled(tableName, messageKey, result);
            } catch (Exception e) {
                log.warn("delay message scheduled actuator , {}", e.getMessage());
            }
        }
    }

    public void sendCompleted(String tableName, String messageKey, boolean result){
        if (isMetrics()){
            try {
                delayMessageMetrics.sendCompleted(tableName, messageKey, result);
            } catch (Exception e) {
                log.warn("delay message sendCompleted actuator , {}", e.getMessage());
            }
        }
    }

    private boolean isMetrics() {
        return properties.isMetrics() && Objects.nonNull(delayMessageMetrics);
    }

}
