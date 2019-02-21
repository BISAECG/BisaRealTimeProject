package com.bisa.health.service;
import com.bisa.health.service.IEcgServiceCallBacks;
import com.bisa.health.ecg.config.ECGConfig;
interface IEcgService{ 
  	void uiRegisterCallBacks(IEcgServiceCallBacks iEcgCallBacks);
	void uiDeviceConnected(String address,String devicename);
	void uiDeviceClose();
	void uiECGConfig(in ECGConfig config);
	void uiNotifiMarker(boolean isMarker);
	void uiInitDrawParam();
	String startMonitor(long timeMillis);
	String endMonitor();
	void startForeground();
	void stopForeground();
}