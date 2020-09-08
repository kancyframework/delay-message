package com.github.kancyframework.delay.message.scheduler.interceptor;

import com.github.kancyframework.delay.message.service.DelayMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * WriteExecLogInterceptor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 19:20
 **/
@Component
@ConditionalOnProperty(prefix = "delay.message.scheduler.log",
        name = {"write-schedule-log-enabled"} , havingValue = "true")
public class WriteScheduleLogInterceptor extends AbstractLogInterceptor {

    @Override
    public void scheduleAfter(String tableName, int limit, long startTime,
                              List<DelayMessage> entityList, boolean asyncExec, int failCount) {

        if (schedulerProperties.getLog().isWriteScheduleLogEnabled()) {
            long sumTime = System.currentTimeMillis() - startTime;
            int messageSize = entityList.size();
            char splitChar = schedulerProperties.getLog().getSplitChar();
            StringBuilder execLog = new StringBuilder()
                    .append(new Timestamp(startTime)).append(splitChar)
                    .append(tableName).append(splitChar)
                    .append(asyncExec).append(splitChar)
                    .append(limit).append(splitChar)
                    .append(messageSize).append(splitChar)
                    .append(failCount).append(splitChar)
                    .append(sumTime).append(splitChar)
                    .append(sumTime / messageSize)
                    .append("\r\n");

            // 序列化数据到本地文件
            writeLog(tableName, execLog.toString(), "schedule.log");
        }
    }
}
