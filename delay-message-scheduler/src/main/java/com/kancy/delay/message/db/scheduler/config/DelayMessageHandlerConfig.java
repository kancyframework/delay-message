package com.kancy.delay.message.db.scheduler.config;

import com.kancy.delay.message.db.scheduler.handler.*;
import com.kancy.delay.message.db.scheduler.handler.processor.LogBeanProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

/**
 * DelayMessageHandlerConfig
 *
 * @author kancy
 * @date 2020/7/18 0:24
 */
@Import({DelayMessageHandlerConfig.HttpHandlerConfig.class,
        DelayMessageHandlerConfig.StreamHandlerConfig.class})
class DelayMessageHandlerConfig {

    @Bean
    public DelayMessageHandler<String> delayMessageHandler(){
        return new DelayMessageHandlerImpl();
    }

    @Bean
    public DelayMessageBeanHandler delayMessageBeanHandler(){
        return new DelayMessageBeanHandler();
    }
    @Bean
    public LogBeanProcessor logBeanProcessor(){
        return new LogBeanProcessor();
    }

    /**
     * Spring Cloud Stream
     */
    @ConditionalOnClass(BinderAwareChannelResolver.class)
    static class StreamHandlerConfig {
        @Bean
        public DelayMessageStreamHandler delayMessageStreamHandler(BinderAwareChannelResolver binderAwareChannelResolver){
            return new DelayMessageStreamHandler(new DynamicChannelResolver(binderAwareChannelResolver));
        }
    }

    /**
     * RestTemplate
     */
    @ConditionalOnClass(RestTemplate.class)
    static class HttpHandlerConfig  {
        @Bean
        @ConditionalOnMissingBean
        public RestTemplate restTemplate(){
            return new RestTemplate();
        }

        @Bean
        public DelayMessageHttpHandler delayMessageHttpHandler(RestTemplate restTemplate){
            return new DelayMessageHttpHandler(restTemplate);
        }
    }
}
