package com.bisa.health.ecg.model;

/**
 * Created by Administrator on 2018/5/4.
 */

public class ReportListDto {
    private String report_number;
    private int report_type;
    private int report_status;

    private int user_guid;
    private String start_time;

    public String getReport_number() {
        return report_number;
    }

    public void setReport_number(String report_number) {
        this.report_number = report_number;
    }

    public int getReport_type() {
        return report_type;
    }

    public void setReport_type(int report_type) {
        this.report_type = report_type;
    }

    public int getReport_status() {
        return report_status;
    }

    public void setReport_status(int report_status) {
        this.report_status = report_status;
    }

    public int getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(int user_guid) {
        this.user_guid = user_guid;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
}
