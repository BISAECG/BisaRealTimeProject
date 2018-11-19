package com.bisa.health.ecg.config;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/29.
 */

public class ECGSetConfig implements Serializable {

    private boolean autoAlarm=false;
    private boolean manualAppAlarm=true;
    private boolean manualDeviceAlarm=true;

    public boolean isAutoAlarm() {
        return autoAlarm;
    }

    public void setAutoAlarm(boolean autoAlarm) {
        this.autoAlarm = autoAlarm;
    }

    public boolean isManualAppAlarm() {
        return manualAppAlarm;
    }

    public void setManualAppAlarm(boolean manualAppAlarm) {
        this.manualAppAlarm = manualAppAlarm;
    }

    public boolean isManualDeviceAlarm() {
        return manualDeviceAlarm;
    }

    public void setManualDeviceAlarm(boolean manualDeviceAlarm) {
        this.manualDeviceAlarm = manualDeviceAlarm;
    }

    public ECGSetConfig() {
        super();
    }


    public ECGSetConfig(boolean autoAlarm, boolean manualAppAlarm, boolean manualDeviceAlarm) {
        this.autoAlarm = autoAlarm;
        this.manualAppAlarm = manualAppAlarm;
        this.manualDeviceAlarm = manualDeviceAlarm;
    }

    @Override
    public String toString() {
        return "ECGSetConfig{" +
                "autoAlarm=" + autoAlarm +
                ", manualAppAlarm=" + manualAppAlarm +
                ", manualDeviceAlarm=" + manualDeviceAlarm +
                '}';
    }
}
