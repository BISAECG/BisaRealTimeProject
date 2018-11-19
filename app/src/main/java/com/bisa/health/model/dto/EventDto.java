package com.bisa.health.model.dto;

import com.bisa.health.provider.event.EventContentValues;
import com.bisa.health.provider.event.EventCursor;

import java.io.Serializable;

public class EventDto implements Serializable{
	
	private int id;
	/*
	 * 用户名
	 */
	private int user_guid;

	/**
	 * 联系人名字
	 */
	private String event_name;
	/**
	 *联系号码
	 */
	private String event_num;
	private int event_type;
	private int event_index;

    private String event_email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(int user_guid) {
        this.user_guid = user_guid;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_num() {
        return event_num;
    }

    public void setEvent_num(String event_num) {
        this.event_num = event_num;
    }

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
    }

    public int getEvent_index() {
        return event_index;
    }

    public void setEvent_index(int event_index) {
        this.event_index = event_index;
    }

    public String getEvent_email() {
        return event_email;
    }

    public void setEvent_email(String event_email) {
        this.event_email = event_email;
    }

    public EventDto() {
		super();
	}

    public EventDto(int id, int user_guid, String event_name, String event_num, int event_type, int event_index,String event_email) {
        this.id = id;
        this.user_guid = user_guid;
        this.event_name = event_name;
        this.event_num = event_num;
        this.event_type = event_type;
        this.event_index = event_index;
        this.event_email=event_email;
    }

    public EventContentValues toEventContentValues(){
		EventContentValues eventContentValues=new EventContentValues();
		eventContentValues.putUserGuid(this.getUser_guid());
		eventContentValues.putEventType(this.getEvent_type());
		eventContentValues.putEventName(this.getEvent_name());
		eventContentValues.putEventNum(this.getEvent_num());
        eventContentValues.putEventIndex(this.getEvent_index());
        eventContentValues.putEventEmail(this.getEvent_email());
		return eventContentValues;
	}
	public EventDto toEvent(EventCursor eventCursor){
		this.setUser_guid(eventCursor.getUserGuid());
		this.setEvent_type(eventCursor.getEventType());
		this.setEvent_name(eventCursor.getEventName());
		this.setEvent_num(eventCursor.getEventNum());
	    this.setEvent_index(eventCursor.getEventIndex());
        this.setEvent_email(eventCursor.getEventEmail());
		return this;

	}

    @Override
    public String toString() {
        return "EventDto{" +
                "id=" + id +
                ", user_guid=" + user_guid +
                ", event_name='" + event_name + '\'' +
                ", event_num='" + event_num + '\'' +
                ", event_type=" + event_type +
                ", event_index=" + event_index +
                ", event_email='" + event_email + '\'' +
                '}';
    }
}
