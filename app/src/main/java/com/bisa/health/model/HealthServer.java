package com.bisa.health.model;

import com.bisa.health.rest.HttpHelp;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/30.
 */

public class HealthServer implements Serializable{

    private static final long serialVersionUID = 1L;

    private String domain;
    private String datServer;

    private String token;
    private String areaCode;
    private String timeZone;
    private String username; //保留最后登入用户名
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopserver() {
        return HttpHelp.HTTP_PREFIX+shopserver;
    }

    public void setShopserver(String shopserver) {
        this.shopserver = shopserver;
    }

    private String shopserver;
    public String getDomain() {
        return HttpHelp.HTTP_PREFIX+domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDatServer() {
        return HttpHelp.HTTP_PREFIX+datServer;
    }

    public void setDatServer(String datServer) {
        this.datServer = datServer;
    }

    public void saveToken(String token){
        this.token=token;
    }
    public String loadToken(){
        return this.token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
