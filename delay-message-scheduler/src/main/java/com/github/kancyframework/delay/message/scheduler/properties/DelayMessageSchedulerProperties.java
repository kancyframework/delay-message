package com.github.kancyframework.delay.message.scheduler.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

/**
 * DelayMessageSchedulerProperties
 *
 * @author kancy
 * @date 2020/7/17 0:12
 */
@RefreshScope
@ConfigurationProperties(prefix = "delay.message.scheduler")
public class DelayMessageSchedulerProperties {

    /**
     * 执行日志
     */
    private LogConfig log = new LogConfig();

    /**
     * 是否使用配置缓存
     */
    private boolean useConfigCache = true;

    /**
     * 处理超时时间，单位秒
     */
    private int processingTimeout = 180;

    /**
     * 最大扫描次数
     */
    private int maxScanTimes = 10;
    /**
     * 每次批量获取的最大数量
     */
    private int maxPrefetchSize = 2000;
    /**
     * 最大过期差异间隔时间
     */
    private Duration maxExpireDiscrepancyIntervalTime = Duration.ofDays(3);
    /**
     * 最小刷新最小的到期时间的时间
     */
    private Duration minRefreshMinExpiredTime = Duration.ofMinutes(10);

    /**
     * 最大刷新最小的到期时间的时间
     */
    private Duration maxRefreshMinExpiredTime = Duration.ofMinutes(30);
    /**
     * 重置消息状态的最小时间
     */
    private Duration minResetMessageStatusTime = Duration.ofMinutes(10);

    /**
     * 重置消息状态的最大时间
     */
    private Duration maxResetMessageStatusTime = Duration.ofMinutes(30);


    public boolean isUseConfigCache() {
        return useConfigCache;
    }

    public void setUseConfigCache(boolean useConfigCache) {
        this.useConfigCache = useConfigCache;
    }

    public int getProcessingTimeout() {
        return processingTimeout;
    }

    public void setProcessingTimeout(int processingTimeout) {
        this.processingTimeout = processingTimeout;
    }

    public int getMaxScanTimes() {
        return maxScanTimes;
    }

    public void setMaxScanTimes(int maxScanTimes) {
        this.maxScanTimes = maxScanTimes;
    }

    public int getMaxPrefetchSize() {
        return maxPrefetchSize;
    }

    public void setMaxPrefetchSize(int maxPrefetchSize) {
        this.maxPrefetchSize = maxPrefetchSize;
    }

    public Duration getMinResetMessageStatusTime() {
        return minResetMessageStatusTime;
    }

    public void setMinResetMessageStatusTime(Duration minResetMessageStatusTime) {
        this.minResetMessageStatusTime = minResetMessageStatusTime;
    }

    public Duration getMaxResetMessageStatusTime() {
        return maxResetMessageStatusTime;
    }

    public void setMaxResetMessageStatusTime(Duration maxResetMessageStatusTime) {
        this.maxResetMessageStatusTime = maxResetMessageStatusTime;
    }

    public Duration getMaxExpireDiscrepancyIntervalTime() {
        return maxExpireDiscrepancyIntervalTime;
    }

    public void setMaxExpireDiscrepancyIntervalTime(Duration maxExpireDiscrepancyIntervalTime) {
        this.maxExpireDiscrepancyIntervalTime = maxExpireDiscrepancyIntervalTime;
    }

    public Duration getMinRefreshMinExpiredTime() {
        return minRefreshMinExpiredTime;
    }

    public void setMinRefreshMinExpiredTime(Duration minRefreshMinExpiredTime) {
        this.minRefreshMinExpiredTime = minRefreshMinExpiredTime;
    }

    public Duration getMaxRefreshMinExpiredTime() {
        return maxRefreshMinExpiredTime;
    }

    public void setMaxRefreshMinExpiredTime(Duration maxRefreshMinExpiredTime) {
        this.maxRefreshMinExpiredTime = maxRefreshMinExpiredTime;
    }

    public LogConfig getLog() {
        return log;
    }

    public void setLog(LogConfig log) {
        this.log = log;
    }

    public static class LogConfig {
        /**
         * 是否开启执行日志
         */
        private boolean writeScanLogEnabled = false;
        /**
         * 是否开启执行日志
         */
        private boolean writeScheduleLogEnabled = false;
        /**
         * 是否分表
         */
        private boolean splitTable = true;
        /**
         * 分隔符
         */
        private char splitChar = '|';
        /**
         * 日志目录
         */
        private String logDir = String.format("%s/delay_message_db/", System.getProperty("user.home"));

        public boolean isSplitTable() {
            return splitTable;
        }

        public void setSplitTable(boolean splitTable) {
            this.splitTable = splitTable;
        }

        public char getSplitChar() {
            return splitChar;
        }

        public void setSplitChar(char splitChar) {
            this.splitChar = splitChar;
        }

        public String getLogDir() {
            return logDir;
        }

        public void setLogDir(String logDir) {
            this.logDir = logDir;
        }

        public boolean isWriteScanLogEnabled() {
            return writeScanLogEnabled;
        }

        public void setWriteScanLogEnabled(boolean writeScanLogEnabled) {
            this.writeScanLogEnabled = writeScanLogEnabled;
        }

        public boolean isWriteScheduleLogEnabled() {
            return writeScheduleLogEnabled;
        }

        public void setWriteScheduleLogEnabled(boolean writeScheduleLogEnabled) {
            this.writeScheduleLogEnabled = writeScheduleLogEnabled;
        }
    }
}
