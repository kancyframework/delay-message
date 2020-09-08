package com.kancy.delay.message.db.scheduler.handler;

import com.kancy.delay.message.db.message.IDelayMessage;
import com.kancy.delay.message.db.scheduler.DelayMessageRef;
import com.kancy.delay.message.db.scheduler.config.DynamicChannelResolver;
import net.dreamlu.mica.core.utils.$;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * <p>
 * DelayMessageStreamHandler
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/17 20:14
 **/

public class DelayMessageStreamHandler implements DelayMessageHandler<String> {

    private final DynamicChannelResolver dynamicChannelResolver;

    public DelayMessageStreamHandler(DynamicChannelResolver dynamicChannelResolver) {
        this.dynamicChannelResolver = dynamicChannelResolver;
    }

    @Override
    public void handle(DelayMessageRef<String> ref) {
        String channelName = ref.getNoticeAddress();
        Assert.hasText(channelName, String.format("delay message [%s] noticeAddress（channelName）is empty.", ref.getMessageKey()));
        MessageChannel channel = dynamicChannelResolver.getChannel(channelName);
        String messageBody = ref.getPayload();
        if (!ref.isUseCache()) {
            messageBody = $.toJson(Collections.singletonMap(IDelayMessage.DATA_ID, messageBody));
        }

        // 发送消息
        channel.send(MessageBuilder.withPayload(messageBody)
                .setHeader(IDelayMessage.MESSAGE_KEY, ref.getMessageKey())
                .setHeader(IDelayMessage.MESSAGE_TYPE, ref.getMessageType())
                .setHeader(IDelayMessage.MESSAGE_ID, ref.getDelayMessageId())
                .setHeader(IDelayMessage.TABLE_NAME, ref.getTableName())
                .build());
    }
}
