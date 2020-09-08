package com.github.kancyframework.delay.message.data.mongo.service;

import com.github.kancyframework.delay.message.data.mongo.repository.DelayMessageEntity;
import com.github.kancyframework.delay.message.data.mongo.repository.DelayMessageRepository;
import com.github.kancyframework.delay.message.service.DelayMessage;
import com.github.kancyframework.delay.message.service.DelayMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;

/**
 * MongoDelayMessageService
 *
 * @author kancy
 * @date 2020/7/25 13:42
 */
public class MongoDelayMessageService implements DelayMessageService {

    private final DelayMessageRepository delayMessageRepository;

    public MongoDelayMessageService(DelayMessageRepository delayMessageRepository) {
        this.delayMessageRepository = delayMessageRepository;
    }

    @Override
    public void storeDelayMessage(String partitionName, DelayMessage info) {
        DelayMessageEntity entity = new DelayMessageEntity();
        BeanUtils.copyProperties(info, entity);
        // 数据一体，数据ID为延时消息ID
        if (Objects.nonNull(info.getPayload())){
            entity.setDataId(entity.getId());
        }
        delayMessageRepository.save(partitionName, entity);
    }

    @Override
    public List<DelayMessage> scanExpiredMessage(String tableName, long minExpireTime, int limit) {
        List<DelayMessageEntity> delayQueueEntities = delayMessageRepository.scanExpiredMessage(tableName, minExpireTime, limit);

        if (CollectionUtils.isEmpty(delayQueueEntities)){
            return Collections.emptyList();
        }
        ArrayList<DelayMessage> list = new ArrayList<>();
        delayQueueEntities.forEach(delayMessageEntity -> {
            DelayMessage info = new DelayMessage();
            BeanUtils.copyProperties(delayMessageEntity, info);
            list.add(info);
        });
        return list;
    }

    @Override
    public void updateStatus(String partitionName, String messageId, int status) {
        delayMessageRepository.updateStatus(partitionName, messageId, status);
    }

    @Override
    public void batchUpdateStatus(String partitionName, List<String> messageIds, int fromStatus, int toStatus) {
        delayMessageRepository.batchUpdateStatus(partitionName, messageIds, fromStatus, toStatus);
    }

    @Override
    public void batchUpdateOnProcessing(String partitionName, List<String> messageIds) {
        delayMessageRepository.batchUpdateOnProcessing(partitionName, messageIds);
    }

    @Override
    public List<String> findAllExecuteTimeoutMessageIds(String partitionName, Duration lastTime, long recentSeconds) {
        return delayMessageRepository.findAllProcessingMessageIdsWithTimeoutAndTimeRange(partitionName, lastTime, recentSeconds);
    }

    /**
     * 找到最小的到期时间
     *
     * @param partitionName
     * @param recentSeconds 最近多少秒内的数据
     * @return
     */
    @Override
    public Date findMinExpiredTime(String partitionName, long recentSeconds) {
        return delayMessageRepository.findMinExpireTime(partitionName, recentSeconds);
    }
}
