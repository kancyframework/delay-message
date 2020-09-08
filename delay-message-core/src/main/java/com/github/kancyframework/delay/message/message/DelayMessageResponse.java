package com.github.kancyframework.delay.message.message;

import java.io.Serializable;

/**
 * DelayMessageRequest
 *
 * @author kancy
 * @date 2020/7/22 21:22
 */
public class DelayMessageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否处理成功
     */
    private boolean success;
    /**
     * 返回码
     */
    private String code;
    /**
     * 返回信息
     */
    private String msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return code;
    }

    public void setStatus(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("success=").append(success);
        sb.append(", code='").append(code).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
