package com.bisa.health.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/29.
 */

public class UpdateVersion implements Serializable{
    private int appVserion=-1;
    private int serverVersion=-1;

    public int getAppVserion() {
        return appVserion;
    }

    public void setAppVserion(int appVserion) {
        this.appVserion = appVserion;
    }

    public int getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean isUpdate(){
        if(appVserion<serverVersion){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "UpdateVersion{" +
                "appVserion=" + appVserion +
                ", serverVersion=" + serverVersion +
                '}';
    }
}
