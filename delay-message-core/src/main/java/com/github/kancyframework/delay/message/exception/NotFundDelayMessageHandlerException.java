package com.github.kancyframework.delay.message.exception;


/**
 * <p>
 * SendDelayMessageException
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 13:40
 **/

public class NotFundDelayMessageHandlerException extends DelayMessageException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param message
     */
    public NotFundDelayMessageHandlerException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message
     * @param cause
     */
    public NotFundDelayMessageHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
