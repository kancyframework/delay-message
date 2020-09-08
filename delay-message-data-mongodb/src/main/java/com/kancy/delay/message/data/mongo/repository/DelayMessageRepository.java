package com.kancy.delay.message.data.mongo.repository;

import com.kancy.delay.message.db.message.MessageStatus;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DelayMessageRepository
 *
 * @author kancy
 * @date 2020/7/25 13:58
 */
public class DelayMessageRepository {

    private final MongoTemplate mongoTemplate;

    private static Set<String> createIndexCollectionNames = new HashSet<>();

    public DelayMessageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(String collectionName, DelayMessageEntity entity) {
        mongoTemplate.save(entity, collectionName);
    }

    public List<DelayMessageEntity> scanExpiredMessage(String collectionName, long minExpireTime, int limit) {
        createIndex(collectionName);
        return mongoTemplate.find(
                Query.query(Criteria.where("messageStatus").in(MessageStatus.WAITING.ordinal(), MessageStatus.TIMEOUT.ordinal())
                        .andOperator(Criteria.where("expiredTime").gte(new Date(minExpireTime)).lte(new Date()))
                ).with(Sort.by(Sort.Direction.ASC, "expiredTime")).limit(limit), DelayMessageEntity.class, collectionName);
    }

    public void updateStatus(String collectionName, String messageId, int status) {
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(messageId)),
                Update.update("messageStatus", status)
                        .set("updatedTime",new Date()),
                collectionName);
    }

    public void batchUpdateStatus(String collectionName, List<String> messageIds, int fromStatus, int toStatus) {
        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(messageIds).and("messageStatus").is(fromStatus)),
                Update.update("messageStatus", toStatus)
                        .set("updatedTime",new Date()),
                collectionName);
    }

    public void batchUpdateOnProcessing(String collectionName, List<String> messageIds) {
        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").in(messageIds)),
                Update.update("messageStatus", MessageStatus.RUNNING.ordinal())
                        .set("updatedTime",new Date())
                        .inc("scanTimes", 1),
                collectionName);
    }

    public List<String> findAllProcessingMessageIdsWithTimeoutAndTimeRange(String collectionName, Duration duration, long recentSeconds) {
        Date minExpireTime = new Date(System.currentTimeMillis() - recentSeconds * 1000);
        Date minUpdateTime = new Date(System.currentTimeMillis() - duration.toMillis());
        List<String> ids = new ArrayList<>();
        mongoTemplate.executeQuery(
                Query.query(Criteria.where("expiredTime").gte(minExpireTime).lte(new Date())
                        .and("messageStatus").is(MessageStatus.RUNNING.ordinal())
                        .and("updatedTime").lt(minUpdateTime)
                ), collectionName, document -> ids.add(document.getString("_id")));
        return ids;
    }

    public Date findMinExpireTime(String collectionName, long recentSeconds) {
        Date minTime = new Date(System.currentTimeMillis() - recentSeconds * 1000);
        List<DelayMessageEntity> list = mongoTemplate.find(
                Query.query(Criteria.where("expiredTime").gte(minTime).lte(new Date())
                        .and("messageStatus").nin(MessageStatus.SUCCESS.ordinal(), MessageStatus.FAIL.ordinal())
                ).with(Sort.by(Sort.Direction.ASC, "expiredTime")).limit(1), DelayMessageEntity.class, collectionName);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0).getExpiredTime();
    }

    /**
     * 自动创建索引
     * @param collectionName
     */
    private void createIndex(String collectionName) {
        if (createIndexCollectionNames.contains(collectionName)){
            return;
        }

        ListIndexesIterable<Document> documents = mongoTemplate.getCollection(collectionName).listIndexes();
        for (Document document : documents) {
            Object key = document.get("key");
            if (Objects.nonNull(key) && key instanceof Document) {
                Document keyDocument = (Document) key;
                if (keyDocument.containsKey("expiredTime") ||
                        keyDocument.containsKey("createdTime") ||
                        keyDocument.containsKey("deletedTime")) {
                    return;
                }
            }
        }
        mongoTemplate.getCollection(collectionName)
                .createIndex(new Document("expiredTime", 1),
                        new IndexOptions().background(true).name("idx_expiredTime"));
        mongoTemplate.getCollection(collectionName)
                .createIndex(new Document("createdTime", 1),
                        new IndexOptions().background(true).name("idx_createdTime"));
        mongoTemplate.getCollection(collectionName)
                .createIndex(new Document("deletedTime", 1),
                        new IndexOptions().background(true).expireAfter(0L, TimeUnit.MILLISECONDS).name("idx_deletedTime"));
        createIndexCollectionNames.add(collectionName);
    }

}
