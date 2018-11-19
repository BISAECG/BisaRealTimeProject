package com.bisa.health.ecg.model;

import java.util.ArrayList;
import java.util.List;

public class RawFileObject {
	private String filename;
	private String sessionid;
	private List<RawObject> rawList;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getSessionid() {
		return sessionid;
	}
	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	public List<RawObject> getRawList() {
		return rawList;
	}
	public void setRawList(List<RawObject> rawList) {
		this.rawList = rawList;
	}
	public RawFileObject(String filename, String sessionid) {
		super();
		this.filename = filename;
		this.sessionid = sessionid;
		rawList=new ArrayList<RawObject>();
	}
	
	
	private RawFileObject() {

	}
	
	
	
	
}
