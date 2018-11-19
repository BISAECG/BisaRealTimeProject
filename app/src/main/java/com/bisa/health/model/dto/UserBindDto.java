package com.bisa.health.model.dto;

import com.bisa.health.model.enumerate.LoginTypeEnum;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/16.
 */

public class UserBindDto implements Serializable{

    private LoginTypeEnum loginType;
    private String username;


    public LoginTypeEnum getLoginType() {
        return loginType;
    }
    public void setLoginType(LoginTypeEnum loginType) {
        this.loginType = loginType;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public UserBindDto(LoginTypeEnum loginType, String username) {
        super();
        this.loginType = loginType;
        this.username = username;
    }
    public UserBindDto() {
        super();
    }

    @Override
    public String toString() {
        return "UserBindDto{" +
                "loginType=" + loginType +
                ", username='" + username + '\'' +
                '}';
    }
}
