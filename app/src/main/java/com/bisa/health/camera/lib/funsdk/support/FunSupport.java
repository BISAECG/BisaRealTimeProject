package com.bisa.health.camera.lib.funsdk.support;

import android.annotation.SuppressLint;
import android.content.Context;

import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.basic.G;
import com.bisa.health.BisaApp;
import com.bisa.health.camera.interfaces.CameraUpgradeListener;
import com.bisa.health.camera.lib.funsdk.support.config.AlarmInfo;
import com.bisa.health.camera.lib.funsdk.support.config.BaseConfig;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdGeneral;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdOPFileQueryJP;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdOPRemoveFileJP;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdOPSCalendar;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdSearchFileNumJP;
import com.bisa.health.camera.lib.funsdk.support.config.DeviceGetJson;
import com.bisa.health.camera.lib.funsdk.support.config.JsonConfig;
import com.bisa.health.camera.lib.funsdk.support.config.OPCompressPic;
import com.bisa.health.camera.lib.funsdk.support.config.SameDayPicInfo;
import com.bisa.health.camera.lib.funsdk.support.config.SystemInfo;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevRecordFile;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevStatus;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevType;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunDeviceBuilder;
import com.bisa.health.camera.lib.funsdk.support.models.FunDeviceSocket;
import com.bisa.health.camera.lib.funsdk.support.models.FunLoginType;
import com.bisa.health.camera.lib.funsdk.support.utils.DeviceWifiManager;
import com.bisa.health.camera.lib.funsdk.support.utils.FileUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.SharedParamMng;
import com.bisa.health.camera.lib.funsdk.support.utils.StringUtils;
import com.bisa.health.camera.lib.sdk.bean.DSTimeBean;
import com.bisa.health.camera.lib.sdk.bean.DayLightTimeBean;
import com.bisa.health.camera.lib.sdk.bean.DownloadInfo;
import com.bisa.health.camera.lib.sdk.bean.HandleConfigData;
import com.bisa.health.camera.lib.sdk.bean.LocationBean;
import com.bisa.health.camera.lib.sdk.bean.TimeZoneBean;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.bisa.health.camera.lib.sdk.struct.ManualSnapModeJP;
import com.bisa.health.camera.lib.sdk.struct.OPRemoveFileJP;
import com.bisa.health.camera.lib.sdk.struct.SDBDeviceInfo;
import com.bisa.health.camera.lib.sdk.struct.SDK_Authority;
import com.bisa.health.camera.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.bisa.health.camera.lib.sdk.struct.SDK_ChannelNameConfigAll;
import com.bisa.health.camera.lib.sdk.struct.SDK_SearchByTime;
import com.bisa.health.camera.lib.sdk.struct.SDK_TitleDot;
import com.bisa.health.camera.lib.sdk.struct.SInitParam;

import com.bisa.health.camera.sdk.XUtils;
import com.lib.ECONFIG;
import com.lib.EDEV_ATTR;
import com.lib.EDEV_JSON_ID;
import com.lib.EDEV_OPTERATE;
import com.lib.EFUN_ATTR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.Mps.MpsClient;
import com.lib.Mps.SMCInitInfo;
import com.lib.Mps.XPMS_SEARCH_ALARMINFO_REQ;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.SDKCONST.SDK_CommTypes;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.lib.EUIMSG.DEV_SLEEP;


public class FunSupport implements IFunSDKResult {

    private static final String TAG = "FunSupport";

    // 应用证书,请在开放平台注册应用之后替换以下4个字段
    private static final String APP_UUID = "0f16ed53820847ddb0b148ffa876e7d8";
    private static final String APP_KEY = "aea2d15387514d09bc86d6b29c9e1c0e";
    private static final String APP_SECRET = "7eace38822b54272acee15de6726f764";
    private static final int APP_MOVECARD = 5;

    // "42.96.197.189";223.4.33.127
    public static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";
    public static final int SERVER_PORT = 15010; // 更新版本的服务器端口

    private static FunSupport mInstance;

    private Context mContext;
    private SharedParamMng mSharedParam;

    private final String SHARED_PARAM_KEY_AUTOLOGIN = "SHARED_PARAM_KEY_AUTOLOGIN";
    private final String SHARED_PARAM_KEY_SAVEPASSWORD = "SHARED_PARAM_KEY_SAVEPASSWORD";
    private final String SHARED_PARAM_KEY_SAVENATIVEPASSWORD = "SHARED_PARAM_KEY_SAVENATIVEPASSWORD";

    private int mFunUserHandler = -1;

    private String mLoginUserName = null; // 当前登录的用户名称
    private String mLoginPassword = null; // 当前登录的用户密码

    private String mTmpLoginUserName = null; // 临时用户名称,仅用于记录后保存
    private String mTmpLoginPassword = null; // 临时密码,仅用于记录后保存

    private boolean mSavePasswordAfterLogin; // 登录后是否保存密码
    private boolean mAutoLoginWhenStartup; // App启动后是否自动登录
    private boolean mSaveNativePassword;   //是否使用本地保存密码

    private String mLastMpsUserName = null; // 最近一次启动了报警推送消息接收的用户

    private FunLoginType mLoginType = null;

    private int mVerificationPassword = 2;
    public String NativeLoginPsw; //本地密码

    // 用户设备列表
    private List<FunDevice> mDeviceList = new ArrayList<FunDevice>();

    // 附近的设备列表
    private List<FunDevice> mAPDeviceList = new ArrayList<FunDevice>();

    // 局域网内的设备列表
    private List<FunDevice> mLanDeviceList = new ArrayList<FunDevice>();

    // 临时设备列表,用户临时保存通过序列号登录的设备
    private List<FunDevice> mTmpSNLoginDeviceList = new ArrayList<FunDevice>();



    public void deviceListDelDevice(String sn) {
        for(FunDevice funDevice : mDeviceList) {
            if(sn.equals(funDevice.getDevSn())) {
                mDeviceList.remove(funDevice);
                return;
            }
        }
    }


    // 当前登录的或者使用的设备
    public FunDevice mCurrDevice;

    // 内部使用消息定义
    private final int MESSAGE_AP_DEVICE_LIST_CHANGED = 0x1000;
    private final int MESSAGE_GET_DEVICE_CONFIG = 0x1001;
    private final int MESSAGE_GET_DEVICE_CONFIG_TIMEOUT = 0x1002;

    private class DeviceGetConfig {
        FunDevice funDevice;
        String configName;
        int channelNo = -1;

        DeviceGetConfig(FunDevice dev, String cfg) {
            funDevice = dev;
            configName = cfg;
            channelNo = -1;
        }

        DeviceGetConfig(FunDevice dev, String cfg, int channel) {
            funDevice = dev;
            configName = cfg;
            channelNo = channel;
        }
    }

    private List<DeviceGetConfig> mDeviceGetConfigQueue = new ArrayList<DeviceGetConfig>();
    private boolean mIsGettingConfig = false;

    private FunSupport() {

    }

    private List<OnFunListener> mListeners = new ArrayList<OnFunListener>();


    public static FunSupport getInstance() {
        if (null == mInstance) {
            synchronized (FunSupport.class) {
                mInstance = new FunSupport();
            }
        }
        return mInstance;
    }

    public void deviceListAdd(FunDevice funDevice) {
        mDeviceList.add(funDevice);
    }
    public void deviceListClear() {
        mDeviceList.clear();
    }

    public void init(Context context) {
        int result = 0;

        mContext = context;

        // 初始化目录
        FunPath.init(context, context.getPackageName());

        mSharedParam = new SharedParamMng(context);
        // 导入保存的参数配置
        loadParams();

        // 库初始化1
        SInitParam param = new SInitParam();
        param.st_0_nAppType = SInitParam.LOGIN_TYPE_MOBILE;
        result = FunSDK.Init(0, G.ObjToBytes(param));

        // Please set the password prefix here
//		result = FunSDK.InitExV2(0, G.ObjToBytes(param), 4, "GIGA_", "cloudgiga.com.br", 8765);
        //FunLog.i(TAG, "FunSDK.Init:" + result);
        //set user server IP Port
//		result = FunSDK.SysSetServerIPPort("MI_SERVER", "cloudgiga.com.br", 80);
//		FunLog.i(TAG, "FunSDK.InitServerIPPort:" + result);

        // 降低隐藏到后台时cpu使用及耗电
        FunSDK.SetApplication((BisaApp)mContext.getApplicationContext());
        // 库初始化2
        FunSDK.MyInitNetSDK();

        // 设置临时文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, FunPath.getDefaultPath());
        // 设置设备更新文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.UPDATE_FILE_PATH, FunPath.getDeviceUpdatePath());
		// 设置SDK相关配置文件保存路径
		FunSDK.SetFunStrAttr(EFUN_ATTR.CONFIG_PATH,FunPath.getDeviceConfigPath());
        // 设置以互联网的方式访问
        result = FunSDK.SysInitNet(SERVER_IP, SERVER_PORT);
        //FunLog.i(TAG, "FunSDK.SysInitNet : " + result);

        // 初始化APP证书(APP启动后调用一次即可)
        FunSDK.XMCloundPlatformInit(
                APP_UUID,        // uuid
                APP_KEY, // App Key
                APP_SECRET, // App Secret
                APP_MOVECARD); // moveCard

        // 创建/注册库接口操作句柄
        mFunUserHandler = FunSDK.RegUser(this);
        //FunLog.i(TAG, "FunSDK.RegUser : " + mFunUserHandler);

        // 注册设备连接和断开的消息监听
        result = FunSDK.SetFunIntAttr(EFUN_ATTR.FUN_MSG_HANDLE, mFunUserHandler);
        //FunLog.i(TAG, "FunSDK.SetFunIntAttr(EFUN_ATTR.FUN_MSG_HANDLE) : " + result);

        //FunSDK.LogInit(mFunUserHandler, "", 1, "", 1);

        /**
         * 以下是登陆设备密码保存模式初始化
         **/
        //该保存方式是使用底层库函数将密码保存到本地
        if (getSaveNativePassword()) {
            FunSDK.SetFunStrAttr(EFUN_ATTR.USER_PWD_DB, FunPath.getConfigPassword());
            //System.out.println("NativePasswordFileName" + FunPath.getConfigPassword());
        }
    }

    public void term() {
        if (mFunUserHandler >= 0) {
            FunSDK.UnRegUser(mFunUserHandler);
            mFunUserHandler = -1;
        }

        FunSDK.MyUnInitNetSDK();

        FileUtils.cleanFolder(FunPath.PATH_CAPTURE_TEMP);

        FunSDK.UnInit(0);
    }

    public void requestOtherDevicesLogin(FunDevice exceptFunDevice) {
        for(FunDevice funDevice : mDeviceList) {
            if(!funDevice.equals(exceptFunDevice)) {
                if(!funDevice.hasLogin() || !funDevice.hasConnected()) {
                    requestDeviceLogin(funDevice);
                }
            }
        }
    }

    public Context getContext() {
        return mContext;
    }

