package com.bisa.health.model.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/16.
 */

public class ForGetPwdDto implements Serializable{

    private String username;
    private String phone;
    private String email;
    private String loginType;

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

    public void vLoginType(String vLoginType){this.loginType=vLoginType;}
    public String vLoginType(){return loginType;}

    @Override
    public String toString() {
        return "ForGetPwdDto{" +
                "username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", loginType='" + loginType + '\'' +
                '}';
    }
}
