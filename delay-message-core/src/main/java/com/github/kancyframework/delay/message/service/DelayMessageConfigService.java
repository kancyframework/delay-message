package com.github.kancyframework.delay.message.service;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

/**
 * DelayMessageConfigService
 *
 * @author kancy
 * @date 2020/7/25 10:41
 */
public interface DelayMessageConfigService {
    DelayMessageConfig queryConfigByMessageKey(@NonNull String messageKey);
    List<DelayMessageConfig> queryConfigByMessageKeys(@NonNull Set<String> messageKeys);
}
