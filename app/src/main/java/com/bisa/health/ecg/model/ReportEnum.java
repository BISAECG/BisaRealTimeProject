package com.bisa.health.ecg.model;

/**
 * Created by Administrator on 2018/5/3.
 */

public enum ReportEnum {


    FREE_REPORT(60000),NULL(-1);

    ReportEnum(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public long getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    private long timeOffset;



}
