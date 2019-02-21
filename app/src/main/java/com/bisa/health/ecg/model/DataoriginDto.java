package com.bisa.health.ecg.model;

import java.io.Serializable;
import java.util.List;

public class DataoriginDto implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<CalendarDto> calendarDtoList;
	private List<AlarmLogDto> alarmLogst;

	public List<CalendarDto> getCalendarDtoList() {
		return calendarDtoList;
	}

	public void setCalendarDtoList(List<CalendarDto> calendarDtoList) {
		this.calendarDtoList = calendarDtoList;
	}

	public List<AlarmLogDto> getAlarmLogst() {
		return alarmLogst;
	}

	public void setAlarmLogst(List<AlarmLogDto> alarmLogst) {
		this.alarmLogst = alarmLogst;
	}

	public DataoriginDto() {
	}
}
