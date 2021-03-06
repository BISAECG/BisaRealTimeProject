package com.bisa.health.camera.lib.funsdk.support;

import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;

/**
 * 设备WiFi配置成功
 * @author Administrator
 *
 */
public interface OnFunDeviceWiFiConfigListener extends OnFunListener {

	// 设备WiFi配置成功
	void onDeviceWiFiConfigSetted(FunDevice funDevice);
	
}
