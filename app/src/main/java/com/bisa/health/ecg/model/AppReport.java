package com.bisa.health.ecg.model;

import com.bisa.health.provider.appreport.AppreportContentValues;
import com.bisa.health.provider.appreport.AppreportCursor;

import java.io.Serializable;
import java.util.Date;

public class AppReport implements Serializable {
	
	private int id;//		报告ID
	
	private String report_number;//		报告唯一编号
	
	private int report_type;//		报告类型(1-15分钟报告；2-24小时报告；)
	
	private int	report_status;//		报告状态（1-生成好未查看，2已查看，3正在生成中；4-尚未上传数据；5-已上传数据;6-无效报告）
	
	private Date start_time;  //起始时间（第一段数据finished_time?

	private int user_guid;   //	用户唯一编码

	private int year;

	private int month;

    private int day;

    private int report_count;

	private String body;

	private String ecgdat;

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
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getReport_count() {
        return report_count;
    }

    public void setReport_count(int report_count) {
        this.report_count = report_count;
    }

	public String getBody() {
		return body;
	}

	public String getEcgdat() {
		return ecgdat;
	}

	public void setEcgdat(String ecgdat) {
		this.ecgdat = ecgdat;
	}

	public void setBody(String body) {
		this.body = body;
	}
	public AppReport() {
		super();
	}

	public AppreportContentValues toUserContentValues(AppReport appReport){

		AppreportContentValues appreportContentValues=new AppreportContentValues();
		appreportContentValues.putUserGuid(appReport.getUser_guid());
		appreportContentValues.putStartTime(appReport.getStart_time());
		appreportContentValues.putReportStatus(appReport.getReport_status());
		appreportContentValues.putReportNumber(appReport.getReport_number());
		appreportContentValues.putReportType(appReport.getReport_type());
        appreportContentValues.putYear(appReport.getYear());
        appreportContentValues.putMonth(appReport.getMonth());
        appreportContentValues.putDay(appReport.getDay());
        appreportContentValues.putDay(appReport.getReport_count());
		appreportContentValues.putBody(appReport.getBody());
		appreportContentValues.putEcgdat(appReport.getEcgdat());
		return appreportContentValues;
	}

	private static final String TAG = "AppReport";
	public AppreportContentValues toUserContentValues(){
		AppreportContentValues appreportContentValues=new AppreportContentValues();
		appreportContentValues.putUserGuid(this.getUser_guid());
		appreportContentValues.putStartTime(this.getStart_time());
		appreportContentValues.putReportStatus(this.getReport_status());
		appreportContentValues.putReportNumber(this.getReport_number());
		appreportContentValues.putReportType(this.getReport_type());
        appreportContentValues.putMonth(this.getMonth());
        appreportContentValues.putYear(this.getYear());
        appreportContentValues.putDay(this.getDay());
        appreportContentValues.putReportCount(this.getReport_count());
		appreportContentValues.putBody(this.body);
		appreportContentValues.putEcgdat(this.ecgdat);
		return appreportContentValues;
	}

	public AppReport toAppReport(AppreportCursor appreportCursor){
		this.setUser_guid(appreportCursor.getUserGuid());
		this.setStart_time(appreportCursor.getStartTime());
		this.setReport_number(appreportCursor.getReportNumber());
		this.setReport_status(appreportCursor.getReportStatus());
		this.setReport_type(appreportCursor.getReportType());
        this.setYear(appreportCursor.getYear());
        this.setMonth(appreportCursor.getMonth());
        this.setDay(appreportCursor.getDay());
        this.setReport_count(appreportCursor.getReportCount());
		this.setBody(appreportCursor.getBody());
		this.setEcgdat(appreportCursor.getEcgdat());
		return this;
	}

	@Override
	public String toString() {
		return "AppReport{" +
				"id=" + id +
				", report_number='" + report_number + '\'' +
				", report_type=" + report_type +
				", report_status=" + report_status +
				", start_time=" + start_time +
				", user_guid=" + user_guid +
				", year=" + year +
				", month=" + month +
				", day=" + day +
				", report_count=" + report_count +
				", body='" + body + '\'' +
				", ecgdat='" + ecgdat + '\'' +
				'}';
	}
}
