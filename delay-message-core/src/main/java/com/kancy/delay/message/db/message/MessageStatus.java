package com.kancy.delay.message.db.message;

/**
 * MessageStatus
 *
 * @author kancy
 * @date 2020/7/17 0:27
 */
public enum MessageStatus {
    /**
     * 待处理 0
     */
    WAITING,
    /**
     * 处理中 1
     */
    RUNNING,
    /**
     * 成功 2
     */
    SUCCESS,
    /**
     * 失败 3
     */
    FAIL,
    /**
     * 处理超时 4
     */
    TIMEOUT;
}
