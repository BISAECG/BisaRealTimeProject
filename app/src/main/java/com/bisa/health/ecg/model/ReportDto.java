package com.bisa.health.ecg.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/4.
 */

public class ReportDto implements Serializable{

    private int id;//		报告ID

    private String report_number;//	报告唯一编号

    private ReportType report_type;//报告类型(10-15分钟报告；11-24小时报告；)

    private ReportStatus report_status;//报告状态（1-生成好未查看，2已查看，3正在生成中；4-尚未上传数据；5-已上传数据;6-无效报告）

    private	int user_guid;   //	用户唯一编码

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getReport_number() {
        return report_number;
    }
    public void setReport_number(String report_number) {
        this.report_number = report_number;
    }


    public ReportType getReport_type() {
        return report_type;
    }
    public void setReport_type(ReportType report_type) {
        this.report_type = report_type;
    }
    public ReportStatus getReport_status() {
        return report_status;
    }
    public void setReport_status(ReportStatus report_status) {
        this.report_status = report_status;
    }
    public int getUser_guid() {
        return user_guid;
    }
    public void setUser_guid(int user_guid) {
        this.user_guid = user_guid;
    }


    public ReportDto() {
        super();
    }

    @Override
    public String toString() {
        return "AppReport [id=" + id + ", report_number=" + report_number + ", report_type=" + report_type
                + ", report_status=" + report_status + ", user_guid=" + user_guid + "]";
    }

}
