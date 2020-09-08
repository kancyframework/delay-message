package com.kancy.delay.message.db.message;

import java.time.Duration;

/**
 * CustomDelayMessage
 *
 * @author kancy
 * @date 2020/7/17 23:05
 */
public class CustomDelayMessage<T> implements IDelayMessage {
    /**
     * 消息key
     */
    private String messageKey;
    /**
     * 延时时间
     */
    private Duration delay;
    /**
     * 消息体
     */
    private T payload;


    public CustomDelayMessage() {
    }

    public CustomDelayMessage(String messageKey, Duration delay, T payload) {
        this.messageKey = messageKey;
        this.delay = delay;
        this.payload = payload;
    }

    public CustomDelayMessage(T payload) {
        setPayload(payload);
    }

    public CustomDelayMessage(T payload, Duration delay) {
        setDelay(delay);
        setPayload(payload);
    }

    public CustomDelayMessage(T payload, int day) {
        setDelay(Duration.ofDays(day));
        setPayload(payload);
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }

    @Override
    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("messageKey='").append(messageKey).append('\'');
        sb.append(", delay=").append(delay);
        sb.append(", payload=").append(payload);
        sb.append('}');
        return sb.toString();
    }
}
