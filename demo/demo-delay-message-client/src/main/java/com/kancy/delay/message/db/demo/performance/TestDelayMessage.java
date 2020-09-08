package com.kancy.delay.message.db.demo.performance;

import com.kancy.delay.message.db.message.DelayMessage;
import com.kancy.delay.message.db.message.SimpleDelayMessage;

/**
 * TestDelayMessage
 *
 * @author kancy
 * @date 2020/7/16 22:43
 */
@DelayMessage(topic = "test_bean")
public class TestDelayMessage extends SimpleDelayMessage<Object> {

}
