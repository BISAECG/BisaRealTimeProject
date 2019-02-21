package com.bisa.health.ecg.config;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/29.
 */

public class ECGConfig implements Parcelable,Serializable {

    private int autoAlarm=1;
    private int manualAppAlarm=1;
    private int manualDeviceAlarm=1;
    //数据采集间隔10秒
    private int collectIinterval=10;
    //数据发送价格20秒
    private int alarmInterval=20;
    private int bleConnMaxCount=5;

    protected ECGConfig(Parcel in) {
        autoAlarm = in.readInt();
        manualAppAlarm = in.readInt();
        manualDeviceAlarm = in.readInt();
        collectIinterval=in.readInt();
        alarmInterval=in.readInt();
        bleConnMaxCount=in.readInt();
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }

    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
    }

    public int getAutoAlarm() {
        return autoAlarm;
    }

    public void setAutoAlarm(int autoAlarm) {
        this.autoAlarm = autoAlarm;
    }

    public int getManualAppAlarm() {
        return manualAppAlarm;
    }

    public void setManualAppAlarm(int manualAppAlarm) {
        this.manualAppAlarm = manualAppAlarm;
    }

    public int getManualDeviceAlarm() {
        return manualDeviceAlarm;
    }

    public void setManualDeviceAlarm(int manualDeviceAlarm) {
        this.manualDeviceAlarm = manualDeviceAlarm;
    }

    public int getCollectIinterval() {
        return collectIinterval;
    }

    public void setCollectIinterval(int collectIinterval) {
        this.collectIinterval = collectIinterval;
    }

    public int getBleConnMaxCount() {
        return bleConnMaxCount;
    }

    public void setBleConnMaxCount(int bleConnMaxCount) {
        this.bleConnMaxCount = bleConnMaxCount;
    }

    public ECGConfig() {
        super();
    }


    @Override
    public String toString() {
        return "ECGConfig{" +
                "autoAlarm=" + autoAlarm +
                ", manualAppAlarm=" + manualAppAlarm +
                ", manualDeviceAlarm=" + manualDeviceAlarm +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(autoAlarm);
        dest.writeInt(manualAppAlarm);
        dest.writeInt(manualDeviceAlarm);
        dest.writeInt(collectIinterval);
        dest.writeInt(alarmInterval);
        dest.writeInt(bleConnMaxCount);
    }
    public static final Creator<ECGConfig> CREATOR = new Creator<ECGConfig>() {
        @Override
        public ECGConfig createFromParcel(Parcel in) {
            return new ECGConfig(in);
        }

        @Override
        public ECGConfig[] newArray(int size) {
            return new ECGConfig[size];
        }
    };
}