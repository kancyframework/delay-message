package com.kancy.delay.message.db.message;

/**
 * DelayMessageAware
 *
 * @author kancy
 * @date 2020/7/17 23:20
 */
public interface DelayMessageAware {

    /**
     * 设置延时消息ID
     *
     * @param delayMessageId
     */
    default void setDelayMessageId(String delayMessageId){

    }

    /**
     * 设置延时消息数据ID
     *
     * @param dataId
     */
    default void setDataId(String dataId){

    }

    /**
     * 设置表名
     *
     * @param tableName
     */
    default void setTableName(String tableName){

    }
}
