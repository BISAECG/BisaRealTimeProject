package com.bisa.health.model;

import java.io.Serializable;

public class XinGeMsg implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int status;

	private String action;

	private String msg;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "XinGeMsg{" +
				"status=" + status +
				", action='" + action + '\'' +
				", msg='" + msg + '\'' +
				'}';
	}


}

