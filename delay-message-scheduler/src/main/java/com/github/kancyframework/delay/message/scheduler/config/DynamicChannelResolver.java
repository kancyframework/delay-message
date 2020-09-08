package com.github.kancyframework.delay.message.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * DynamicChannelResolver
 * 动态channel处理器
 *
 * @author kancy
 * @date 2020/7/18 0:24
 */
public class DynamicChannelResolver {

    private static final Logger log = LoggerFactory.getLogger(DynamicChannelResolver.class);

    private final BinderAwareChannelResolver binderAwareChannelResolver;

    private final Map<String, MessageChannel> messageChannelMap = new HashMap<>();

    public DynamicChannelResolver(BinderAwareChannelResolver binderAwareChannelResolver) {
        this.binderAwareChannelResolver = binderAwareChannelResolver;
    }

    public MessageChannel getChannel(String destinationChannel) {
        if (!messageChannelMap.containsKey(destinationChannel)) {
            log.info("创建BinderAwareChannelResolver实例[destinationChannel: {}]", destinationChannel);
            createBinderAwareChannelResolver(destinationChannel);
        }
        return messageChannelMap.get(destinationChannel);
    }

    /**
     * 创建动态MessageChannel，由于resolveDestination存在并发问题，因此使用synchronized
     *
     * @param replyChannel
     */
    private synchronized void createBinderAwareChannelResolver(String replyChannel) {
        MessageChannel messageChannel = binderAwareChannelResolver.resolveDestination(replyChannel);
        messageChannelMap.putIfAbsent(replyChannel, messageChannel);
    }
}
