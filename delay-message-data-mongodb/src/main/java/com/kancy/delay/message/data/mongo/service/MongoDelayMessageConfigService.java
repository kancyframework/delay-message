package com.kancy.delay.message.data.mongo.service;

import com.kancy.delay.message.data.mongo.repository.DelayMessageConfigEntity;
import com.kancy.delay.message.data.mongo.repository.DelayMessageConfigRepository;
import com.kancy.delay.message.db.service.DelayMessageConfig;
import com.kancy.delay.message.db.service.DelayMessageConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * MongoDelayMessageService
 *
 * @author kancy
 * @date 2020/7/25 13:42
 */
public class MongoDelayMessageConfigService implements DelayMessageConfigService {

    private final DelayMessageConfigRepository delayMessageConfigRepository;

    public MongoDelayMessageConfigService(DelayMessageConfigRepository delayMessageConfigRepository) {
        this.delayMessageConfigRepository = delayMessageConfigRepository;
    }

    @Override
    public DelayMessageConfig queryConfigByMessageKey(String messageKey) {
        DelayMessageConfigEntity configEntity = delayMessageConfigRepository.queryConfigByMessageKey(messageKey);
        DelayMessageConfig config = new DelayMessageConfig();
        BeanUtils.copyProperties(configEntity,config);
        return config;
    }

    @Override
    public List<DelayMessageConfig> queryConfigByMessageKeys(Set<String> messageKeys) {
        List<DelayMessageConfigEntity> delayMessageConfigEntities = delayMessageConfigRepository.queryConfigByMessageKeys(messageKeys);
        if (CollectionUtils.isEmpty(delayMessageConfigEntities)){
            return Collections.emptyList();
        }

        List<DelayMessageConfig> list = new ArrayList<>(delayMessageConfigEntities.size());
        delayMessageConfigEntities.forEach(configEntity -> {
            DelayMessageConfig config = new DelayMessageConfig();
            BeanUtils.copyProperties(configEntity, config);
            list.add(config);
        });
        return list;
    }
}
