package com.kancy.delay.message.db.service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * DelayMessageStoreService
 *
 * @author kancy
 * @date 2020/7/25 9:59
 */
public interface DelayMessageService {

    /**
     * 存储消息
     * @param info
     */
    void storeDelayMessage(String partitionName, DelayMessage info);

    /**
     * 扫描到期的消息
     * @param tableName
     * @param minExpireTime
     * @param limit
     * @return
     */
    List<DelayMessage> scanExpiredMessage(String tableName, long minExpireTime, int limit);

    /**
     * 更新状态
     * @param partitionName
     * @param messageId
     * @param status
     */
    void updateStatus(String partitionName, String messageId, int status);

    /**
     * 批量更新状态
     * @param partitionName
     * @param messageIds
     * @param fromStatus
     * @param toStatus
     */
    void batchUpdateStatus(String partitionName, List<String> messageIds, int fromStatus, int toStatus);

    /**
     * 批量更新为处理中
     * @param partitionName
     * @param messageIds
     */
    void batchUpdateOnProcessing(String partitionName, List<String> messageIds);

    /**
     * 获取超时执行的消息id
     * @param partitionName
     * @param lastTime
     * @param recentSeconds 最近多少秒内的数据
     * @return
     */
    List<String> findAllExecuteTimeoutMessageIds(String partitionName, Duration lastTime, long recentSeconds);

    /**
     * 找到最小的到期时间
     * @param partitionName
     * @param recentSeconds 最近多少秒内的数据
     * @return
     */
    Date findMinExpiredTime(String partitionName, long recentSeconds);
}
