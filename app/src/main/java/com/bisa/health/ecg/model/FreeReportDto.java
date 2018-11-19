package com.bisa.health.ecg.model;

import android.content.Context;

import com.bisa.health.R;

import java.io.Serializable;

public class FreeReportDto implements Serializable {

	private int user_guid;
	//正常心博：
	private String normal_cardiac;
	//正常心博次数：
	private String normal_cardiac_num;
	//过慢心博：
	private String toLow_cardiac;
	//过慢心博次数:
	private String toLow_cardiac_num;
	//过快心博：
	private String dizzy_cardiac;
	//过快心博次数：
	private String dizzy_cardiac_num;
	//最高心率:
	private String max_heart_rate;
	//平均心率:
	private String average_heart_rate;
	//最低心率:
	private String min_heart_rate;
	//总结建议
	private String suggest;

	private int resourceId;
	
	private String report_number;
	
	private String subtime_time;

	private int report_type;//10：15分钟 11:24小时  12:1分钟
	
	private int report_status;//1：未读 2：已读


	public int getUser_guid() {
		return user_guid;
	}

	public void setUser_guid(int user_guid) {
		this.user_guid = user_guid;
	}


	public String getNormal_cardiac() {
		return normal_cardiac;
	}
	public void setNormal_cardiac(String normal_cardiac) {
		this.normal_cardiac = normal_cardiac;
	}
	public String getNormal_cardiac_num() {
		return normal_cardiac_num;
	}
	public void setNormal_cardiac_num(String normal_cardiac_num) {
		this.normal_cardiac_num = normal_cardiac_num;
	}
	public String getToLow_cardiac() {
		return toLow_cardiac;
	}
	public void setToLow_cardiac(String toLow_cardiac) {
		this.toLow_cardiac = toLow_cardiac;
	}
	public String getToLow_cardiac_num() {
		return toLow_cardiac_num;
	}
	public void setToLow_cardiac_num(String toLow_cardiac_num) {
		this.toLow_cardiac_num = toLow_cardiac_num;
	}
	public String getDizzy_cardiac() {
		return dizzy_cardiac;
	}
	public void setDizzy_cardiac(String dizzy_cardiac) {
		this.dizzy_cardiac = dizzy_cardiac;
	}
	public String getDizzy_cardiac_num() {
		return dizzy_cardiac_num;
	}
	public void setDizzy_cardiac_num(String dizzy_cardiac_num) {
		this.dizzy_cardiac_num = dizzy_cardiac_num;
	}
	public String getMax_heart_rate() {
		return max_heart_rate;
	}
	public void setMax_heart_rate(String max_heart_rate) {
		this.max_heart_rate = max_heart_rate;
	}
	public String getAverage_heart_rate() {
		return average_heart_rate;
	}
	public void setAverage_heart_rate(String average_heart_rate) {
		this.average_heart_rate = average_heart_rate;
	}
	public String getMin_heart_rate() {
		return min_heart_rate;
	}
	public void setMin_heart_rate(String min_heart_rate) {
		this.min_heart_rate = min_heart_rate;
	}
	public String getSuggest() {
		return suggest;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	public String getReport_number() {
		return report_number;
	}
	public void setReport_number(String report_number) {
		this.report_number = report_number;
	}
	public String getSubtime_time() {
		return subtime_time;
	}
	public void setSubtime_time(String subtime_time) {
		this.subtime_time = subtime_time;
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

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String switchDesc(int index, Context context){

		switch (index){
			case 0:
				return context.getResources().getString(R.string.free_report_error);

			case 1:
				return context.getResources().getString(R.string.free_report_normal);
			case 2:
				return context.getResources().getString(R.string.free_report_fast_a);

			case 3:
				return context.getResources().getString(R.string.free_report_fast_b);
			case 4:
				return context.getResources().getString(R.string.free_report_last_a);
			case 5:
				return context.getResources().getString(R.string.free_report_last_b);

		}

		return null;
	}

    public FreeReportDto() {
    }




}
