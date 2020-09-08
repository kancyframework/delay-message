package com.github.kancyframework.delay.message.data.mongo.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * DelayMessageConfigRepository
 *
 * @author kancy
 * @date 2020/7/25 13:48
 */
public class DelayMessageConfigRepository {

    private final MongoTemplate mongoTemplate;

    public DelayMessageConfigRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public DelayMessageConfigEntity queryConfigByMessageKey(String messageKey) {
        List<DelayMessageConfigEntity> configList = mongoTemplate.find(
                Query.query(Criteria.where("messageKey").is(messageKey)), DelayMessageConfigEntity.class, "t_delay_message_config");
        if (CollectionUtils.isEmpty(configList)){
            return null;
        }
        return configList.get(0);
    }

    public List<DelayMessageConfigEntity> queryConfigByMessageKeys(Set<String> messageKeys) {
        return mongoTemplate.find(Query.query(Criteria.where("messageKey").in(messageKeys)),
                DelayMessageConfigEntity.class, "t_delay_message_config");
    }
}
