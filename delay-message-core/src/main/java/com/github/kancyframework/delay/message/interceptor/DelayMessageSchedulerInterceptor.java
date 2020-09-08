package com.github.kancyframework.delay.message.interceptor;

import com.github.kancyframework.delay.message.service.DelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;

import java.util.List;

/**
 * <p>
 * DelayMessageInterceptor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 13:45
 **/

public interface DelayMessageSchedulerInterceptor {
    /**
     * 调度之前
     * @param tableName
     * @param limit
     */
    default void scheduleBefore(String tableName, int limit){

    }

    /**
     * 调度之后
     * @param tableName
     * @param limit
     * @param startTime
     * @param entityList
     * @param asyncExec
     * @param failCount
     */
    default void scheduleAfter(String tableName, int limit, long startTime,
                               List<DelayMessage> entityList, boolean asyncExec, int failCount){

    }
    /**
     * 调度完成
     * @param tableName
     * @param limit
     * @param scheduleSize
     */
    default void scheduleCompleted(String tableName, int limit, int scheduleSize){

    }

    /**
     * 扫描完成
     * @param tableName
     * @param limit
     * @param minExpireTime
     * @param startTime
     * @param delayMessageEntities
     */
    default void scanCompleted(String tableName, int limit, long minExpireTime, long startTime,
                               List<DelayMessage> delayMessageEntities){

    }

    /**
     * 处理消息之前
     * @param entity
     * @param config
     */
    default void handleMessageBefore(DelayMessage entity, DelayMessageConfig config){

    }

    /**
     * 处理消息完成
     * @param entity
     * @param config
     * @param exception
     */
    default void handleMessageCompleted(DelayMessage entity, DelayMessageConfig config, Exception exception){

    }


}