    public int getHandler() {
        return mFunUserHandler;
    }

    public String getString(Integer strResId) {
        if (null != mContext) {
            return mContext.getResources().getString(strResId);
        }
        return "";
    }

    public DeviceWifiManager getDeviceWifiManager() {
        return DeviceWifiManager.getInstance(getContext());
    }

    public boolean isAPDeviceConnected(FunDevice funDevice) {
        if (null == funDevice || null == funDevice.devName) {
            return false;
        }

        String curSSID = getDeviceWifiManager().getSSID();
        if (null == curSSID) {
            return false;
        }

        return curSSID.equals(funDevice.devName);
    }

    // SetFunStrAttr(EFUN_ATTR.APP_PATH)
    public void setAppPath(String path) {
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, path);
    }

    public void setLoginType(FunLoginType loginType) {
        if (loginType == FunLoginType.LOGIN_BY_AP) {
            // FunSDK.SysInitLocal(mDefaultPath);
            FunSDK.SysInitAsAPModle(FunPath.getDeviceApPath());
        } else if (loginType == FunLoginType.LOGIN_BY_LOCAL) {
            FunSDK.SysInitLocal(FunPath.getLocalDB());
        } else {
            FunSDK.SysInitNet(SERVER_IP, SERVER_PORT);
        }
        this.mLoginType = loginType;
    }

    public FunLoginType getLoginType() {
        return mLoginType;
    }



    public void registerOnFunDeviceListener(OnFunDeviceListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceOptListener(OnFunDeviceOptListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }


    public void registerOnFunDeviceCaptureListener(OnFunDeviceCaptureListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceFileListener(OnFunDeviceFileListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceTalkListener(OnFunDeviceTalkListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceAlarmListener(OnFunDeviceAlarmListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceSerialListener(OnFunDeviceSerialListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceWiFiConfigListener(OnFunDeviceWiFiConfigListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceRecordListener(OnFunDeviceRecordListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceConnectListener(OnFunDeviceConnectListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnAddSubDeviceResultListener(OnAddSubDeviceResultListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }

    public void registerOnFunDeviceWakeUpListener(OnFunDeviceWakeUpListener ls) {
        if (!mListeners.contains(ls)) {
            mListeners.add(ls);
        }
    }

    public void registerFunVideoViewListener(FunVideoViewListener ls) {
        if (!mListeners.contains(ls)) {
            mListeners.add(ls);
        }
    }


    public void removeFunVideoViewListener(FunVideoViewListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }


    public void removeOnFunDeviceListener(OnFunDeviceListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceOptListener(OnFunDeviceOptListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }


    public void removeOnFunDeviceCaptureListener(OnFunDeviceCaptureListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceFileListener(OnFunDeviceFileListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceTalkListener(OnFunDeviceTalkListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceAlarmListener(OnFunDeviceAlarmListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceSerialListener(OnFunDeviceSerialListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceWiFiConfigListener(OnFunDeviceWiFiConfigListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceRecordListener(OnFunDeviceRecordListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceConnectListener(OnFunDeviceConnectListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnAddSubDeviceResultListener(OnAddSubDeviceResultListener l) {
        if (!mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public void removeOnFunDeviceWakeUpListener(OnFunDeviceWakeUpListener ls) {
        if (!mListeners.contains(ls)) {
            mListeners.remove(ls);
        }
    }

    /**
     * 保存配置,是否应用启动后自动登录
     *
     * @param auto 是否自动登录
     */
    public void setAutoLogin(boolean auto) {
        mAutoLoginWhenStartup = auto;
        mSharedParam.setBooleanUserValue(
                SHARED_PARAM_KEY_AUTOLOGIN, auto);
    }

    /**
     * 获取是否自动登录
     *
     * @return
     */
    public boolean getAutoLogin() {
        return mAutoLoginWhenStartup;
    }

    /**
     * 保存配置，是否使用登录密码本地保存
     *
     * @param is
     */
    public void setSaveNativePassword(boolean is) {
        mSaveNativePassword = is;
        mSharedParam.setBooleanUserValue(
                SHARED_PARAM_KEY_SAVENATIVEPASSWORD, is);
    }

    /**
     * 获取是否使用本地保存密码
     *
     * @return
     */
    public boolean getSaveNativePassword() {
        return mSaveNativePassword;
    }

    /**
     * 保存配置,是否登录后保存密码
     *
     * @param save
     */
    public void setSavePasswordAfterLogin(boolean save) {
        mSavePasswordAfterLogin = save;
        mSharedParam.setBooleanUserValue(
                SHARED_PARAM_KEY_SAVEPASSWORD, save);
    }

    /**
     * 获取是否保存密码
     *
     * @return
     */
    public boolean getSavePasswordAfterLogin() {
        return mSavePasswordAfterLogin;
    }

    /**
     * 获取最近一次登录的用户名
     *
     * @return
     */
    public String getSavedUserName() {
        return FunLoginHistory.getInstance().getLastLoginUserName();
    }

    /**
     * 获取最近一次登录用户名的密码
     *
     * @return
     */
    public String getSavedPassword() {
        return getSavedPassword(getSavedUserName());
    }

    /**
     * 获取登录过的所有用户名
     *
     * @return
     */
    public List<String> getSavedUserNames() {
        return FunLoginHistory.getInstance().getAllUserNames();
    }

    /**
     * 获取用户的密码(本地已保存的)
     *
     * @param userName
     * @return
     */
    public String getSavedPassword(String userName) {
        return FunLoginHistory.getInstance().getPassword(userName);
    }

    private void loadParams() {
        try {
            mAutoLoginWhenStartup = mSharedParam.getBooleanUserValue(SHARED_PARAM_KEY_AUTOLOGIN, true);
            mSavePasswordAfterLogin = mSharedParam.getBooleanUserValue(SHARED_PARAM_KEY_SAVEPASSWORD, true);
            mSaveNativePassword = mSharedParam.getBooleanUserValue(SHARED_PARAM_KEY_SAVENATIVEPASSWORD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDeviceList(byte[] pData) {
        mDeviceList.clear();
        if (null == pData || pData.length <= 0) {
            return;
        }

        try {
            SDBDeviceInfo info = new SDBDeviceInfo();
            int nItemLen = G.Sizeof(info);
            int nCount = pData.length / nItemLen;
            SDBDeviceInfo infos[] = new SDBDeviceInfo[nCount];
            for (int i = 0; i < nCount; ++i) {
                infos[i] = new SDBDeviceInfo();
            }
            G.BytesToObj(infos, pData);
            for (int i = 0; i < nCount; ++i) {
                FunDevType devType = FunDevType.getType(infos[i].st_7_nType);

                // 根据设备类型创建设备对象
                FunDevice funDevice = FunDeviceBuilder.buildWith(devType);
                // 初始化设备参数
                funDevice.initWith(infos[i]);
                // 添加到设备列表
                mDeviceList.add(funDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dumpDeviceList();
    }

    private void updateLanDeviceList(SDK_CONFIG_NET_COMMON_V2[] searchResult) {
        mLanDeviceList.clear();

        if (null != searchResult) {
            for (SDK_CONFIG_NET_COMMON_V2 com : searchResult) {
                addLanDevice(com);
            }
        }
    }

    private FunDevice addLanDevice(SDK_CONFIG_NET_COMMON_V2 comm) {
        FunDevice funDevice = null;
        synchronized (mLanDeviceList) {
            String devSn = G.ToString(comm.st_14_sSn);

            if (null != devSn) {
                if (null == (funDevice = findLanDevice(devSn))) {
                    // 根据设备类型,新建一个对象
                    FunDevType devType = FunDevType.getType(comm.st_15_DeviceType);

                    funDevice = FunDeviceBuilder.buildWith(devType);

                    funDevice.initWith(comm);

                    mLanDeviceList.add(funDevice);
                }
            }
        }

        return funDevice;
    }

    public void dumpDeviceList() {
        FunLog.d(TAG, "DeviceList:" + mDeviceList.size());
        for (FunDevice devInfo : mDeviceList) {
            FunLog.d(TAG, "     dev : " + devInfo.toString());
        }
    }



    /********************************************************************************
     * 设备访问相关接口
     */
    /**
     * 请求用户获取设备列表
     *
     * @return 请求是否成功
     */
    public boolean requestDeviceList() {
        FunLog.d(TAG, "---> call requestDeviceList()");
        int result = FunSDK.SysGetDevList(getHandler(),
                mLoginUserName, mLoginPassword, 0);
        return (result == 0);
    }

    /**
     * 设备重命名
     *
     * @param funDevice
     * @param newDevName
     * @return
     */
    public boolean requestDeviceRename(FunDevice funDevice, String newDevName) {
        funDevice.devName = newDevName;
        SDBDeviceInfo devInfo = funDevice.toSDBDeviceInfo();
        int result = FunSDK.SysChangeDevInfo(getHandler(),
                G.ObjToBytes(devInfo),
                mLoginUserName, mLoginPassword, funDevice.getId());
        return (result == 0);
    }


    public boolean requestDeviceAddSubDev(FunDevice funDevice, String Command, String pconfig) {
        System.out.println("zyy----------pconfig   " + pconfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), funDevice.devSn, Command, pconfig, -1, 5000, funDevice.getId());
        return true;
    }


    public void requestControlSubDevice(FunDevice funDevice, String Command, String pconfig) {
        System.out.println("zyy----------pconfig   " + pconfig.toString());
        FunSDK.DevSetConfigByJson(getHandler(), funDevice.devSn, Command, pconfig, -1, 5000, funDevice.getId());
    }



    /**
     * 请求获取附近的AP设备列表
     *
     * @return
     */
    public boolean requestAPDeviceList() {
        new Thread() {

            @Override
            public void run() {

                // 如果WIFI未打开,先打开WIFI
                if (!DeviceWifiManager.getInstance(getContext()).isWiFiEnabled()) {
                    DeviceWifiManager.getInstance(getContext()).openWifi();
                }

                // 搜索WIFI信号10秒,为了提高用户体验效果,分多次扫描
                final int scanSecond = 10;
                final int ONE_SECOND = 1000;

                for (int i = 0; i < scanSecond; i++) {
                    int nAPDevChanged = 0;

                    DeviceWifiManager.getInstance(getContext()).startScan(
                            DeviceWifiManager.WIFI_TYPE.DEV_AP, ONE_SECOND);

                    List<ScanResult> scanResults = DeviceWifiManager.getInstance(getContext()).getWifiList();
                    // 先删除扫描列中已经不存在的WiFi设备
                    for (int iDev = mAPDeviceList.size() - 1; iDev >= 0; iDev--) {
                        FunDevice funDevice = mAPDeviceList.get(iDev);
                        boolean isExist = false;
                        for (ScanResult scanResult : scanResults) {
                            if (scanResult.SSID.equals(funDevice.devName)) {
                                // 存在
                                isExist = true;
                                break;
                            }
                        }

                        if (!isExist) {
                            // 不存在了,从列表移除
                            FunLog.e("AP", "AP Deice offline : " + funDevice.devName);
                            mAPDeviceList.remove(iDev);
                            nAPDevChanged++;
                        }
                    }

                    String currSSID = DeviceWifiManager.getInstance(getContext()).getSSID();
                    String currGwIp = DeviceWifiManager.getInstance(getContext()).getGatewayIp();
                    for (ScanResult scanResult : scanResults) {
                        String ssid = scanResult.SSID.trim();
                        String bssid = scanResult.BSSID;
                        FunDevice funDevice = findAPDevice(ssid);
                        if (null != funDevice) {
                            // 已经存在了,如果有需要可以在这里更新设备信息
                        } else {
                            // 不存在添加一个新的AP设备到列表
                            int typeIndex = DeviceWifiManager.getXMDeviceAPType(ssid);
                            FunDevType devType = FunDevType.getType(typeIndex);
                            funDevice = FunDeviceBuilder.buildWith(devType);
                            funDevice.initWith(devType, ssid, bssid);
                            if (ssid.equals(currSSID) && null != currGwIp) {
                                funDevice.devIp = currGwIp;
                            }

//							SDBDeviceInfo devInf = funDevice.toSDBDeviceInfo();
//							FunLog.i("test", "AddDevice:" + devInf.toString());
                            // FunSDK.SysAddDevice(getHandler(),
                            // G.ObjToBytes(devInf),
//									funDevice.loginName, funDevice.loginPsw, mAPDeviceList.size());

                            mAPDeviceList.add(funDevice);
                            nAPDevChanged++;
                        }
                    }

                    // 总共有nAPDevChanged个设备发生了变化
                    if (i == 0 || nAPDevChanged > 0) {
                        // 监听通知
                        if (null != mHandler) {
                            mHandler.sendEmptyMessage(MESSAGE_AP_DEVICE_LIST_CHANGED);
                        }
                    }
                }
            }

        }.start();


        return true;
    }

    public boolean requestLanDeviceList() {
        int result = FunSDK.DevSearchDevice(getHandler(), 10000, 0);
        return (result == 0);
    }

    /**
     * 开始WiFi快速配置
     *
     * @param ssid
     * @param data
     * @param info
     * @param gw_ipaddr
     * @param type
     * @param isbroad
     * @param mac
     * @param nTimeout
     * @return
     */
    public boolean startWiFiQuickConfig(String ssid,
                                        String data, String info, String gw_ipaddr, int type, int isbroad, String mac, int nTimeout) {
        int nGetType = 2; // 2代配置
        int result = FunSDK.DevStartAPConfig(getHandler(),
                nGetType,
                ssid, data, info, gw_ipaddr, type, isbroad, mac, nTimeout);
        return (result == 0);
    }

    /**
     * 快速配置停止
     */
    public void stopWiFiQuickConfig() {
        FunSDK.DevStopAPConfig();
    }

    /**
     * 串口命令透传-打开串口
     *
     * @param funDevice
     * @return
     */
    public boolean transportSerialOpen(FunDevice funDevice) {
        int result = FunSDK.DevOption(getHandler(), // userId句柄
                funDevice.getDevSn(), // 设备序列号
                EDEV_OPTERATE.EDOPT_DEV_OPEN_TANSPORT_COM, // 打开串口命令
                null,
                0,
                SDK_CommTypes.SDK_COMM_TYPES_RS485,
                //			SDK_CommTypes.SDK_COMM_TYPES_RS232,
                0,
                0,
                "COM_OPEN",
                funDevice.getId());
        return (result == 0);
    }

    /**
     * 串口命令透传-关闭串口
     *
     * @param funDevice
     * @return
     */
    public boolean transportSerialClose(FunDevice funDevice) {
        int result = FunSDK.DevOption(getHandler(),
                funDevice.getDevSn(),
                EDEV_OPTERATE.EDOPT_DEV_CLOSE_TANSPORT_COM,
                null, 0,
                SDK_CommTypes.SDK_COMM_TYPES_RS485, 0, 0,
                "COM_CLOSE", funDevice.getId());
        return (result == 0);
    }

    /**
     * 串口命令透传-发送数据
     *
     * @param funDevice
     * @param pData
     * @return
     */
    public boolean transportSerialWrite(FunDevice funDevice, byte[] pData) {
        int result = FunSDK.DevOption(getHandler(),
                funDevice.getDevSn(),
                EDEV_OPTERATE.EDOPT_DEV_TANSPORT_COM_WRITE,
                pData, pData.length,
                SDK_CommTypes.SDK_COMM_TYPES_RS485,
                //			SDK_CommTypes.SDK_COMM_TYPES_RS232,
                getHandler(), 0,
                "COM_WRITE", funDevice.getId());
        return (result == 0);
    }

    /**
     * 串口命令透传-发送数据(字符串形式)
     *
     * @param funDevice
     * @param cmdStr
     * @return
     */
    public boolean transportSerialWrite(FunDevice funDevice, String cmdStr) {
        return transportSerialWrite(funDevice, cmdStr.getBytes());
    }

    /**
     * 设备登录(请求)
     *
     * @param funDevice
     * @return
     */
    public boolean requestDeviceLogin(FunDevice funDevice) {
        if (null == funDevice) {
            return false;
        }

        String loginName = (null == funDevice.loginName) ? "admin" : funDevice.loginName;
        String loginPsd = (null == funDevice.loginPsw) ? "" : funDevice.loginPsw;
        // 使用之前保存的密码,非默认密码了,密码保存的位置,可以根据需求设计,DEMO里面是保存在一个文件中,参考FunDevicePassword.java
        //String devicePasswd = FunDevicePassword.getInstance().getDevicePassword(funDevice.getDevSn());

        //if (devicePasswd != null) {
            //loginPsd = devicePasswd;
        //}

//			switch (mVerificationPassword) {
//			case 2:					//验证本地密码
//				if (devicePasswd != null) {
//				    loginPsd = devicePasswd;
//				}
//				if (funDevice.servicepsd()) {
//					mVerificationPassword--;
//				}
//				mVerificationPassword--;
//				break;
//			case 1:					//验证funDevice密码
//				mVerificationPassword--;
//				funDevice.setServicepsd(true);		//funDevice密码已验证标志
//				break;
//			default:
//				break;
//			}

//			if (loginPsd.equals("") && null != devicePasswd) {
//
//				loginPsd = devicePasswd;
////				funDevice.loginPsw = loginPsd;
//			}

        //NativeLoginPsw = loginPsd;

        //if (null == findDeviceById(funDevice.getId())) {
            // 如果登录的设备不存在,添加一个临时设备在列表里面,方便后续回调处理
            //mTmpSNLoginDeviceList.add(funDevice);
        //}
        //FunSDK.DevSetLocalPwd(funDevice.getDevSn(),loginName,loginPsd);
        int result = FunSDK.DevLogin(getHandler(),
                funDevice.getDevSn(),
                loginName, loginPsd,
                funDevice.getId());

        return (result == 0);
    }

    /**
     * 获取通道信息
     *
     * @param
     */
    public void requestGetDevChnName(FunDevice funDevice) {
        FunSDK.DevGetChnName(getHandler(), funDevice.getDevSn(), "", "", funDevice.getId());

    }


    /**
     * 设备登录(通过序列号直接登录)
     *
     * @param devMac
     * @param loginName
     * @param loginPasswd
     * @return
     */
    public boolean requestDeviceLogin(String devMac,
                                      String loginName, String loginPasswd) {
        if (null == loginName || loginName.length() == 0) {
            loginName = "admin";
        }

        if (null == loginPasswd) {
            loginPasswd = "";
        }

        // 虚拟一个FunDevice对象,只是为了Demo代码上统一,并不是必须的
        // 移植时只需要关系下面FunSDK.DevLogin()的调用方法即可
        FunDevice funDevice = findDeviceBySn(devMac);// findTempDevice(devMac);
        if (null == funDevice) {
            funDevice = new FunDevice();
            funDevice.devMac = devMac;
            funDevice.devName = devMac;
            funDevice.devIp = "0.0.0.0";
            funDevice.loginName = loginName;
            funDevice.loginPsw = loginPasswd;
            funDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
            funDevice.isRemote = true;
            mTmpSNLoginDeviceList.add(funDevice);
        }

        int result = FunSDK.DevLogin(getHandler(),
                devMac, loginName, loginPasswd, funDevice.getId());
        return (result == 0);
    }

    /**
     * 设备登出
     *
     * @param funDevice
     * @return
     */
    public boolean requestDeviceLogout(FunDevice funDevice) {
        int result = FunSDK.DevLogout(getHandler(),
                funDevice.getDevSn(),
                funDevice.getId());

        // 设置设备未登录状态
        setDeviceHasLogin(funDevice.getDevSn(), false);

        // 清空临时设备变量
        //mCurrDevice = null;

        return (result == 0);
    }

    /**
     * 获取所有设备的在线状态
     *
     * @return
     */
    public boolean requestAllDeviceStatus() {
        int result = FunSDK.SysGetDevState(getHandler(), getAllDeviceSns(), 0);
        return (result == 0);
    }

    /**
     * 查询所有局域网内的设备在线状态
     *
     * @return
     */
    public boolean requestAllLanDeviceStatus() {
        int result = FunSDK.SysGetDevState(getHandler(), getAllLanDeviceSns(), 0);
        return (result == 0);
    }

    /**
     * 获取当个设备的在线状态
     *
     * @param funDevice
     * @return
     */
    public boolean requestDeviceStatus(FunDevice funDevice) {
        if (null == funDevice) {
            return false;
        }

        int result = FunSDK.SysGetDevState(getHandler(),
                funDevice.getDevSn(), funDevice.getId());
        return (result == 0);
    }

    /**
     * 获取当个设备的在线状态
     *
     * @param devType 设备类型(手动指定)
     * @param devMac  设备序列号
     * @return
     */
    public boolean requestDeviceStatus(FunDevType devType, String devMac) {
        // 虚拟一个FunDevice对象,只是为了Demo代码上统一,并不是必须的
        FunDevice funDevice = buildTempDeivce(devType, devMac);

        funDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;

        int result = FunSDK.SysGetDevState(getHandler(),
                funDevice.getDevSn(), funDevice.getId());
        return (result == 0);
    }

    /**
     * 获取设备的当前参数和运行状态
     *
     * @param funDevice
     * @return
     */
    public boolean requestDeviceConfig(FunDevice funDevice) {
        SDK_Authority authority = new SDK_Authority();
        int result = FunSDK.DevGetConfig(getHandler(), funDevice.getDevSn(), ECONFIG.CFG_ATHORITY,
                G.Sizeof(authority), -1, 5000, funDevice.getId());
        return (result == 0);
    }

    /**
     * 获取设备的参数
     *
     * @param funDevice  设备
     * @param configName 配置名称
     * @return
     */
    public boolean requestDeviceConfig(FunDevice funDevice, String configName) {
//		int result = FunSDK.DevGetConfigByJson(getHandler(), funDevice.getDevSn(),
        // configName, 4096, -1, 10000, funDevice.getId());
        // return (result == 0);

        // 先添加到队列再依次读取配置
        // synchronized (mDeviceGetConfigQueue) {
//			mDeviceGetConfigQueue.add(new DeviceGetConfig(funDevice, configName));
        // }
        // mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG);
        // mHandler.sendEmptyMessageDelayed(MESSAGE_GET_DEVICE_CONFIG, 50);
        // return true;
        return requestDeviceConfig(funDevice, configName, -1);
    }

    /**
     * 获取设备的参数
     *
     * @param funDevice  设备
     * @param configName 配置名称
     * @param channelNo  通道号
     * @return
     */
    public boolean requestDeviceConfig(FunDevice funDevice, String configName, int channelNo) {

        // 先添加到队列再依次读取配置
        synchronized (mDeviceGetConfigQueue) {
            mDeviceGetConfigQueue.add(new DeviceGetConfig(funDevice, configName, channelNo));
        }
        mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG);
        mHandler.sendEmptyMessageDelayed(MESSAGE_GET_DEVICE_CONFIG, 50);
        return true;
    }

    private void resetTimeoutDeviceConfigFromQueue(DeviceGetConfig config) {
        mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG);
        mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG_TIMEOUT);
        synchronized (mDeviceGetConfigQueue) {
            if (mDeviceGetConfigQueue.contains(config)) {
                mDeviceGetConfigQueue.remove(config);
            }
            mIsGettingConfig = false;
        }
        mHandler.sendEmptyMessage(MESSAGE_GET_DEVICE_CONFIG);
    }

    private boolean requestDeviceConfigFromQueue() {
        DeviceGetConfig devCfg = null;

        mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG);

        if (mIsGettingConfig) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_GET_DEVICE_CONFIG, 50);
            return true;
        }

        synchronized (mDeviceGetConfigQueue) {
            if (mDeviceGetConfigQueue.size() > 0 && !mIsGettingConfig) {
                devCfg = mDeviceGetConfigQueue.get(0);
                mIsGettingConfig = true;

                // 设置一个超时时间,避免SDK不返回内容时,无法接续处理
                mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG_TIMEOUT);
                Message toutMsg = new Message();
                toutMsg.what = MESSAGE_GET_DEVICE_CONFIG_TIMEOUT;
                toutMsg.obj = devCfg;
                mHandler.sendMessageDelayed(toutMsg, 30000);
            }
        }

        if (null != devCfg) {
            int result = FunSDK.DevGetConfigByJson(getHandler(), devCfg.funDevice.getDevSn(),
                    devCfg.configName, 4096, devCfg.channelNo, 10000, devCfg.funDevice.getId());
            mHandler.sendEmptyMessageDelayed(MESSAGE_GET_DEVICE_CONFIG, 10);
            return (result == 0);
        }

        return true;
    }

    private void requestDeviceConfigDone(FunDevice funDevice, String configName) {
        DeviceGetConfig devCfg = null;
        synchronized (mDeviceGetConfigQueue) {
            // 取消定时超时消息
            mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG_TIMEOUT);

            for (int i = 0; i < mDeviceGetConfigQueue.size(); i++) {
                DeviceGetConfig tmpCfg = mDeviceGetConfigQueue.get(i);
                if (tmpCfg.configName.equals(configName)
                        && tmpCfg.funDevice.getId() == funDevice.getId()) {
                    devCfg = tmpCfg;
                    mDeviceGetConfigQueue.remove(i);
                    break;
                }
            }
        }

        if (devCfg == null) {
            FunLog.e(TAG, "Error!!! must not be null here!");
        } else if (!devCfg.configName.equals(configName)) {
            FunLog.e(TAG, "Error!!! must be the same configName here! [" + devCfg.configName + "] != [" + configName + "]");
        } else {
            synchronized (mDeviceGetConfigQueue) {
                mIsGettingConfig = false;
            }
            mHandler.removeMessages(MESSAGE_GET_DEVICE_CONFIG);
            mHandler.sendEmptyMessage(MESSAGE_GET_DEVICE_CONFIG);
        }
    }

    /**
     * 设备控制(请求)
     *
     * @param funDevice 设备
     * @param parmObj   参数对象
     * @return
     */
    public boolean requestDeviceSetConfig(FunDevice funDevice, Object parmObj) {
        int result = -1;

        FunLog.i("test", "requestDeviceSetConfig : " + funDevice.getId());

        if (parmObj instanceof BaseConfig) {
            BaseConfig baseConfig = (BaseConfig) parmObj;
            result = FunSDK.DevSetConfigByJson(getHandler(),
                    funDevice.getDevSn(),
                    baseConfig.getConfigName(),
                    baseConfig.getSendMsg(),
                    funDevice.CurrChannel,
                    60000,
                    funDevice.getId());
        }
        return (result == 0);
    }

    /**
     * 运动相机拍照和录像
     *
     * @param funDevice
     * @return
     */
    public boolean requestSportCmdGeneral(FunDevice funDevice, ManualSnapModeJP data) {
        int result = FunSDK.DevCmdGeneral(getHandler(), funDevice.getDevSn(),
                EDEV_JSON_ID.NET_MANUALSNAP_MODE_REQ,
                ManualSnapModeJP.CLASSNAME, -1, 0, data
                        .getSendMsg().getBytes(), -1, ManualSnapModeJP.CAPTURE);
        return (result == 0);
    }

    /**
     * 请求设备的参数命令(DevCmdGeneral,区别与DevGetConfigByJson())
     *
     * @param funDevice
     * @param cmd
     * @return
     */
    public boolean requestDeviceCmdGeneral(FunDevice funDevice, DevCmdGeneral cmd) {
        int result = FunSDK.DevCmdGeneral(getHandler(),
                funDevice.getDevSn(),
                cmd.getJsonID(), cmd.getConfigName(), 0, 10000,
                null != cmd.getSendMsg() ? cmd.getSendMsg().getBytes() : null, -1, funDevice.getId());
        return (result == 0);
    }

    public boolean requestDeviceFileNum(FunDevice funDevice,
                                        DevCmdSearchFileNumJP cmd, int nSeq) {
        int result = FunSDK.DevCmdGeneral(getHandler(),
                funDevice.getDevSn(),
                cmd.getJsonID(), cmd.getConfigName(), 1024, 10000,
                cmd.getSendMsg().getBytes(), -1, nSeq);
        return (result == 0);
    }

    public boolean requestDeviceFileList(FunDevice funDevice,
                                         H264_DVR_FINDINFO info) {
        int result = FunSDK.DevFindFile(getHandler(),
                funDevice.getDevSn(), G.ObjToBytes(info), 10000,
                20000, funDevice.getId());
        Log.i("SDK_LOG", "--> DevFindFile : info = " + info.toString());
        return (result == 0);
    }

    public boolean requestDeviceFileListByTime(FunDevice funDevice,
                                               SDK_SearchByTime info) {
        int result = FunSDK.DevFindFileByTime(getHandler(),
                funDevice.getDevSn(), G.ObjToBytes(info),
                10000, 0);
        Log.i("SDK_LOG", "--> DevFindFileByTime : info = " + info.toString());
        return (result == 0);
    }

    public boolean requestDeviceSearchPicture(FunDevice funDevice,
                                              OPCompressPic opCompressPic, String filePath, int seq) {
        int result = FunSDK.DevSearchPicture(getHandler(),                  //获取缩略图该接口只提供运动相机使用
                funDevice.getDevSn(),
                EDEV_JSON_ID.COMPRESS_PICTURE_REQ, 50000, 2000,
                opCompressPic.getSendMsg().getBytes(),
                20, -1,
                filePath, seq);
        return (result == 0);
    }

    public boolean requestDeviceRemovePicture(FunDevice funDevice,
                                              DevCmdOPRemoveFileJP cmdOPRemoveFileJP) {
        int result = FunSDK.DevCmdGeneral(getHandler(),
                funDevice.getDevSn(), cmdOPRemoveFileJP.getJsonID(),
                cmdOPRemoveFileJP.getConfigName(), cmdOPRemoveFileJP.getFileNum() * 256,
                cmdOPRemoveFileJP.getFileNum() * 2000,
                cmdOPRemoveFileJP.getSendMsg().getBytes(), -1, 0);
        return (result == 0);
    }

    public boolean requestDeviceRemoveFile(FunDevice funDevice,
                                           OPRemoveFileJP mOPRemoveFileJP) {
        int result = FunSDK.DevCmdGeneral(getHandler(), funDevice.getDevSn(),
                EDEV_JSON_ID.FILERMOVE_REQ, OPRemoveFileJP.CLASSNAME, -1,
                mOPRemoveFileJP.getFileNum() * 5000, mOPRemoveFileJP
                        .getSendMsg().getBytes(), -1, 0);
        return (result == 0);
    }

    public boolean requestDeviceSearchPicByPath(FunDevice funDevice,
                                                DevCmdOPFileQueryJP cmdOPFileQueryJP, int seq) {
        int result = FunSDK.DevCmdGeneral(getHandler(),
                funDevice.getDevSn(), cmdOPFileQueryJP.getJsonID(),
                cmdOPFileQueryJP.getConfigName(), -1, 1000,
                cmdOPFileQueryJP.getSendMsg().getBytes(), -1, seq);

        return (result == 1);
    }

    /**
     * 搜索设备报警信息
     *
     * @param funDevice
     * @param info
     * @return
     */
    public boolean requestDeviceSearchAlarmInfo(FunDevice funDevice,
                                                XPMS_SEARCH_ALARMINFO_REQ info) {
        int result = MpsClient.SearchAlarmInfo(
                getHandler(), G.ObjToBytes(info), funDevice.getId());
        return (result == 1);
    }

    /**
     * 请求设备进行抓拍
     *
     * @param funDevice 要进行操作的设备实例
     * @return
     */
    public boolean requestDeviceCapture(FunDevice funDevice) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = sdf.format(new Date());
        strDate = strDate + System.currentTimeMillis();

        int result = FunSDK.DevOption(getHandler(),
                funDevice.getDevSn(),
                EDEV_OPTERATE.EDOPT_DEV_GET_IMAGE,
                null, 0, 0, 0, 0,
                FunPath.PATH_CAPTURE_TEMP + File.separator + strDate + ".jpg", funDevice.getId());
        return (result == 1);

    }

    /**
     * 请求设备进行抓拍
     *
     * @param funDevice 要进行操作的设备实例
     * @return
     */
    public boolean requestDeviceCapture(FunDevice funDevice, String path) {

        int result = FunSDK.DevOption(getHandler(),
                funDevice.getDevSn(),
                EDEV_OPTERATE.EDOPT_DEV_GET_IMAGE,
                null, 0, 0, 0, 0,
                path, funDevice.getId());
        return (result == 1);

    }

    /**
     * 通过文件来下载目标文件
     *
     * @param funDevices          目标设备
     * @param pH264_DVR_FILE_DATA 查询条件。H264_DVR_FILE_DATA 对象字节流
     * @param szFilePath          保存文件路径+文件名
     * @return
     */
    public boolean requestDeviceDownloadByFile(FunDevice funDevices, byte[] pH264_DVR_FILE_DATA, String szFilePath, int position) {
        int result = FunSDK.DevDowonLoadByFile(getHandler(),
                funDevices.getDevSn(), pH264_DVR_FILE_DATA, szFilePath, position);
        return (result == 1);
    }

    /**
     * 通过时间来下载目标录像文件
     * EUIMSG.ON_FILE_DOWNLOAD:下载函数调用结果
     * EUIMSG.ON_FILE_DLD_POS:下载进度消息回调
     * EUIMSG.ON_FILE_DLD_COMPLETE:下载完成消息回调
     *
     * @return
     */
    public boolean requestDeviceDownloadByTime(DownloadInfo<H264_DVR_FINDINFO> info) {
        Object obj = info.getObj();
        int result = FunSDK.DevDowonLoadByTime(getHandler(), info.getSn(),
                G.ObjToBytes(obj), info.getFileName(), -1);
        return (result == 1);
    }

    /**
     * EMSG_ON_FILE_DOWNLOAD:
     * 请求设备开始对讲
     *
     * @param funDevice
     * @return
     */
    public int requestDeviceStartTalk(FunDevice funDevice) {
        return FunSDK.DevStarTalk(getHandler(), funDevice.getDevSn(),0,0, 0);
    }

    public void requestDeviceStopTalk(int hTalker) {
        FunSDK.DevStopTalk(hTalker);
    }

    public int requestDeviceSendTalkData(FunDevice funDevice, byte[] data, int size) {
        return FunSDK.DevSendTalkData(funDevice.getDevSn(), data, size);
    }

    /**
     * 请求设备云台控制
     *
     * @param funDevice
     * @param nPTZCommand 参考EPTZCMD.TILT_UP,EPTZCMD.TILT_DOWN,EPTZCMD.PAN_RIGHT,EPTZCMD.PAN_LEFT
     * @param bStop
     * @return
     */
    public boolean requestDevicePTZControl(FunDevice funDevice,
                                           int nPTZCommand, boolean bStop, int channel) {
        int result = FunSDK.DevPTZControl(getHandler(),
                funDevice.getDevSn(), channel, nPTZCommand, bStop ? 1 : 0, 4, funDevice.getId());
        return (result == 1);
    }

    /**
     * 设置/启动局域网报警监听,局域网报警功能,设置实时报警消息的接收者,所有设备的报警信息均由此接收,设置一次即可
     *
     * @return
     */
    public boolean startDeviceLanAlarmListener() {
        int result = FunSDK.DevSetAlarmListener(getHandler(), 0);
        return (result == 1);
    }

    /**
     * 请求打开/关闭设备局域网报警功能
     *
     * @param devSn
     * @param enable
     * @return
     */
    public boolean requestDeviceLanAlarmEnable(String devSn,
                                               boolean enable) {
        int nValue = enable ? 1 : 0; // 0为关闭 ，非0为开启（建议赋1）
        int result = FunSDK.DevSetAttrAlarmByInt(getHandler(),
                devSn,
                EDEV_ATTR.EDA_OPT_ALARM,
                nValue,
                0);
        return (result == 1);
    }

    /**
     * 设备升级检测接口
     */
    public boolean requestDeviceUpdateCheck(String devSn) {
        int result = FunSDK.DevCheckUpgrade(getHandler(), devSn, 0);
        return (result == 0);
    }

    public boolean requestDeviceTitleDot(FunDevice funDevice,
                                         SDK_TitleDot titleDot) {
        int result = FunSDK.DevCmdGeneral(getHandler(), funDevice.getDevSn(),
                EDEV_JSON_ID.CONFIG_CHANNELTILE_DOT_SET,
                "TitleDot", 1024, 5000, G.ObjToBytes(titleDot),
                -1, 0);
        return (result == 0);
    }

    /**
     * 通过设备ID查找设备
     *
     * @param devId
     * @return
     */
    public FunDevice findDeviceById(int devId) {
        for (FunDevice funDev : mDeviceList) {
            if (devId == funDev.getId()) {
                return funDev;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devId == funDevice.getId()) {
                return funDevice;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devId == funDevice.getId()) {
                return funDevice;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devId == funDevice.getId()) {
                return funDevice;
            }
        }

        return null;
    }

    /**
     * 通过设备(MAC)查找设备
     *
     * @param devSn
     * @return
     */
    public FunDevice findDeviceBySn(String devSn) {
        if (null == devSn) {
            return null;
        }

        for (FunDevice funDev : mDeviceList) {
            if (devSn.equals(funDev.getDevSn())) {
                return funDev;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                return funDevice;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                return funDevice;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                return funDevice;
            }
        }

        return null;
    }

    private void setDeviceStatus(String devSn, FunDevStatus status, int statusValue) {
        // 只要是序列号相同的,不管是在局域网设备列表内还是在用户设备列表内都同时更新一下,因为肯定是同一个设备
        if (null == devSn) {
            return;
        }

        for (FunDevice funDev : mDeviceList) {
            if (devSn.equals(funDev.getDevSn())) {
                funDev.devStatus = status;
                funDev.devStatusValue = statusValue;
                break;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                funDevice.devStatusValue = statusValue;
                break;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                funDevice.devStatusValue = statusValue;
                break;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                funDevice.devStatusValue = statusValue;
                break;
            }
        }
    }

    private void setDeviceStatus(String devSn, FunDevStatus status) {
        // 只要是序列号相同的,不管是在局域网设备列表内还是在用户设备列表内都同时更新一下,因为肯定是同一个设备
        if (null == devSn) {
            return;
        }

        for (FunDevice funDev : mDeviceList) {
            if (devSn.equals(funDev.getDevSn())) {
                funDev.devStatus = status;
                break;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                break;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                break;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.devStatus = status;
                break;
            }
        }
    }

    public void setDeviceHasLogin(String devSn, boolean login) {
        // 只要是序列号相同的,不管是在局域网设备列表内还是在用户设备列表内都同时更新一下,因为肯定是同一个设备
        if (null == devSn) {
            return;
        }

        for (FunDevice funDev : mDeviceList) {
            if (devSn.equals(funDev.getDevSn())) {
                funDev.setHasLogin(login);
                break;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setHasLogin(login);
                break;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setHasLogin(login);
                break;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setHasLogin(login);
                break;
            }
        }
    }

    private void setDeviceHasConnected(String devSn, boolean connected) {
        // 只要是序列号相同的,不管是在局域网设备列表内还是在用户设备列表内都同时更新一下,因为肯定是同一个设备
        if (null == devSn) {
            return;
        }

        for (FunDevice funDev : mDeviceList) {
            if (devSn.equals(funDev.getDevSn())) {
                funDev.setConnected(connected);
                break;
            }
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setConnected(connected);
                break;
            }
        }

        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setConnected(connected);
                break;
            }
        }

        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                funDevice.setConnected(connected);
                break;
            }
        }
    }

    private String getAllDeviceSns() {
        String devIds = "";
        for (FunDevice funDev : mDeviceList) {
            if (devIds.length() > 0) {
                devIds += ";";
            }
            devIds += funDev.getDevSn();
        }
        return devIds;
    }

    private String getAllLanDeviceSns() {
        String devIds = "";
        for (FunDevice funDev : mLanDeviceList) {
            if (devIds.length() > 0) {
                devIds += ";";
            }
            devIds += funDev.getDevSn();
        }
        return devIds;
    }

    /**
     * 获取当前用户的设备列表表
     *
     * @return 用户设备列表
     */
    public List<FunDevice> getDeviceList() {
        return mDeviceList;
    }

    /**
     * 获取当前附近的AP设备列表
     *
     * @return AP设备列表
     */
    public List<FunDevice> getAPDeviceList() {
        return mAPDeviceList;
    }

    public FunDevice findAPDevice(String ssid) {
        if (null == ssid || ssid.length() == 0) {
            return null;
        }

        for (FunDevice funDevice : mAPDeviceList) {
            if (ssid.equals(funDevice.devName)) {
                return funDevice;
            }
        }

        return null;
    }

    public FunDevice findLanDevice(String devSn) {
        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                return funDevice;
            }
        }
        return null;
    }

    /**
     * 获取局域网内的设备列表
     *
     * @return
     */
    public List<FunDevice> getLanDeviceList() {
        return mLanDeviceList;
    }

    public FunDevice findTempDevice(String devMac) {
        for (FunDevice funDevice : mTmpSNLoginDeviceList) {
            if (devMac.equals(funDevice.devMac)) {
                return funDevice;
            }
        }

        return null;
    }

    /**
     * 通过设备序列号,模拟一个设备对象
     *
     * @param devType 设备类型(手动指定)
     * @param devMac  设备序列号
     * @return
     */
    public FunDevice buildTempDeivce(FunDevType devType,
                                     String devMac) {
        FunDevice funDevice = findDeviceBySn(devMac);// findTempDevice(devMac);
        if (null == funDevice) {
        	if (devType == FunDevType.EE_DEV_INTELLIGENTSOCKET) {
        		funDevice = new FunDeviceSocket();
			}else {
				funDevice = new FunDevice();
			}
            funDevice.devMac = devMac;
            funDevice.devName = devMac;
            funDevice.devIp = "0.0.0.0";
            funDevice.devType = devType;
            funDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
            funDevice.isRemote = true;
            mTmpSNLoginDeviceList.add(funDevice);
        }
        return funDevice;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_AP_DEVICE_LIST_CHANGED: {
                    // AP设备列表变化通知
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceListener) {
                            ((OnFunDeviceListener) l).onAPDeviceListChanged();
                        }
                    }
                }
                break;
                case MESSAGE_GET_DEVICE_CONFIG: {
                    requestDeviceConfigFromQueue();
                }
                break;
                case MESSAGE_GET_DEVICE_CONFIG_TIMEOUT: {
                    resetTimeoutDeviceConfigFromQueue((DeviceGetConfig) msg.obj);
                }
                break;
            }
        }

    };


    private void onUserInfoSavedAfterLoginSuccess() {
        // 保存当前登录的用户名和密码
        mLoginUserName = mTmpLoginUserName;
        mLoginPassword = mTmpLoginPassword;

        // 如果不需要保存密码,那么设置密码为空
        if (getSavePasswordAfterLogin()) {
            FunLoginHistory.getInstance().saveLoginInfo(mLoginUserName, mLoginPassword);
        } else {
            FunLoginHistory.getInstance().saveLoginInfo(mLoginUserName, "");
        }

        // 登录成功后,初始化设备报警/消息推送服务
        mpsInit();
    }

    /**
     * 初始化设备报警/消息推送服务(必须在登录成功之后使用)
     */
    private void mpsInit() {
        // 初始化消息推送服务
        try {
            if (null != mLoginUserName
                    && !mLoginUserName.equals(mLastMpsUserName)) {
                String pushToken = MyUtils.getMpsPushToken(getContext());

                SMCInitInfo info = new SMCInitInfo();
                G.SetValue(info.st_0_user, mLoginUserName);
                G.SetValue(info.st_1_password, mLoginPassword);
                G.SetValue(info.st_2_token, pushToken);

                MpsClient.Init(getHandler(), G.ObjToBytes(info), 0);

                mLastMpsUserName = mLoginUserName;

                FunLog.i(TAG, "MpsClient init with user : " + mLastMpsUserName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关联/添加一个设备的报警消息接收
     *
     * @param funDevice
     */
    public void mpsLinkDevice(FunDevice funDevice) {
        if (null != funDevice) {
            FunLog.d(TAG, "LinkDev : " + funDevice.getDevSn()
                    + ", " + funDevice.loginName + ", " + funDevice.loginPsw);
            MpsClient.LinkDev(getHandler(),
                    funDevice.getDevSn(),
                    funDevice.loginName, funDevice.loginPsw, 0);
        }
    }

    /**
     * 去掉/删除一个设备的报警消息接收
     *
     * @param devSn
     */
    public void mpsUnLinkDevice(String devSn) {
        if (null != devSn) {
            MpsClient.UnlinkDev(getHandler(),
                    devSn, 0);
        }
    }

    public void requestDevWakeUp(FunDevice funDevice) {
        if (null != funDevice) {
            FunSDK.DevWakeUp(getHandler(), funDevice.getDevSn(), funDevice.getId());
        }
    }

    public void requestDevSleep(FunDevice funDevice) {
        if (null != funDevice) {
            if (funDevice.hasLogin()) {
                requestDeviceLogout(funDevice);
            }else {
                FunSDK.DevLogout(getHandler(), funDevice.getDevSn(), funDevice.getId());
            }
        }
    }

    //同步时区
    public void requestSyncDevZone(FunDevice funDevice,int zone) {
        TimeZoneBean timeZoneBean = new TimeZoneBean();
        timeZoneBean.timeMin = zone;
        timeZoneBean.FirstUserTimeZone = 0;
        FunSDK.DevSetConfigByJson(getHandler(), funDevice.getDevSn(),
                JsonConfig.SYSTEM_TIMEZONE,
                HandleConfigData.getSendData(JsonConfig.SYSTEM_TIMEZONE,
                        "0x1", timeZoneBean),
                -1, 5000, funDevice.getId());
        FunSDK.DevGetConfigByJson(getHandler(),funDevice.getDevSn(),
                JsonConfig.GENERAL_LOCATION,
                1024,-1,5000,funDevice.getId());
    }

    @Override
    public int OnFunSDKResult(Message msg, MsgContent msgContent) {
        /*
        FunLog.d(TAG, "msg.what : " + msg.what);
        FunLog.d(TAG, "msg.arg1 : " + msg.arg1 + " [" + FunError.getErrorStr(msg.arg1) + "]");
        FunLog.d(TAG, "msg.arg2 : " + msg.arg2);
        if (null != msgContent) {
            FunLog.d(TAG, "msgContent.sender : " + msgContent.sender);
            FunLog.d(TAG, "msgContent.seq : " + msgContent.seq);
            FunLog.d(TAG, "msgContent.str : " + msgContent.str);
            FunLog.d(TAG, "msgContent.arg3 : " + msgContent.arg3);
            FunLog.d(TAG, "msgContent.pData : " + msgContent.pData);
        }
        */
        switch (msg.what) {
            case EUIMSG.SYS_GET_DEV_INFO_BY_USER_XM: {
                FunLog.i(TAG, "EUIMSG.SYS_GET_DEV_INFO_BY_USER_XM");

                if (msg.arg1 == FunError.EE_OK) {
                    // 用户登录成功

                    // 保存当前登录的用户名和密码
                    onUserInfoSavedAfterLoginSuccess();

                    // 登录成功之后立刻获取用户设备列表
                    requestDeviceList();

                    // 回调
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunLoginListener) {
                            ((OnFunLoginListener) l).onLoginSuccess();
                        }
                    }
                } else {
                    // 用户登录失败

                    // 回调
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunLoginListener) {
                            ((OnFunLoginListener) l).onLoginFailed(msg.arg1);
                        }
                    }
                }
            }
            break;


            case EUIMSG.SYS_GET_DEV_STATE: // 获取设备在线状态
            {
                FunLog.i(TAG, "EUIMSG.SYS_GET_DEV_STATE");

                int seq = msgContent.seq;
                String devSn = msgContent.str;
                boolean isOnline = (msg.arg1 > 0);
                FunDevStatus devStatus = isOnline ? FunDevStatus.STATUS_ONLINE : FunDevStatus.STATUS_OFFLINE;
                FunDevice funDev = null;
                if (seq != 0) {
                    funDev = findDeviceById(seq);
                } else {
                    funDev = findDeviceBySn(devSn);
                }

                if (null != funDev) {
                    if (devStatus == FunDevStatus.STATUS_ONLINE) {
                        int idrState = FunSDK.GetDevState(msgContent.str, SDKCONST.EFunDevStateType.IDR);
                        if (idrState == SDKCONST.EFunDevState.SLEEP) {
                            devStatus = FunDevStatus.STATUS_SLEEP;
                        }else if (idrState == 3) {
                            devStatus = FunDevStatus.STATUS_CAN_NOT_WAKE_UP;
                        }
                    }
                    setDeviceStatus(devSn, devStatus, msg.arg1);
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceListener) {
                            ((OnFunDeviceListener) l).onDeviceStatusChanged(funDev);
                        }
                        if (l instanceof OnFunDeviceWakeUpListener) {
                            ((OnFunDeviceWakeUpListener) l).onDeviceState(devStatus);
                        }
                    }
                }
            }
            break;

            case EUIMSG.SYS_CHANGEDEVINFO: // 修改设备信息(重命名等)
            {
                FunDevice funDev = findDeviceById(msgContent.seq);
                if (null != funDev) {
                    if (msg.arg1 == FunError.EE_OK) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceChangeInfoSuccess(funDev);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceChangeInfoFailed(funDev, msg.arg1);
                            }
                        }
                    }
                }
            }
            break;

            case EUIMSG.DEV_AP_CONFIG:
                if (msg.arg1 >= 0) {
                    SDK_CONFIG_NET_COMMON_V2 common = new SDK_CONFIG_NET_COMMON_V2();
                    G.BytesToObj(common, msgContent.pData);
                    FunDevice funDevice = addLanDevice(common);

                    if (null != funDevice) {
                        // 回调,通知设备WiFi配置成功
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceWiFiConfigListener) {
                                ((OnFunDeviceWiFiConfigListener) l).onDeviceWiFiConfigSetted(funDevice);
                            }
                        }
                    }
                }
                break;

            case EUIMSG.DEV_SEARCH_DEVICES: // 局域网内设备搜索结果
            {
                int length = msg.arg2;
                if (length > 0) {
                    SDK_CONFIG_NET_COMMON_V2[] searchResult = new SDK_CONFIG_NET_COMMON_V2[length];
                    for (int i = 0; i < searchResult.length; i++) {
                        searchResult[i] = new SDK_CONFIG_NET_COMMON_V2();
                    }
                    G.BytesToObj(searchResult, msgContent.pData);
                    for (SDK_CONFIG_NET_COMMON_V2 sdk_config_net_common_v2 : searchResult) {
                        if (sdk_config_net_common_v2.st_15_DeviceType == -1) {
                            sdk_config_net_common_v2.st_15_DeviceType = 0;
                        }
                    }
                    updateLanDeviceList(searchResult);
                } else {
                    updateLanDeviceList(null);
                }

                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceListener) {
                        ((OnFunDeviceListener) l).onLanDeviceListChanged();
                    }
                }
            }
            break;

            case EUIMSG.DEV_LOGIN: // 设备登录
            {
                FunLog.i(TAG, "EUIMSG.DEV_LOGIN");

                int seq = msgContent.seq;
                FunDevice funDev = findDeviceById(seq);
                if (null != funDev) {
                    if (msg.arg1 == FunError.EE_OK) {

                        // 设置已经登录标志
                        setDeviceHasLogin(funDev.getDevSn(), true);

                        // 临时保存当前登录的设备
                        //mCurrDevice = funDev;

                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceLoginSuccess(funDev);
                            }
                        }
                    } else {

                        //mCurrDevice = null;
//					if (mVerificationPassword == 0) {
//						//已全部验证
//						mVerificationPassword = 2;
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceLoginFailed(funDev, msg.arg1);
                            }
                        }
