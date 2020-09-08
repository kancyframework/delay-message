package com.kancy.delay.message.db.exception;


/**
 * <p>
 * SendDelayMessageException
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 13:40
 **/

public class SendDelayMessageException extends DelayMessageException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     *
     * @param message
     */
    public SendDelayMessageException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message
     * @param cause
     */
    public SendDelayMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
