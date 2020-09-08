package com.kancy.delay.message.db.message;

import java.time.Duration;


/**
 * <p>
 * SimpleDelayMessage
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/17 14:22
 **/

public class SimpleDelayMessage<T> extends AbstractDelayMessage {

    public SimpleDelayMessage() {

    }

    public SimpleDelayMessage(T payload) {
        setPayload(payload);
    }

    public SimpleDelayMessage(T payload, Duration delay) {
        setDelay(delay);
        setPayload(payload);
    }

    public SimpleDelayMessage(T payload, int day) {
        setDelay(Duration.ofDays(day));
        setPayload(payload);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
