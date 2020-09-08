package com.github.kancyframework.delay.message.config;

import brave.propagation.CurrentTraceContext;
import com.github.kancyframework.delay.message.actuator.DelayMessageMetrics;
import com.github.kancyframework.delay.message.service.DelayMessageConfigService;
import com.github.kancyframework.delay.message.cache.DelayMessageConfigCache;
import com.github.kancyframework.delay.message.health.DelayMessageHealthIndicator;
import com.github.kancyframework.delay.message.properties.DelayMessageProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * DelayMessageCoreAutoConfiguration
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 15:04
 **/
@EnableConfigurationProperties({DelayMessageProperties.class})
@Import({
        DelayMessageCoreAutoConfiguration.TraceConfig.class,
        DelayMessageCoreAutoConfiguration.MetricsConfig.class
})
public class DelayMessageCoreAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public DelayMessageConfigCache delayMessageConfigCache(DelayMessageConfigService delayMessageConfigService,
                                                           DelayMessageProperties properties){
        return new DelayMessageConfigCache(delayMessageConfigService, properties);
    }

    /**
     * 雪花算法
     * @param properties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowIdGenerator snowIdGenerator(DelayMessageProperties properties){
        if (properties.getWorkerId() < 0 || properties.getDatacenterId() < 0){
            return new SnowIdGenerator();
        } else {
            return new SnowIdGenerator(properties.getWorkerId(), properties.getDatacenterId());
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageHealthIndicator delayMessageHealthIndicator(){
        return new DelayMessageHealthIndicator();
    }

    /**
     * TraceConfig
     */
    @ConditionalOnClass({CurrentTraceContext.class, SleuthProperties.class})
    static class TraceConfig {
        @Bean
        public TraceContextResolver traceContextResolver(){
            return new TraceContextResolver();
        }
    }

    /**
     * MetricsConfig
     */
    @ConditionalOnClass({MeterRegistry.class, HealthIndicator.class})
    static class MetricsConfig {

        @Bean
        public DelayMessageMetrics delayMessageMetrics(){
            return new DelayMessageMetrics();
        }

        @Bean
        public DelayMessageMetricsResolver delayMessageMetricsResolver(){
            return new DelayMessageMetricsResolver();
        }
    }

}
