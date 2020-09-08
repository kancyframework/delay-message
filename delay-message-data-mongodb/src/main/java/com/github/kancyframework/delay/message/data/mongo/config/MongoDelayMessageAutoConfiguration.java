package com.github.kancyframework.delay.message.data.mongo.config;

import com.github.kancyframework.delay.message.data.mongo.repository.DelayMessageConfigRepository;
import com.github.kancyframework.delay.message.data.mongo.repository.DelayMessageRepository;
import com.github.kancyframework.delay.message.data.mongo.service.MongoDelayMessageConfigService;
import com.github.kancyframework.delay.message.data.mongo.service.MongoDelayMessageService;
import com.github.kancyframework.delay.message.health.DelayMessageHealthIndicator;
import com.mongodb.MongoClientURI;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.mongo.MongoHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;
import java.util.Objects;

/**
 * MongoDelayMessageAutoConfiguration
 *
 * @author kancy
 * @date 2020/7/25 14:34
 */
@EnableConfigurationProperties(MongoDbProperties.class)
@Import({MongoDelayMessageConfigService.class, MongoDelayMessageService.class})
public class MongoDelayMessageAutoConfiguration {

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Autowired(required = false)
    private DelayMessageHealthIndicator delayMessageHealthIndicator;

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageConfigRepository delayMessageConfigRepository(MongoDbProperties properties){
        MongoDbFactory mongoDbFactory = getMongoDbFactory(properties);
        return new DelayMessageConfigRepository(new MongoTemplate(mongoDbFactory, removeClassMappingMongoConverter(mongoDbFactory)));
    }

    @Bean
    @ConditionalOnMissingBean
    public DelayMessageRepository delayQueueRepository(MongoDbProperties properties){
        MongoDbFactory mongoDbFactory = getMongoDbFactory(properties);
        MongoTemplate simpleMongoTemplate = new MongoTemplate(mongoDbFactory, removeClassMappingMongoConverter(mongoDbFactory));
        addMongoHealthIndicator(simpleMongoTemplate);
        return new DelayMessageRepository(simpleMongoTemplate);
    }

    /**
     * 获取MongoDbFactory
     * @param properties
     * @return
     */
    private MongoDbFactory getMongoDbFactory(MongoDbProperties properties) {
        MongoDbFactory mongoDbFactory = null;
        if (StringUtils.isEmpty(properties.getUri()) && Objects.nonNull(mongoTemplate)) {
            mongoDbFactory = this.mongoTemplate.getMongoDbFactory();
        } else {
            mongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(properties.getUri()));
        }
        return mongoDbFactory;
    }

    /**
     * 移除 _class
     * @param factory
     * @return
     */
    private static MongoConverter removeClassMappingMongoConverter(MongoDbFactory factory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());
        MongoMappingContext mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(conversions);
        // Don't save _class to mongo
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        converter.afterPropertiesSet();
        return converter;
    }

    private void addMongoHealthIndicator(MongoTemplate mongoTemplate) {
        if (Objects.nonNull(delayMessageHealthIndicator)){
            delayMessageHealthIndicator.addHealthIndicator(new MongoHealthIndicator(mongoTemplate));
        }
    }

}
