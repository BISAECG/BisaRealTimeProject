package com.bisa.health;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.config.ECGSetConfig;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.FileIOKit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.android.otherPush.StubAppUtils;
import com.tencent.android.tpush.XGPushConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class BisaApplication extends Application {

	private static final String TAG = "BisaApplication";
	private String APP_PATH;
	private SharedPersistor sharedPersistor;
    private Locale locale = Locale.getDefault();
	private AppManager appManager=AppManager.getAppManager();
	private HealthServer mHealthServer=null;
	public  ECGSetConfig ecgSetConfig=null;
	@Override
	public void onCreate() {
		super.onCreate();

		if(sharedPersistor==null)
			sharedPersistor=new SharedPersistor(this.getApplicationContext());
		sharedPersistor.init();
		mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
		APP_PATH= Environment.getExternalStorageDirectory().getPath() + "/bishealth";
		HealthPath healthPath=initSysPath(APP_PATH);
		sharedPersistor.saveObject(healthPath);

		Log.i(TAG, "onCreate: "+locale.getCountry());
		try {

			InputStream is=getAssets().open(locale.getCountry()+"_area.json");
            byte[] bytes=FileIOKit.FromInputStreamToByte(is);
			final Gson gson=new Gson();
			List<ServerDto> listArea=gson.fromJson(new String(bytes,"UTF-8"),new TypeToken<List<ServerDto>>(){}.getType());
			sharedPersistor.saveObject(ServerDto.class.getName(),listArea);

        }catch (IOException e){
			try{
				InputStream is=getAssets().open("US_area.json");
				byte[] bytes=FileIOKit.FromInputStreamToByte(is);
				final Gson gson=new Gson();
				List<ServerDto> listArea=gson.fromJson(new String(bytes,"UTF-8"),new TypeToken<List<ServerDto>>(){}.getType());
				sharedPersistor.saveObject(ServerDto.class.getName(),listArea);
			}catch (IOException ioe){
				return;
			}
        }
		ecgSetConfig=sharedPersistor.loadObject(ECGSetConfig.class.getName());
		if(ecgSetConfig==null){
			ecgSetConfig=new ECGSetConfig();
			sharedPersistor.saveObject(ecgSetConfig);
		}

		XGPushConfig.enableDebug(this,true);

		XGPushConfig.enableOtherPush(getApplicationContext(), true);
		XGPushConfig.setHuaweiDebug(true);

		XGPushConfig.setMiPushAppId(getApplicationContext(), "2882303761517852735");
		XGPushConfig.setMiPushAppKey(getApplicationContext(), "5611785210735");

        XGPushConfig.setMzPushAppId(getApplicationContext(), "1001673");
        XGPushConfig.setMzPushAppKey(getApplicationContext(), "1317acb21c2040448d555ea3d33b4a15");

		//CrashHandler.getInstance().init(this);
		
	}

	public  ECGSetConfig getEcgSetConfig() {
		return ecgSetConfig;
	}

	public  void setEcgSetConfig(ECGSetConfig ecgSetConfig) {
		this.ecgSetConfig = ecgSetConfig;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		System.exit(0);
		Log.i(TAG, "onTerminate: ");
	}

	@Override
	protected void attachBaseContext(Context base) {
		StubAppUtils.attachBaseContext(base);
		super.attachBaseContext(base);
	}

	protected HealthPath initSysPath(String aPath) {

		HealthPath healthPath=new HealthPath(aPath);

		File dirFile = new File(healthPath.getLog());
		Log.i(TAG,"create:"+aPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		dirFile = new File(healthPath.getSys());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		dirFile = new File(healthPath.getOtgzipdat());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		dirFile = new File(healthPath.getBackdat());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		dirFile = new File(healthPath.getUser());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		dirFile = new File(healthPath.getFreedat());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		dirFile = new File(healthPath.getFeepdf());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		clearInit(healthPath);
		return healthPath;
	}

	public void clearInit(HealthPath healthPath){
		if(healthPath!=null){

//			/**
//			 * 软件重新启动转移文件保证cache都是最新文件
//			 */
//			File tempFile=new File(systemDir.getUptemp());
//			File[] tempList=tempFile.listFiles();
//
//			if(tempFile.length()>0){
//				for(File f:tempList){
//					File ecdFile=new File(systemDir.getUpdat(),f.getName());
//					boolean result=FileIOKit.copyFileToFile(f,ecdFile);
//					if(result){
//						f.delete();
//					}else{
//						if(ecdFile.exists()){
//							ecdFile.delete();
//						}
//					}
//				}
//
//
//			}
			/**
			 * 清理90天以前的文件
			 */

			File updateFile=new File(healthPath.getFreedat());
			File[] updateList = updateFile.listFiles();

			Calendar cal = Calendar.getInstance(Locale.getDefault());
			int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
			int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
			cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
			long curGMTTime = cal.getTimeInMillis();
			if(updateList!=null) {
				for (File _f : updateList) {
					String[] datFileNameArray = _f.getName().split("\\.");
					if (datFileNameArray.length == 2) {
						String[] _datFileNameArray = datFileNameArray[0].split("\\_");
						try {

							SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
							Date datDate = df.parse(_datFileNameArray[1]);
							long datGMTTime = datDate.getTime();
							long offsetTime = 90l * 24l * 60l * 60l * 1000l;
							if (Math.abs(curGMTTime - datGMTTime) > offsetTime) {// 删除90天的前的DAT
								_f.delete();
							}

						} catch (Exception e) {
							_f.delete();
						}

					}
				}
			}
		}

	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {

		appManager.addActivity(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		return appManager.currentActivity();
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		appManager.finishActivity();
	}

	public Activity firstActivity(){
		return appManager.firstActivity();
	}
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		appManager.finishActivity(activity);
	}


	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class cls) {
		appManager.finishActivity(cls);
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		appManager.finishAllActivity();
	}

	/**
	 * 退出应用程序
	 */
	public void exitApp() {
			finishAllActivity();
	}
	public void restart(Activity context){
		finishAllActivity();
		ActivityUtil.startActivity(context,LoginActivity.class, ActionEnum.NULL);
	}

}
