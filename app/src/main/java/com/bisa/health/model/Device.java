package com.bisa.health.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.bisa.health.provider.device.DeviceContentValues;
import com.bisa.health.provider.device.DeviceCursor;

public class Device implements Parcelable{


	private int user_guid;

	/**
	 * 设备的中文名字
	 */
	private String devname;
	/**
	 * 设备的MAC地址
	 */
	private String macadderss;
	/**
	 * 设备型号
	 */
	private String devnum;
	/**
	 * 设备的连接状态
	 */
	private int connstatus;
	/**
	 * 跳转设备的类名
	 */
	private String  clzName;
	/**
	 * 是否选中
	 */
	private int checkbox;
	/**
	 * 设备的ICO地址
	 */
	private int icoflag;


	/**
	 * 设备的自定义名称
	 */
	private String custName;


	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}

	public String getMacadderss() {
		return macadderss;
	}

	public void setMacadderss(String macadderss) {
		this.macadderss = macadderss;
	}

	public String getDevnum() {
		return devnum;
	}

	public void setDevnum(String devnum) {
		this.devnum = devnum;
	}

	public int getConnstatus() {
		return connstatus;
	}

	public void setConnstatus(int connstatus) {
		this.connstatus = connstatus;
	}

	public String getClzName() {
		return clzName;
	}

	public void setClzName(String clzName) {
		this.clzName = clzName;
	}

	public int getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(int checkbox) {
		this.checkbox = checkbox;
	}

	public int getIcoflag() {
		return icoflag;
	}

	public void setIcoflag(int icoflag) {
		this.icoflag = icoflag;
	}

	public int getUser_guid() {
		return user_guid;
	}

	public void setUser_guid(int user_guid) {
		this.user_guid = user_guid;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Device(int user_guid, String devname, String macadderss, String devnum, int connstatus, String clzName, int checkbox, int icoflag, String custName) {
		this.user_guid = user_guid;
		this.devname = devname;
		this.macadderss = macadderss;
		this.devnum = devnum;
		this.connstatus = connstatus;
		this.clzName = clzName;
		this.checkbox = checkbox;
		this.icoflag = icoflag;
		this.custName = custName;
	}

	public Device() {
	}

	public Device(Parcel source) {
		this.user_guid = source.readInt();
		this.devname = source.readString();
		this.macadderss = source.readString();
		this.devnum = source.readString();
		this.connstatus = source.readInt();
		this.clzName = source.readString();
		this.checkbox = source.readInt();
		this.icoflag = source.readInt();
		this.custName = source.readString();
	}


	public DeviceContentValues toDeviceContentValues(Device device){
		DeviceContentValues deviceContentValues=new DeviceContentValues();
		deviceContentValues.putUserGuid(device.getUser_guid());
		deviceContentValues.putCheckbox(device.getCheckbox());
		deviceContentValues.putClzname(device.getClzName());
		deviceContentValues.putConnstatus(device.getConnstatus());
		deviceContentValues.putMacadderss(device.getMacadderss());
		deviceContentValues.putIcoflag(device.getIcoflag());
		deviceContentValues.putDevname(device.getDevname());
		deviceContentValues.putDevnum(device.getDevnum());
		deviceContentValues.putCustName(device.getCustName());
		return deviceContentValues;
	}
	public DeviceContentValues toDeviceContentValues(){
		DeviceContentValues deviceContentValues=new DeviceContentValues();
		deviceContentValues.putUserGuid(this.getUser_guid());
		deviceContentValues.putCheckbox(this.getCheckbox());
		deviceContentValues.putClzname(this.getClzName());
		deviceContentValues.putConnstatus(this.getConnstatus());
		deviceContentValues.putMacadderss(this.getMacadderss());
		deviceContentValues.putIcoflag(this.getIcoflag());
		deviceContentValues.putDevname(this.getDevname());
		deviceContentValues.putDevnum(this.getDevnum());
		deviceContentValues.putCustName(this.getCustName());
		return deviceContentValues;
	}

	public Device toDevice(DeviceCursor deviceCursor){
		this.setUser_guid(deviceCursor.getUserGuid());
		this.setClzName(deviceCursor.getClzname());
		this.setCheckbox(deviceCursor.getCheckbox());
		this.setConnstatus(deviceCursor.getConnstatus());
		this.setDevname(deviceCursor.getDevname());
		this.setDevnum(deviceCursor.getDevnum());
		this.setIcoflag(deviceCursor.getIcoflag());
		this.setMacadderss(deviceCursor.getMacadderss());
		this.setCustName(deviceCursor.getCustName());
		return this;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.user_guid );
		dest.writeString(this.devname);
		dest.writeString(this.macadderss);
		dest.writeString(this.devnum);
		dest.writeInt(this.connstatus);
		dest.writeString(this.clzName);
		dest.writeInt(this.checkbox);
		dest.writeInt(this.icoflag);
		dest.writeString(this.custName);
	}

	public static final Creator<Device> CREATOR = new Creator<Device>() {

		@Override
		public Device createFromParcel(Parcel source) {
			return new Device(source);
		}

		@Override
		public Device[] newArray(int size) {
			return new Device[size];
		}

	};

	@Override
	public String toString() {
		return "Device{" +
				"user_guid=" + user_guid +
				", devname='" + devname + '\'' +
				", macadderss='" + macadderss + '\'' +
				", devnum='" + devnum + '\'' +
				", connstatus=" + connstatus +
				", clzName='" + clzName + '\'' +
				", checkbox=" + checkbox +
				", icoflag=" + icoflag +
				", custName='" + custName + '\'' +
				'}';
	}
}
