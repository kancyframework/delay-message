package com.github.kancyframework.delay.message.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.kancyframework.delay.message.properties.DelayMessageProperties;
import com.github.kancyframework.delay.message.service.DelayMessageConfigService;
import com.github.kancyframework.delay.message.service.DelayMessageConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;

/**
 * DelayMessageConfigCache
 *
 * @author kancy
 * @date 2020/7/19 8:51
 */
public class DelayMessageConfigCache implements InitializingBean {

    private final DelayMessageConfigService delayMessageConfigService;

    private final DelayMessageProperties properties;

    private Cache<String, DelayMessageConfig> delayMessageConfigCache;

    public DelayMessageConfigCache(DelayMessageConfigService delayMessageConfigService,
                                   DelayMessageProperties properties) {
        this.delayMessageConfigService = delayMessageConfigService;
        this.properties = properties;
    }

    public DelayMessageConfig queryConfigByMessageKey(@NonNull String messageKey){
        return delayMessageConfigCache.get(messageKey, this::queryDelayMessageConfigEntityFromDb);
    }

    private DelayMessageConfig queryDelayMessageConfigEntityFromDb(String messageKey) {
        return delayMessageConfigService.queryConfigByMessageKey(messageKey);
    }

    @Override
    public void afterPropertiesSet() {
        delayMessageConfigCache = Caffeine.newBuilder()
                .maximumSize(properties.getConfigCacheMaxSize())
                .expireAfterWrite(properties.getConfigCacheTime())
                .build();
    }
}
