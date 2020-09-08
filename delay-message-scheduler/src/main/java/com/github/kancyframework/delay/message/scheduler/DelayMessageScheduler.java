package com.github.kancyframework.delay.message.scheduler;

import com.github.kancyframework.delay.message.message.IDelayMessage;

import java.util.Date;

/**
 * DelayMessageScheduler
 *
 * @author kancy
 * @date 2020/7/15 22:17
 */
public interface DelayMessageScheduler {
    /**
     * 消费延时消息
     * @return
     */
    default int schedule() {
        return schedule(IDelayMessage.DEFAULT_TABLE_NAME);
    }

    /**
     * 消费延时消息
     *
     * @param tableName
     * @return
     */
    int schedule(String tableName);

    /**
     * 消费延时消息
     *
     * @param limit
     * @return
     */
    default int schedule(int limit) {
        return schedule(IDelayMessage.DEFAULT_TABLE_NAME, limit);
    }

    /**
     * 消费延时消息
     *
     * @param tableName
     * @param limit
     * @return
     */
    int schedule(String tableName, int limit);


    /**
     * 消费延时消息
     *
     * @param tableName
     * @param limit
     * @param minScanExpiredTime
     * @return
     */
    int schedule(String tableName, int limit, Date minScanExpiredTime);

}
