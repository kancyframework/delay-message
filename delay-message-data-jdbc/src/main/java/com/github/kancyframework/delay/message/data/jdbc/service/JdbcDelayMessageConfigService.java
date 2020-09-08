package com.github.kancyframework.delay.message.data.jdbc.service;

import com.github.kancyframework.delay.message.data.jdbc.dao.DelayMessageConfigDao;
import com.github.kancyframework.delay.message.data.jdbc.dao.DelayMessageConfigEntity;
import com.github.kancyframework.delay.message.service.DelayMessageConfigService;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * JdbcDelayMessageConfigService
 *
 * @author kancy
 * @date 2020/7/25 12:23
 */
public class JdbcDelayMessageConfigService implements DelayMessageConfigService {

    private final DelayMessageConfigDao delayMessageConfigDao;

    public JdbcDelayMessageConfigService(DelayMessageConfigDao delayMessageConfigDao) {
        this.delayMessageConfigDao = delayMessageConfigDao;
    }

    @Override
    public DelayMessageConfig queryConfigByMessageKey(String messageKey) {
        DelayMessageConfigEntity configEntity = delayMessageConfigDao.queryConfigByMessageKey(messageKey);
        DelayMessageConfig config = new DelayMessageConfig();
        BeanUtils.copyProperties(configEntity,config);
        return config;
    }

    @Override
    public List<DelayMessageConfig> queryConfigByMessageKeys(Set<String> messageKeys) {
        List<DelayMessageConfigEntity> delayMessageConfigEntities = delayMessageConfigDao.queryConfigByMessageKeys(messageKeys);
        if (CollectionUtils.isEmpty(delayMessageConfigEntities)){
            return Collections.emptyList();
        }

        List<DelayMessageConfig> list = new ArrayList<>(delayMessageConfigEntities.size());
        delayMessageConfigEntities.forEach(configEntity -> {
            DelayMessageConfig config = new DelayMessageConfig();
            BeanUtils.copyProperties(configEntity,config);
            list.add(config);
        });

        return list;
    }
}
