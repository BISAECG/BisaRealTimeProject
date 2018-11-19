package com.bisa.health.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/3.
 */

public class ResultData <T> implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 5674813304197397731L;
    /**
     * error code
     */
    private int code;
    /**
     * error msg
     */
    private String message;


    /**
     * 登入时间戳gmt时间
     */
    private long timestamp;



    private String token;

    private T data;


    public int getCode() {
        return code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public long getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public T getData() {
        return data;
    }

    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public void setData(T data) {
        this.data = data;
    }


    public ResultData() {
        super();

    }

    @Override
    public String toString() {
        return "ResultData [code=" + code + ", message=" + message + ", timestamp=" + timestamp + ", data=" + data
                + "]";
    }

}
