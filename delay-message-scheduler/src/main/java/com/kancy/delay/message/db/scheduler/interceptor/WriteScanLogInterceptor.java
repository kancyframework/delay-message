package com.kancy.delay.message.db.scheduler.interceptor;

import com.kancy.delay.message.db.service.DelayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
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
        name = {"write-scan-log-enabled"} , havingValue = "true")
public class WriteScanLogInterceptor extends AbstractLogInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WriteScanLogInterceptor.class);

    @Override
    public void scanCompleted(String tableName, int limit, long minExpireTime,
                              long startTime, List<DelayMessage> delayMessageEntities) {
        Date min = null;
        Date max = null;
        if (!CollectionUtils.isEmpty(delayMessageEntities)) {
            min = delayMessageEntities.get(0).getExpiredTime();
            max = delayMessageEntities.get(delayMessageEntities.size() - 1).getExpiredTime();
        }

        char splitChar = schedulerProperties.getLog().getSplitChar();
        StringBuilder sb = new StringBuilder()
                .append(new Timestamp(startTime)).append(splitChar)
                .append(tableName).append(splitChar)
                .append(limit).append(splitChar)
                .append(delayMessageEntities.size()).append(splitChar)
                .append(System.currentTimeMillis() - startTime).append(splitChar)
                .append(new Timestamp(minExpireTime)).append(splitChar)
                .append(min).append(splitChar)
                .append(max);
        log.info("scan log -> {}", sb);
        if (schedulerProperties.getLog().isWriteScanLogEnabled()) {
            sb.append("\r\n");
            writeLog(tableName, sb.toString(), "scan.log");
        }
    }
}
