package com.github.kancyframework.delay.message.client.config;

import com.github.kancyframework.delay.message.client.DelayMessageClient;
import com.github.kancyframework.delay.message.client.DelayMessageClientImpl;
import com.github.kancyframework.delay.message.client.interceptor.ClientMetricsInterceptor;
import com.github.kancyframework.delay.message.client.properties.DelayMessageClientProperties;
import com.github.kancyframework.delay.message.config.DelayMessageCoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * DelayMessageClientAutoConfiguration
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/16 14:53
 **/
@EnableConfigurationProperties(DelayMessageClientProperties.class)
@ComponentScan(basePackageClasses = ClientMetricsInterceptor.class)
@AutoConfigureAfter(DelayMessageCoreAutoConfiguration.class)
@Import(DelayMessageCoreAutoConfiguration.class)
public class DelayMessageClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageClient delayMessageClient(){
        return new DelayMessageClientImpl();
    }

    @Bean
    public DelayMessageClientEndpoint delayMessageEndpoint(){
        return new DelayMessageClientEndpoint();
    }

}
