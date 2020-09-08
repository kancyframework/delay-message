package com.github.kancyframework.delay.message.config;

import com.github.kancyframework.delay.message.properties.TaskExecutorProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * DelayMessageTaskExecutor
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/17 13:00
 **/

public class DelayMessageTaskExecutor extends ThreadPoolTaskExecutor {

    private static final int BLOCK_QUEUE_CAPACITY = 0;

    private final TaskExecutorProperties executorProperties;

    public DelayMessageTaskExecutor(TaskExecutorProperties executorProperties) {
        this.executorProperties = executorProperties;
    }

    @Override
    public void afterPropertiesSet(){
        // 不启用阻塞队列进行缓冲
        setQueueCapacity(BLOCK_QUEUE_CAPACITY);
        // 线程数不够时，交给主线程处理，阻塞主线程
        setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        setMaxPoolSize(executorProperties.getTaskPoolSize());
        setCorePoolSize(executorProperties.getTaskPoolSize());
        setThreadNamePrefix(executorProperties.getThreadNamePrefix());
        setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
        setAwaitTerminationSeconds(executorProperties.getAwaitTerminationSeconds());
        setWaitForTasksToCompleteOnShutdown(executorProperties.isWaitForTasksToCompleteOnShutdown());
        Runtime.getRuntime().addShutdownHook(newThread(this::destroy));
        super.afterPropertiesSet();
    }

}