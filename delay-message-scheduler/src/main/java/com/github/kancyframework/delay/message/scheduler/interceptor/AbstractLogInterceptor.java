package com.github.kancyframework.delay.message.scheduler.interceptor;

import com.github.kancyframework.delay.message.interceptor.DelayMessageSchedulerInterceptor;
import com.github.kancyframework.delay.message.scheduler.properties.DelayMessageSchedulerProperties;
import net.dreamlu.mica.core.utils.$;
import net.dreamlu.mica.core.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Date;

/**
 * <p>
 * AbstractLogInterceptor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 19:21
 **/

public abstract class AbstractLogInterceptor implements DelayMessageSchedulerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AbstractLogInterceptor.class);

    @Autowired
    protected DelayMessageSchedulerProperties schedulerProperties;

    /**
     * 写执行日志
     *
     * @param tableName
     * @param logLineData
     * @param fileName
     */
    protected void writeLog(String tableName, String logLineData, String fileName) {
        try {
            String logFilePath = null;
            String dateFormat = $.format(new Date(), "yyyyMM");
            if (schedulerProperties.getLog().isSplitTable()) {
                logFilePath = String.format("%s/%s/%s_%s", schedulerProperties.getLog().getLogDir(), dateFormat, tableName, fileName);
            } else {
                logFilePath = String.format("%s/%s/%s", schedulerProperties.getLog().getLogDir(), dateFormat, fileName);
            }

            File logFile = new File(logFilePath.replace("\\", "/").replaceAll("/+", "/"));
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
                log.info("create {}Log file : {}", fileName, logFile.getAbsolutePath());
            }
            FileUtil.writeToFile(logFile, logLineData, true);
        } catch (Exception e) {
            log.error(String.format("写执行[%s]日志失败", fileName), e);
        }
    }
}
