package com.bisa.health.ecg.model;

import com.bisa.health.usb.fat.Fat128Sys;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.FunUtil;

/**
 * Created by Administrator on 2018/5/10.
 */

public class OTGECGFileDto {
    private String mUserTimeZone;
    private final int DEFAULT_OFFSET=250;
    private Fat128Sys fat128Sys;
    private boolean isChecked;
    private String title;
    private long startGMTTime;
    private long endGMTTime;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Fat128Sys getFat128Sys() {
        return fat128Sys;
    }

    public void setFat128Sys(Fat128Sys fat128Sys) {
        this.fat128Sys = fat128Sys;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getStartGMTTime() {
        return startGMTTime;
    }

    public void setStartGMTTime(long startGMTTime) {
        this.startGMTTime = startGMTTime;
    }

    public long getEndGMTTime() {
        return endGMTTime;
    }

    public void setEndGMTTime(long endGMTTime) {
        this.endGMTTime = endGMTTime;
    }

    public OTGECGFileDto(Fat128Sys fat128Sys, boolean isChecked, String mUserTimeZone) {
        this.mUserTimeZone=mUserTimeZone;
        this.fat128Sys = fat128Sys;
        this.isChecked = isChecked;
        long mSeconds=fat128Sys.getLength()/DEFAULT_OFFSET;
        String[] ecgNameArray=FunUtil.ecgNameSpilt(fat128Sys.getFilename());
        if(ecgNameArray.length>=2){
            String startTime=DateUtil.getUserDateStr("yyyyMMddHHmmss",ecgNameArray[1],mUserTimeZone,0);
            String endTime=DateUtil.getUserDateStr("yyyyMMddHHmmss",ecgNameArray[1],mUserTimeZone,mSeconds*1000);

            this.startGMTTime=DateUtil.getUserDateLong("yyyyMMddHHmmss",ecgNameArray[1],0);
            this.endGMTTime=DateUtil.getUserDateLong("yyyyMMddHHmmss",ecgNameArray[1],mSeconds*1000);
            this.title=startTime+" ~ "+endTime;
        }


    }

    @Override
    public String toString() {
        return "OTGECGFileDto{" +
                "mUserTimeZone='" + mUserTimeZone + '\'' +
                ", DEFAULT_OFFSET=" + DEFAULT_OFFSET +
                ", fat128Sys=" + fat128Sys +
                ", isChecked=" + isChecked +
                ", title='" + title + '\'' +
                ", startGMTTime=" + startGMTTime +
                ", endGMTTime=" + endGMTTime +
                '}';
    }
}
