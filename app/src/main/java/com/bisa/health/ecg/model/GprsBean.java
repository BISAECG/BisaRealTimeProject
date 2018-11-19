package com.bisa.health.ecg.model;


public class GprsBean {
	private int id;
	private int userguid;
	private String lng;
	private String lat;
	private String collection_time;
	private int gprs_type;
	private String address;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserguid() {
		return userguid;
	}
	public void setUserguid(int userguid) {
		this.userguid = userguid;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getCollection_time() {
		return collection_time;
	}
	public void setCollection_time(String collection_time) {
		this.collection_time = collection_time;
	}
	public int getGprs_type() {
		return gprs_type;
	}
	public void setGprs_type(int gprs_type) {
		this.gprs_type = gprs_type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}