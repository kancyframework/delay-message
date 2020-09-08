package com.kancy.delay.message.db.properties;

/**
 * <p>
 * TaskExecutorProperties
 * <p>
 *
 * @author: kancy
 * @date: 2020/3/5 14:28
 **/

public class TaskExecutorProperties {
    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "delay-task-";

    /**
     * 任务池大小（核心线程数=最大线程数）
     */
    private Integer taskPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
    /**
     * 活跃时间
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 等待线程池的任务处理完成再关闭进程
     */
    private boolean waitForTasksToCompleteOnShutdown = false;

    /**
     * 系统关闭前等待线程处理最大时间，单位秒
     */
    private Integer awaitTerminationSeconds = 10;

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Integer getTaskPoolSize() {
        return taskPoolSize;
    }

    public void setTaskPoolSize(Integer taskPoolSize) {
        this.taskPoolSize = taskPoolSize;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    public Integer getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(Integer awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }
}
