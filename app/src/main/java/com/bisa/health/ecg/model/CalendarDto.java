package com.bisa.health.ecg.model;

import java.io.Serializable;
import java.util.Date;

public class CalendarDto  implements Serializable {

	private int user_guid;
	private String report_number;
	private Date start_time;
	private int report_status;
	private int report_type;
	private int day;
	private int report_count;
	private int log_count;

	public int getUser_guid() {
		return user_guid;
	}

	public void setUser_guid(int user_guid) {
		this.user_guid = user_guid;
	}

	public String getReport_number() {
		return report_number;
	}

	public void setReport_number(String report_number) {
		this.report_number = report_number;
	}

	public int getReport_status() {
		return report_status;
	}

	public void setReport_status(int report_status) {
		this.report_status = report_status;
	}

	public int getReport_type() {
		return report_type;
	}

	public void setReport_type(int report_type) {
		this.report_type = report_type;
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

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public int getLog_count() {
		return log_count;
	}

	public void setLog_count(int log_count) {
		this.log_count = log_count;
	}

	public CalendarDto() {
	}

	@Override
	public String toString() {
		return "CalendarDto{" +
				"user_guid=" + user_guid +
				", report_number='" + report_number + '\'' +
				", start_time=" + start_time +
				", report_status=" + report_status +
				", report_type=" + report_type +
				", day=" + day +
				", report_count=" + report_count +
				", log_count=" + log_count +
				'}';
	}
}
