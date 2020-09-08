package com.github.kancyframework.delay.message.demo.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * <p>
 * DemoMessageListener
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/18 11:22
 **/
@Slf4j
@Component
public class DemoMessageListener {

    /**
     * 子类实现 子类方法需要添加： @StreamListener注解配置
     *
     * @param message
     *
     */
    @StreamListener(value = Sink.INPUT, condition = "headers['delayMessageMessageType']=='test_stream'")
    public void onMessage(Message<String> message) {
        log.info(message.getPayload());
    }
}
