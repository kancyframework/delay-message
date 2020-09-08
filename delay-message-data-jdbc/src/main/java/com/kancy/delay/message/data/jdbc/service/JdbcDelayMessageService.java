package com.kancy.delay.message.data.jdbc.service;

import com.kancy.delay.message.data.jdbc.dao.DelayMessageDao;
import com.kancy.delay.message.data.jdbc.dao.DelayMessageEntity;
import com.kancy.delay.message.data.jdbc.dao.DelayMessageInfoDao;
import com.kancy.delay.message.data.jdbc.dao.DelayMessageInfoEntity;
import com.kancy.delay.message.db.service.DelayMessage;
import com.kancy.delay.message.db.service.DelayMessageService;
import net.dreamlu.mica.core.utils.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JdbcDelayMessageService
 *
 * @author kancy
 * @date 2020/7/25 12:23
 */
public class JdbcDelayMessageService implements DelayMessageService {

    private static final Logger log = LoggerFactory.getLogger(JdbcDelayMessageService.class);

    private final DelayMessageDao delayMessageDao;
    private final DelayMessageInfoDao delayMessageInfoDao;

    public JdbcDelayMessageService(DelayMessageDao delayMessageDao, DelayMessageInfoDao delayMessageInfoDao) {
        this.delayMessageDao = delayMessageDao;
        this.delayMessageInfoDao = delayMessageInfoDao;
    }

    @Override
    public void storeDelayMessage(String partitionName, DelayMessage info) {
        if (Objects.nonNull(info.getPayload())){
            DelayMessageInfoEntity infoEntity = new DelayMessageInfoEntity();
            infoEntity.setMessage($.toJson(info));
            infoEntity.setId(info.getDataId());
            delayMessageInfoDao.save(String.format("%s_info", partitionName), infoEntity);
        }
        DelayMessageEntity entity = new DelayMessageEntity();
        BeanUtils.copyProperties(info, entity);
        delayMessageDao.saveWhitDelay(partitionName, entity, info.getDelay());
    }

    @Override
    public List<DelayMessage> scanExpiredMessage(String partitionName, long minExpireTime, int limit) {
        List<DelayMessageEntity> entityList = delayMessageDao.scan(partitionName, minExpireTime, limit);
        if (CollectionUtils.isEmpty(entityList)){
            return Collections.emptyList();
        }

        Map<String, String> messageMap = delayMessageInfoDao.queryMessageMap(String.format("%s_info", partitionName),
                entityList.stream().map(DelayMessageEntity::getDataId).collect(Collectors.toSet()));

        List<DelayMessage> list = new ArrayList<>();
        entityList.forEach(entity ->{
            try {
                String jsonMessage = messageMap.get(entity.getDataId());
                DelayMessage delayMessage = $.readJson(jsonMessage, DelayMessage.class);
                if (Objects.isNull(delayMessage)){
                    delayMessage = new DelayMessage();
                    BeanUtils.copyProperties(entity, delayMessage);
                    delayMessage.setDelay(Duration.ofMillis(entity.getExpiredTime().getTime() - entity.getCreatedTime().getTime()));
                }else{
                    delayMessage.setScanTimes(entity.getScanTimes());
                    delayMessage.setMessageStatus(entity.getMessageStatus());
                    delayMessage.setTraceId(entity.getTraceId());
                    delayMessage.setCreatedTime(entity.getCreatedTime());
                    delayMessage.setUpdatedTime(entity.getUpdatedTime());
                }
                list.add(delayMessage);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        return list;
    }

    @Override
    public void updateStatus(String partitionName, String messageId, int status) {
        delayMessageDao.updateStatus(partitionName, messageId, status);
    }

    @Override
    public void batchUpdateStatus(String partitionName, List<String> messageIds, int fromStatus, int toStatus) {
        delayMessageDao.batchUpdateStatus(partitionName, messageIds, fromStatus, toStatus);
    }

    @Override
    public void batchUpdateOnProcessing(String partitionName, List<String> messageIds) {
        delayMessageDao.batchUpdateOnProcessing(partitionName, messageIds);
    }

    @Override
    public List<String> findAllExecuteTimeoutMessageIds(String partitionName, Duration lastTime, long recentSeconds) {
        return delayMessageDao.findAllExecuteTimeoutMessageIds(partitionName, lastTime, recentSeconds);
    }

    /**
     * 找到最小的到期时间
     *
     * @param partitionName
     * @param recentSeconds
     * @return
     */
    @Override
    public Date findMinExpiredTime(String partitionName, long recentSeconds) {
        return delayMessageDao.findMinExpireTime(partitionName, recentSeconds);
    }
}
