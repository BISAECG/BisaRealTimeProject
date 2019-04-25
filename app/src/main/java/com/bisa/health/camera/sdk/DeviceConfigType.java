package com.bisa.health.camera.sdk;

import com.bisa.health.camera.lib.funsdk.support.config.AVEncVideoWidget;
import com.bisa.health.camera.lib.funsdk.support.config.AlarmOut;
import com.bisa.health.camera.lib.funsdk.support.config.CameraClearFog;
import com.bisa.health.camera.lib.funsdk.support.config.CameraParam;
import com.bisa.health.camera.lib.funsdk.support.config.CameraParamEx;
import com.bisa.health.camera.lib.funsdk.support.config.CloudStorage;
import com.bisa.health.camera.lib.funsdk.support.config.DetectBlind;
import com.bisa.health.camera.lib.funsdk.support.config.DetectMotion;
import com.bisa.health.camera.lib.funsdk.support.config.FVideoOsdLogo;
import com.bisa.health.camera.lib.funsdk.support.config.LocalAlarm;
import com.bisa.health.camera.lib.funsdk.support.config.PowerSocketArm;
import com.bisa.health.camera.lib.funsdk.support.config.PowerSocketImage;
import com.bisa.health.camera.lib.funsdk.support.config.RecordParam;
import com.bisa.health.camera.lib.funsdk.support.config.RecordParamEx;
import com.bisa.health.camera.lib.funsdk.support.config.SimplifyEncode;

public class DeviceConfigType {
	//通道相关的配置类型
	public static final String[] DeviceConfigByChannel = {
			CameraParam.CONFIG_NAME,
			CameraParamEx.CONFIG_NAME,
			AVEncVideoWidget.CONFIG_NAME ,
			DetectMotion.CONFIG_NAME,
			DetectBlind.CONFIG_NAME,
			LocalAlarm.CONFIG_NAME,
			RecordParam.CONFIG_NAME,
			RecordParamEx.CONFIG_NAME,
			CameraClearFog.CONFIG_NAME,
	};
	//通道无关的配置类型
	public static final String[] DeviceConfigCommon = {
			CloudStorage.CONFIG_NAME,
			PowerSocketImage.CONFIG_NAME,
			SimplifyEncode.CONFIG_NAME,
			FVideoOsdLogo.CONFIG_NAME,
			PowerSocketArm.CONFIG_NAME,
			AlarmOut.CONFIG_NAME,
	};
}
