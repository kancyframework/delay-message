package com.github.kancyframework.delay.message.demo.performance;

import com.github.kancyframework.delay.message.message.DelayMessage;
import com.github.kancyframework.delay.message.message.SimpleDelayMessage;

/**
 * TestDelayMessage
 *
 * @author kancy
 * @date 2020/7/16 22:43
 */
@DelayMessage(topic = "test_bean")
public class TestDelayMessage extends SimpleDelayMessage<Object> {

}
