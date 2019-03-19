package com.bisa.health.model;

import java.io.Serializable;

public class Event implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int id;
    /*
     * identify id
     */
    private int user_guid;
    /**
     * contact method
     * 1-phone
     * 2-mail
     */
    private int event_type;
    /**
     * contact name
     */
    private String event_name;
    /**
     *contact number
     */
    private String event_num;
    private String event_mail;

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

    public int getEvent_type() {
        return event_type;
    }

    public void setEvent_type(int event_type) {
        this.event_type = event_type;
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

    public String getEvent_mail() {
        return event_mail;
    }

    public void setEvent_mail(String event_mail) {
        this.event_mail = event_mail;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", user_guid=" + user_guid +
                ", event_type=" + event_type +
                ", event_name='" + event_name + '\'' +
                ", event_num='" + event_num + '\'' +
                ", event_mail='" + event_mail + '\'' +
                '}';
    }
}
