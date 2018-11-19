package com.bisa.health.ecg.model;

import java.util.ArrayList;
import java.util.List;

public class GSFileObject {
	private String filename;
	private String guid;
	private List<GprsBean> gsList;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public List<GprsBean> getGsList() {
		return gsList;
	}

	public void setGsList(List<GprsBean> gsList) {
		this.gsList = gsList;
	}

	public GSFileObject(String filename, String guid) {
		super();
		this.filename = filename;
		this.guid = guid;
		gsList=new ArrayList<GprsBean>();
	}


	private GSFileObject() {

	}
	
	
	
	
}
