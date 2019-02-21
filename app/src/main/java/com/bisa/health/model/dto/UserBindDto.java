package com.bisa.health.model.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/16.
 */

public class UserBindDto implements Serializable{

    private String username;
    private String phone;
    private String email;
    private String verifyType;
    private String bindType;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    @Override
    public String toString() {
        return "UserBindDto{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", verifyType='" + verifyType + '\'' +
                ", bindType='" + bindType + '\'' +
                '}';
    }
}
