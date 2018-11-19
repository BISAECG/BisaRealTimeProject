package com.bisa.health.ecg.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/9.
 */

public class OTGECGDto {
    private String title;
    private List<OTGECGFileDto> list;
    private boolean isShow;


    private int ymd;
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<OTGECGFileDto> getList() {
        return list;
    }

    public void setList(List<OTGECGFileDto> list) {
        this.list = list;
    }

    public OTGECGDto() {
        list=new ArrayList<OTGECGFileDto>();
    }


    public int getYmd() {
        return ymd;
    }

    public void setYmd(int ymd) {
        this.ymd = ymd;
    }
}
