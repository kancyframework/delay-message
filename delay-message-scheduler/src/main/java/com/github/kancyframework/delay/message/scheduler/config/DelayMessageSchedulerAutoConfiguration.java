package com.github.kancyframework.delay.message.scheduler.config;

import com.github.kancyframework.delay.message.config.DelayMessageCoreAutoConfiguration;
import com.github.kancyframework.delay.message.config.DelayMessageTaskExecutor;
import com.github.kancyframework.delay.message.properties.TaskExecutorProperties;
import com.github.kancyframework.delay.message.scheduler.DelayMessageScheduleOptimizer;
import com.github.kancyframework.delay.message.scheduler.DelayMessageScheduler;
import com.github.kancyframework.delay.message.scheduler.DelayMessageSchedulerImpl;
import com.github.kancyframework.delay.message.scheduler.interceptor.SchedulerMetricsInterceptor;
import com.github.kancyframework.delay.message.scheduler.properties.DelayMessageSchedulerProperties;
import com.github.kancyframework.delay.message.service.DelayMessageService;
import com.github.kancyframework.delay.message.scheduler.xxljob.XxlJobConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * DelayMessageSchedulerAutoConfiguration
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 14:55
 **/
@EnableConfigurationProperties({DelayMessageSchedulerProperties.class})
@AutoConfigureAfter(DelayMessageCoreAutoConfiguration.class)
@ComponentScan(basePackageClasses = SchedulerMetricsInterceptor.class)
@Import({DelayMessageHandlerConfig.class, XxlJobConfig.class})
public class DelayMessageSchedulerAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "delay.message.executor")
    @ConditionalOnMissingBean
    public TaskExecutorProperties taskExecutorProperties() {
        return new TaskExecutorProperties();
    }

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public DelayMessageTaskExecutor delayMessageTaskExecutor(TaskExecutorProperties executorProperties) {
        return new DelayMessageTaskExecutor(executorProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageScheduler delayMessageScheduler() {
        return new DelayMessageSchedulerImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageSchedulerEndpoint delayMessageSchedulerEndpoint() {
        return new DelayMessageSchedulerEndpoint();
    }

    /**
     * 调度优化器
     * @param schedulerProperties
     * @param delayMessageService
     * @return
     */
    @Bean
    public DelayMessageScheduleOptimizer delayMessageScheduleOptimizer(DelayMessageSchedulerProperties schedulerProperties,
                                                                       DelayMessageService delayMessageService) {
        return new DelayMessageScheduleOptimizer(schedulerProperties, delayMessageService);
    }

}
