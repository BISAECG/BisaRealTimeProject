package com.bisa.health.model;


import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/26.
 */

public class HealthPath implements Serializable {


    public HealthPath(String basePath) {

        this.log=basePath +"/log";
        this.otgzipdat =basePath +"/otgzipdat";
        this.backdat=basePath+"/backdat";
        this.sys=basePath +"/sys";
        this.user=basePath +"/user";
        this.freedat=basePath +"/freedat";
        this.feepdf=basePath +"/feepdf";
    }

    public HealthPath(){

    }

    private String log;
    private String otgzipdat;
    private String backdat;
    private String sys;
    private String user;
    private String freedat;
    private String feepdf;


    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }




    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getBackdat() {
        return backdat;
    }

    public void setBackdat(String backdat) {
        this.backdat = backdat;
    }

    public String getOtgzipdat() {
        return otgzipdat;
    }

    public String getFreedat() {
        return freedat;
    }

    public void setFreedat(String freedat) {
        this.freedat = freedat;
    }

    public void setOtgzipdat(String otgzipdat) {
        this.otgzipdat = otgzipdat;
    }

    public String getFeepdf() {
        return feepdf;
    }

    public void setFeepdf(String feepdf) {
        this.feepdf = feepdf;
    }
}
