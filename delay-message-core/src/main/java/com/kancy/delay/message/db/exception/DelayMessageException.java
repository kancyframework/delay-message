package com.kancy.delay.message.db.exception;

/**
 * <p>
 * DelayMessageException
 * <p>
 *
 * @author: kancy
 * @date: 2020/7/20 13:40
 **/

public class DelayMessageException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the specified detail message.
     */
    public DelayMessageException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     */
    public DelayMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
