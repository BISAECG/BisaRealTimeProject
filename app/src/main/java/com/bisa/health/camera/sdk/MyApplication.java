package com.bisa.health.camera.sdk;

import android.app.Application;

import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.sdk.download.XDownloadFileManager;


public class MyApplication extends Application {

    private static MyApplication application;

    public static MyApplication getInstance() {
        return application;
    }
	@Override
	public void onCreate() {
		super.onCreate();
		
		/**
		 * 以下是FunSDK初始化
		 */
		FunSupport.getInstance().init(this);
		
		/**
		 * 以下是网络图片下载等的本地缓存初始化,可以加速图片显示,和节省用户流量
		 * 跟FunSDK无关,只跟com.example.download内容相关
		 */
		String cachePath = FunPath.getCapturePath();
		XDownloadFileManager.setFileManager(
				cachePath, 				// 缓存目录
				20 * 1024 * 1024		// 20M的本地缓存空间
				);
	}

	public void exit() {

		FunSupport.getInstance().term();
	}
	
}
