package com.bisa.health.model;

import com.bisa.health.R;
import com.bisa.health.ecg.ECGActivity;

public enum BleNamePreSuffix {

	BLE_ECG("HC+", R.string.xixin_ecg,0, ECGActivity.class.getName(), R.drawable.ecg_ico), BLE_CAMERA("CC+",  R.string.xixin_camera, 0, null,
			R.drawable.ico_camera);
	private String PreSuffix;
	private int DevName;
	private int ConnStatus;
	private String  clzName;
	private int resource;

	public String getPreSuffix() {
		return PreSuffix;
	}

	public void setPreSuffix(String preSuffix) {
		PreSuffix = preSuffix;
	}

	public int getDevName() {
		return DevName;
	}

	public void setDevName(int devName) {
		DevName = devName;
	}

	public int getConnStatus() {
		return ConnStatus;
	}

	public void setConnStatus(int connStatus) {
		ConnStatus = connStatus;
	}

	public String getClzName() {
		return clzName;
	}

	public void setClzName(String clzName) {
		this.clzName = clzName;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	private BleNamePreSuffix(String PreSuffix, int DevName, int ConnStatus, String clzName, int resource) {
		this.PreSuffix = PreSuffix;
		this.DevName = DevName;
		this.ConnStatus = ConnStatus;
		this.clzName = clzName;
		this.resource = resource;
	}

	// 手写的从int到enum的转换函数
	public static int valueOf(int index, String BleName) {

		if(BleName==null){
			return -1;
		}

		switch (index) {
		case 0:
			return BleName.indexOf(BLE_ECG.getPreSuffix());
		case 1:
			return BleName.indexOf(BLE_CAMERA.getPreSuffix());
		default:
			return -1;
		}
	}

	// 手写的从int到enum的转换函数
	public static BleNamePreSuffix valueOf(int index) {

		switch (index) {
		case 0:
			return BLE_ECG;
		case 1:
			return BLE_CAMERA;
		default:
			return null;
		}
	}

}
