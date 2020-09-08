package com.github.kancyframework.delay.message.data.jdbc.dao;

/**
 * DelayMessageInfoEntity
 *
 * @author kancy
 * @date 2020/7/25 12:51
 */
public class DelayMessageInfoEntity {
    private String id;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
