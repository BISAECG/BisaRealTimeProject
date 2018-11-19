package com.bisa.health.service;
import com.bisa.health.service.IEcgServiceCallBacks;
interface IEcgService{ 
  	void uiRegisterCallBacks(IEcgServiceCallBacks iEcgCallBacks);
	void uiDeviceConnected(String address,String devicename);
	void uiDeviceClose();
	void uiNotifiMarker(boolean isMarker);
	void uiInitDrawParam();
	String startMonitor(long timeMillis);
	String endMonitor();
}