//					}else {
//						//验证服务器列表密码
//						requestDeviceLogin(funDev);
//					}
                    }
                } else {
                    FunLog.e(TAG, "Recive -> EUIMSG.DEV_LOGIN, but no device matched.");
                }
            }
            break;
            case EUIMSG.DEV_GET_CONFIG: {
                FunLog.i(TAG, "EUIMSG.DEV_GET_CONFIG");

                if (msg.arg1 >= 0 && msgContent.arg3 == ECONFIG.CFG_ATHORITY) {
                    SDK_Authority authority = new SDK_Authority();
                    G.BytesToObj(authority, msgContent.pData);
                    if ((authority.st_1_Ability & 1) != 1) {

                    }
                }
            }
            break;

            case EUIMSG.DEV_GET_JSON: // 设备配置信息
            case EUIMSG.DEV_CMD_EN: {

                FunLog.i(TAG, "EUIMSG.DEV_GET_JSON");

                FunDevice funDevice = findDeviceById(msgContent.seq);
                if (null == funDevice) {
                    funDevice = mCurrDevice;
                }

                if (null != funDevice) {

                    //特殊处理（运动相机拍照回调不处理）
                    if (ManualSnapModeJP.CLASSNAME.equals(msgContent.str)) {
                        if (msg.arg1 < 0) {
                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l).onDeviceGetConfigFailed(
                                            funDevice, msg.arg1);
                                }
                            }
                        }
                        break;
                    }
                    requestDeviceConfigDone(funDevice, msgContent.str);
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceGetConfigFailed(
                                        funDevice, msg.arg1);
                            }
                        }
                    } else if (null != msgContent.pData) {
                        // 解析JSON
                        String json = G.ToString(msgContent.pData);
                        FunLog.i(TAG, "EUIMSG.DEV_GET_JSON --> json: " + json);
                        FunLog.i(TAG, "configName = " + msgContent.str);

                        if (StringUtils.contrast(msgContent.str, JsonConfig.GENERAL_LOCATION)) {
                            //如果是同步时区的话 需要设置夏令时的
                            HandleConfigData data = new HandleConfigData();
                            if (data.getDataObj(G.ToString(msgContent.pData), LocationBean.class)) {
                                LocationBean locationBean = (LocationBean) data.getObj();
                                if (locationBean != null) {
                                    DayLightTimeBean dayLightTimeBean = XUtils.getDayLightTimeInfo(TimeZone.getDefault());
                                    if (dayLightTimeBean != null) {
                                        if (dayLightTimeBean.useDLT) {
                                            locationBean.setdSTRule("On");
                                            DSTimeBean dstStart = new DSTimeBean();
                                            dstStart.setYear(dayLightTimeBean.year);
                                            dstStart.setMonth(dayLightTimeBean.beginMonth);
                                            dstStart.setDay(dayLightTimeBean.beginDay);
                                            DSTimeBean dstEnd = new DSTimeBean();
                                            dstEnd.setYear(dayLightTimeBean.beginMonth > dayLightTimeBean.endMonth
                                                    ? dayLightTimeBean.year + 1 : dayLightTimeBean.year);
                                            dstEnd.setMonth(dayLightTimeBean.endMonth);
                                            dstEnd.setDay(dayLightTimeBean.endDay);
                                            locationBean.setdSTStart(dstStart);
                                            locationBean.setdSTEnd(dstEnd);
                                        } else {
                                            locationBean.setdSTRule("Off");
                                        }
                                    }
                                    FunSDK.DevSetConfigByJson(getHandler(), funDevice.getDevSn(),
                                            JsonConfig.GENERAL_LOCATION,
                                            HandleConfigData.getSendData(JsonConfig.GENERAL_LOCATION,
                                                    "0x02", locationBean),
                                            -1, 5000, funDevice.getId());
                                }
                            }

                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l)
                                            .onDeviceGetConfigSuccess(funDevice, msgContent.str, msgContent.seq);
                                }
                            }
                        }else if (DeviceGetJson.onParse(funDevice, msgContent.str, json)) {
                            // 此处特殊处理,如果是SystemInfo,msg.arg2是连接方式
                            if (SystemInfo.CONFIG_NAME.equals(msgContent.str)) {
                                funDevice.setNetConnectType(msg.arg2);
                            }

                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l)
                                            .onDeviceGetConfigSuccess(funDevice, msgContent.str, msgContent.seq);
                                }
                            }
                        } else {
                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l)
                                            .onDeviceGetConfigFailed(funDevice,
                                                    FunError.EE_DVR_DEV_VER_NOMATCH);
                                }
                            }
                        }
                    }
                } else {
                    // 不是设备发出去的请求回应,暂时不处理
                    FunLog.e(TAG, "Recive -> EUIMSG.DEV_GET_JSON, but no device matched.");
                }
            }
            break;
            case EUIMSG.DEV_SET_JSON: {
                FunLog.i(TAG, "EUIMSG.DEV_SET_JSON");

                FunDevice funDevice = findDeviceById(msgContent.seq);
                if (null != funDevice) {
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceSetConfigFailed(
                                        funDevice, msgContent.str, msg.arg1);
                            }
                            if (l instanceof OnAddSubDeviceResultListener) {
                                ((OnAddSubDeviceResultListener) l).onAddSubDeviceFailed(funDevice, msgContent);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceSetConfigSuccess(
                                        funDevice, msgContent.str);
                            }
                            if (l instanceof OnAddSubDeviceResultListener) {
                                ((OnAddSubDeviceResultListener) l).onAddSubDeviceSuccess(funDevice, msgContent);
                            }
                        }
                    }
                } else {
                    // 不是设备发出去的请求回应,暂时不处理
                    FunLog.e(TAG, "Recive -> EUIMSG.DEV_SET_JSON, but no device matched.");
                }

                if(msgContent.str.equals(JsonConfig.OPERATION_DEFAULT_CONFIG)) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof CameraUpgradeListener) {
                            ((CameraUpgradeListener) l).devSetJson(msg, msgContent);
                        }
                    }
                }

            }
            break;
            case EUIMSG.DEV_GET_CHN_NAME: {                    //获取通道信息
                FunLog.i(TAG, "EUIMSG.DEV_GET_CHN_NAME");

                FunDevice funDevice = findDeviceById(msgContent.seq);

                if (msg.arg1 > 0) {
                    if (msgContent.pData != null && msgContent.pData.length > 0) {
                        SDK_ChannelNameConfigAll Channel = new SDK_ChannelNameConfigAll();
                        G.BytesToObj(Channel, msgContent.pData);
                        Channel.nChnCount = msg.arg1;
                        funDevice.setChannel(Channel);
                    }
                }
            }
            break;
            case EUIMSG.DEV_OPTION: {
                FunLog.i(TAG, "EUIMSG.DEV_OPTION");

                FunDevice funDevice = findDeviceById(msgContent.seq);

                if (EDEV_OPTERATE.EDOPT_DEV_GET_IMAGE == msgContent.arg3) {
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceCaptureListener) {
                                ((OnFunDeviceCaptureListener) l).onCaptureFailed(msg.arg1);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceCaptureListener) {
                                ((OnFunDeviceCaptureListener) l).onCaptureSuccess(msgContent.str);
                            }
                        }
                    }
                } else if (EDEV_OPTERATE.EDOPT_DEV_OPEN_TANSPORT_COM == msgContent.arg3) {
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceSerialListener) {
                                ((OnFunDeviceSerialListener) l)
                                        .onDeviceSerialOpenFailed(funDevice, msg.arg1);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceSerialListener) {
                                ((OnFunDeviceSerialListener) l)
                                        .onDeviceSerialOpenSuccess(funDevice);
                            }
                        }
                    }
                } else if (EDEV_OPTERATE.EDOPT_DEV_TANSPORT_COM_READ == msgContent.arg3) {

                } else if (EDEV_OPTERATE.EDOPT_DEV_TANSPORT_COM_WRITE == msgContent.arg3) {
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceSerialListener) {
                                ((OnFunDeviceSerialListener) l)
                                        .onDeviceSerialWriteFailed(funDevice, msg.arg1);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceSerialListener) {
                                ((OnFunDeviceSerialListener) l)
                                        .onDeviceSerialWriteSuccess(funDevice);
                            }
                        }
                    }
                } else {
                    if (null != funDevice) {
                        if (msg.arg1 < 0) {
                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l)
                                            .onDeviceOptionFailed(funDevice, msgContent.str, msg.arg1);
                                }
                            }
                        } else {
                            for (OnFunListener l : mListeners) {
                                if (l instanceof OnFunDeviceOptListener) {
                                    ((OnFunDeviceOptListener) l)
                                            .onDeviceOptionSuccess(funDevice, msgContent.str);
                                }
                            }
                        }
                    }
                }
            }
            break;

            case EUIMSG.DEV_FIND_FILE: // 设备文件信息搜索(search file by file)
            {
                FunLog.i(TAG, "EUIMSG.DEV_FIND_FILE");

                FunDevice funDevice = findDeviceById(msgContent.seq);
                int fileNum = msg.arg1;
                if (fileNum < 0) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceRecordListener) {
                            ((OnFunDeviceRecordListener) l).onRequestRecordListFailed(msg.arg1);
                        }
                    }
                }
                DevCmdOPSCalendar cmdCalendar = (DevCmdOPSCalendar) funDevice.getConfig(DevCmdOPSCalendar.CONFIG_NAME);
                if (null != funDevice
                        && fileNum > 0
                        && null != msgContent.pData
                        && null != cmdCalendar) {
                    H264_DVR_FILE_DATA datas[] = new H264_DVR_FILE_DATA[msg.arg1];
                    for (int i = 0; i < datas.length; i++) {
                        datas[i] = new H264_DVR_FILE_DATA();
                    }

                    G.BytesToObj(datas, msgContent.pData);
                    SameDayPicInfo picInfo = cmdCalendar.getDayData(datas[0].st_3_beginTime);

                    if (null != picInfo) {
                        for (int i = 0; i < datas.length; i++) {
                            // FunLog.d("test", datas[i].toString());
                            picInfo.setPicData(datas[i]);
                        }

                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceFileListChanged(funDevice);
                            }
                        }
                    } else {
                        FunLog.e("DEV_FIND_FILE", "search file error!");
                    }

                } else if (cmdCalendar == null) {
                    if (msg.arg1 >= 0) {
                        H264_DVR_FILE_DATA[] datas = new H264_DVR_FILE_DATA[msg.arg1];
                        for (int i = 0; i < datas.length; i++) {
                            datas[i] = new H264_DVR_FILE_DATA();
                        }

                        G.BytesToObj(datas, msgContent.pData);

                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceFileListChanged(funDevice, datas);
                            }
                        }
                    } else {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceOptListener) {
                                ((OnFunDeviceOptListener) l).onDeviceFileListGetFailed(funDevice);
                            }
                        }
                    }
                }
            }
            break;

            case EUIMSG.DEV_FIND_FILE_BY_TIME: //(search file by time)
            {
                FunLog.i(TAG, "DEV_FIND_FILE_BY_TIME");

                if (msg.arg1 < 0) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceRecordListener) {
                            ((OnFunDeviceRecordListener) l).onRequestRecordListFailed(msg.arg1);
                        }
                    }
                } else {

                    List<FunDevRecordFile> files = FunDevRecordFile.parseDevFilesByResult(msgContent.pData);

                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceRecordListener) {
                            ((OnFunDeviceRecordListener) l).onRequestRecordListSuccess(files);
                        }
                    }
                }
            }
            break;
            case EUIMSG.DEV_SEARCH_PIC: {
                FunLog.i(TAG, "EUIMSG.DEV_SEARCH_PIC");

                if (msg.arg1 == FunError.EE_OK) {
                    // 文件下载成功
                    String path = msgContent.str;
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceFileListener) {
                            ((OnFunDeviceFileListener) l).onDeviceFileDownCompleted(mCurrDevice,
                                    path, msgContent.seq);
                        }
                    }
                }
            }
            break;

            case EUIMSG.DEV_START_TALK: {
                FunLog.i(TAG, "EUIMSG.DEV_START_TALK");

                if (msg.arg1 < 0) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceTalkListener) {
                            ((OnFunDeviceTalkListener) l).onDeviceStartTalkFailed(msg.arg1);
                        }
                    }
                } else {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceTalkListener) {
                            ((OnFunDeviceTalkListener) l).onDeviceStartTalkSuccess();
                        }
                    }
                }
            }
            break;
            case EUIMSG.DEV_STOP_TALK: {
                FunLog.i(TAG, "EUIMSG.DEV_STOP_TALK");

                if (msg.arg1 < 0) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceTalkListener) {
                            ((OnFunDeviceTalkListener) l).onDeviceStopTalkFailed(msg.arg1);
                        }
                    }
                } else {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceTalkListener) {
                            ((OnFunDeviceTalkListener) l).onDeviceStopTalkSuccess();
                        }
                    }
                }
            }
            break;

            case EUIMSG.SAVE_IMAGE_FILE: {
                FunLog.i(TAG, "EUIMSG.SAVE_IMAGE_FILE");

                if (msg.arg1 == FunError.EE_OK) {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceCaptureListener) {
                            ((OnFunDeviceCaptureListener) l).onCaptureSuccess(msgContent.str);
                        }
                    }
                } else {
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceCaptureListener) {
                            ((OnFunDeviceCaptureListener) l).onCaptureFailed(msg.arg1);
                        }
                    }
                }
            }
            break;
            // 下载完成消息回调
            case EUIMSG.ON_FILE_DLD_COMPLETE: {
                FunLog.i(TAG, "EUIMSG.ON_FILE_DLD_COMPLETE");

                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceFileListener) {
                        ((OnFunDeviceFileListener) l).onDeviceFileDownCompleted(
                                mCurrDevice, msgContent.str, msgContent.seq);
                    }
                }
            }
            break;
            // 下载进度消息回调
            case EUIMSG.ON_FILE_DLD_POS: {
                FunLog.i(TAG, "EUIMSG.ON_FILE_DLD_POS");

                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceFileListener) {
                        ((OnFunDeviceFileListener) l).onDeviceFileDownProgress(
                                msg.arg1, msg.arg2, msgContent.seq);
                    }
                }
            }
            break;
            // 下载函数调用结果
            case EUIMSG.ON_FILE_DOWNLOAD: {
                FunLog.i(TAG, "EUIMSG.ON_FILE_DOWNLOAD");

                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceFileListener) {
                        ((OnFunDeviceFileListener) l).onDeviceFileDownStart(
                                msg.arg1 == 0, msgContent.seq);
                    }
                }
            }
            break;

            // 收到设备连接成功消息
            case EUIMSG.DEV_ON_RECONNECT: {
                FunLog.i(TAG, "EUIMSG.DEV_ON_RECONNECT");
                if (msg.arg1 == FunError.EE_OK) {
                    FunDevice funDev = findDeviceBySn(msgContent.str);
                    if (null != funDev) {
                        setDeviceHasConnected(msgContent.str, true);
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceConnectListener) {
                                ((OnFunDeviceConnectListener) l).onDeviceReconnected(funDev);
                            }
                        }
                    }
                }
            }
            break;
            // 收到设备断开消息
            case EUIMSG.DEV_ON_DISCONNECT: {
                FunLog.i(TAG, "EUIMSG.DEV_ON_DISCONNECT");
                if (msg.arg1 == FunError.EE_OK) {
                    FunDevice funDev = findDeviceBySn(msgContent.str);
                    if (null != funDev) {
                        setDeviceHasConnected(msgContent.str, false);
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceConnectListener) {
                                ((OnFunDeviceConnectListener) l).onDeviceDisconnected(funDev);
                            }
                        }
                    }
                }
            }
            break;

            case EUIMSG.DEV_ON_TRANSPORT_COM_DATA: {
                // 收到串口数据
                FunLog.i(TAG, "EUIMSG.DEV_ON_TRANSPORT_COM_DATA");

                FunDevice funDevice = findDeviceById(msgContent.seq);

                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceSerialListener) {
                        ((OnFunDeviceSerialListener) l).onDeviceSerialTransmitData(funDevice, msgContent.pData);
                    }
                }
            }
            break;

            // 设备报警相关
            case EUIMSG.MC_ON_PictureCb:
            case EUIMSG.MC_ON_AlarmCb: {
                FunLog.e(TAG, TAG + "EUIMSG.MC_ON_AlarmCb");

                FunDevice funDev = findDeviceBySn(msgContent.str);
                if (null != funDev) {
                    // 收到设备报警
                    for (OnFunListener l : mListeners) {
                        if (l instanceof OnFunDeviceAlarmListener) {
                            ((OnFunDeviceAlarmListener) l).onDeviceAlarmReceived(funDev);
                        }
                    }
                }
            }
            break;
            case EUIMSG.MC_LinkDev: // 设备报警关联成功
            {
                FunLog.i(TAG, "EUIMSG.MC_LinkDev");
            }
            break;
            case EUIMSG.MC_UnlinkDev: // 解除报警关联
            {
                FunLog.i(TAG, "EUIMSG.MC_UnlinkDev");
            }
            break;
            case EUIMSG.MC_SearchAlarmInfo: // 设备历史报警消息获取
            {
                FunLog.i(TAG, "EUIMSG.MC_SearchAlarmInfo");

                FunDevice funDevice = findDeviceById(msgContent.seq);

                if (null != funDevice) {
                    if (msg.arg1 < 0) {
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceAlarmListener) {
                                ((OnFunDeviceAlarmListener) l).onDeviceAlarmSearchFailed(funDevice, msg.arg1);
                            }
                        }
                    } else {
                        List<AlarmInfo> infos = new ArrayList<AlarmInfo>();
                        AlarmInfo info = null;
                        int nNext[] = new int[1];
                        nNext[0] = 0;
                        int nStart = 0;
                        for (int i = 0; i < msgContent.arg3; ++i) {
                            String ret = G.ArrayToString(msgContent.pData, nStart, nNext);
                            nStart = nNext[0];
                            info = new AlarmInfo();
                            if (!info.onParse(ret)) {
                                if (!info.onParse("{" + ret))
                                    break;
                            }
                            // if (dateStr.equals(info.getStartTime().substring(0,
                            // 10))) {
                            infos.add(info);
                            // }
                        }

                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceAlarmListener) {
                                ((OnFunDeviceAlarmListener) l).onDeviceAlarmSearchSuccess(funDevice, infos);
                            }
                        }
                    }
                }
            }
            break;


            // 局域网报警相关
            case EUIMSG.DEV_SET_ATTR: {
                FunLog.i(TAG, "EUIMSG.DEV_SET_ATTR");
            }
            break;
            case EUIMSG.DEV_GET_LAN_ALARM: {
                // 收到局域网报警消息
                FunLog.i(TAG, "EUIMSG.DEV_GET_LAN_ALARM");

                // 设备的序列号
                String devSn = msgContent.str;
                // 查找对应序列号的设备是否还存在,如果不存在就不要回调到上层了
                FunDevice funDevice = findDeviceBySn(devSn);

                if (null != funDevice) {
                    try {
                        String json = G.ToString(msgContent.pData);
                        FunLog.d(TAG, json);

                        // 解析报警消息
                        AlarmInfo alarmInfo = new AlarmInfo();
                        alarmInfo.onParse(json);

                        // 回调,通知上层接收到一个局域网报警消息
                        for (OnFunListener l : mListeners) {
                            if (l instanceof OnFunDeviceAlarmListener) {
                                ((OnFunDeviceAlarmListener) l).onDeviceLanAlarmReceived(
                                        funDevice, alarmInfo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            case EUIMSG.DEV_WAKEUP: {
                FunDevice funDevice = null;
                if (msgContent.seq != 0) {
                    funDevice = findDeviceById(msgContent.seq);
                }
                FunDevStatus devStatus = null;
                if (funDevice != null) {
                    int idrState = FunSDK.GetDevState(funDevice.getDevSn(), SDKCONST.EFunDevStateType.IDR);
                    if (idrState == SDKCONST.EFunDevState.SLEEP) {
                        devStatus = FunDevStatus.STATUS_SLEEP;
                    } else if (idrState == 3) {
                        devStatus = FunDevStatus.STATUS_CAN_NOT_WAKE_UP;
                    } else {
                        devStatus = FunDevStatus.STATUS_ONLINE;
                    }
                    if (msg.arg1 >= 0) {
                        setDeviceHasLogin(funDevice.getDevSn(), true);
                    }
                }
                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceWakeUpListener) {
                        ((OnFunDeviceWakeUpListener) l)
                                .onWakeUpResult(msg.arg1 >= 0, devStatus);
                    }
                }
            }
                break;
            case DEV_SLEEP: {
                FunDevStatus devStatus = null;
                int idrState = FunSDK.GetDevState(msgContent.str, SDKCONST.EFunDevStateType.IDR);
                if (idrState == SDKCONST.EFunDevState.SLEEP) {
                    devStatus = FunDevStatus.STATUS_SLEEP;
                } else if (idrState == 3) {
                    devStatus = FunDevStatus.STATUS_CAN_NOT_WAKE_UP;
                } else {
                    devStatus = FunDevStatus.STATUS_ONLINE;
                }
                for (OnFunListener l : mListeners) {
                    if (l instanceof OnFunDeviceWakeUpListener) {
                        ((OnFunDeviceWakeUpListener) l)
                                .onSleepResult(msg.arg1 >= 0, devStatus);
                    }
                }
            }
                break;





            case EUIMSG.START_PLAY:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).startPlay(msg, msgContent);
                    }
                }

            }
            break;

            case EUIMSG.ON_PLAY_INFO:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).onPlayInfo(msgContent);
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_END:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).onPlayEnd();
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_BEGIN:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).onPlayBufferBegin();
                    }
                }
            }
            break;
            case EUIMSG.ON_PLAY_BUFFER_END:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).onPlayBufferEnd();
                    }
                }
            }
            break;

            case EUIMSG.ON_FRAME_USR_DATA:
            {
                for (OnFunListener l : mListeners) {
                    if (l instanceof FunVideoViewListener) {
                        ((FunVideoViewListener) l).onFrameUsrData(msg, msgContent);
                    }
                }
            }
            break;




            case EUIMSG.DEV_CHECK_UPGRADE:
                for (OnFunListener l : mListeners) {
                    if (l instanceof CameraUpgradeListener) {
                        ((CameraUpgradeListener) l).devCheckUpgrade(msg, msgContent);
                    }
                }
                /* 检查设备升级 */
                break;

            case EUIMSG.DEV_START_UPGRADE:
                for (OnFunListener l : mListeners) {
                    if (l instanceof CameraUpgradeListener) {
                        ((CameraUpgradeListener) l).devStartUpgrade(msg, msgContent);
                    }
                }
                break;
            case EUIMSG.DEV_STOP_UPGRADE:
                for (OnFunListener l : mListeners) {
                    if (l instanceof CameraUpgradeListener) {
                        ((CameraUpgradeListener) l).devStopUpgrade();
                    }
                }
                break;

            case EUIMSG.DEV_ON_UPGRADE_PROGRESS:
                for (OnFunListener l : mListeners) {
                    if (l instanceof CameraUpgradeListener) {
                        ((CameraUpgradeListener) l).devOnUpgradeProgress(msg, msgContent);
                    }
                }
                break;

            default:
                break;
        }

        return 0;
    }


    public void registerCameraUpgradeListener(CameraUpgradeListener l) {
        if (!mListeners.contains(l)) {
            mListeners.add(l);
        }
    }


    public void removeCameraUpgradeListener(CameraUpgradeListener l) {
        if (mListeners.contains(l)) {
            mListeners.remove(l);
        }
    }

    public boolean requestDevStartUpgrade(String devSn,  int nType) {
        int result = FunSDK.DevStartUpgrade(mFunUserHandler, devSn, nType, 0);
        return result == 0;
    }
    public boolean requestDevStopUpgrade(String devSn) {
        int result = FunSDK.DevStopUpgrade(mFunUserHandler, devSn, 0);
        return result == 0;
    }

}
