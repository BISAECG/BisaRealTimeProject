package com.bisa.health.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.bisa.health.R;
import com.bisa.health.ble.BleDefinedConnStatus;
import com.bisa.health.ble.BleDefinedUUIDs;
import com.bisa.health.ble.BleWrapper;
import com.bisa.health.ble.BleWrapperServiceCallbacks;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.dao.IDeviceDao;
import com.bisa.health.ecg.config.ECGConfig;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.ecg.dao.ReportDaoImpl;
import com.bisa.health.ecg.enumerate.XiXinEventEnum;
import com.bisa.health.ecg.model.GprsBean;
import com.bisa.health.ecg.model.RawObject;
import com.bisa.health.model.APCServer;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.FinalBisa;
import com.bisa.health.utils.Notificator;
import com.bisa.health.utils.Utility;
import com.bisahelath.decoding.help.ECGBuildHelp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class ECGService extends Service implements BleWrapperServiceCallbacks {

    private final String TAG = "ECGService";

    /**
     * BLE 变量
     */
    private BleWrapper mBleWrapper;
    private boolean bleWrapperStatus = true;
    private boolean mBleConnStatus = false;


    private String mAddress;
    private String mDeviceName;
    private BroadcastReceiver keepLiveReceiver;
    /**
     * 自动连接设备时间
     */
    private static final int BLE_CONN_TIME_INTERVAL = 1000 * 6; // 10 seconds
    private static final int BLE_ALARM_INTERVAL = 1000 * 5; // 10 seconds
    private IEcgServiceCallBacks iEcgServiceCallBacks = null;
    private static final int BLE_TRY_CONN_COUNT = 5;
    private int BLE_CONN_NUMBER = 0;


    /**
     * 系统资源
     */
    private PowerManager pManager = null;
    private WakeLock mWakeLock = null;
    private static MediaPlayer warningPlayer = null;

    /**
     * ECG处理变量
     */
    private byte[] byteAttary = new byte[10];

    private final byte[] ecgbyte = new byte[1024];

    private static int fIndex = 0;
    private static boolean isWrite = false;
    private String sessionid;
    private File ecdFile;

    private User mUser;

    private HealthPath healthPath;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private static ECGConfig ecgConfig;

    //目前没有用到未定温服
    private LocationService locationService = null;
    private IRestService restService = null;
    /**
     * 设备电池变量
     */
    private static int batteryValue = -100;

    private IDeviceDao deviceDao;
    private IReportDao iappReportDao;
    private BufferedOutputStream bos;
    ECGBuildHelp ecgBuildHelp = new ECGBuildHelp();

    @SuppressLint("InvalidWakeLockTag")
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {


        super.onCreate();

        pManager = ((PowerManager) getSystemService(POWER_SERVICE));

        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "CPUKeepRunning");
        mWakeLock.acquire();

        sharedPersistor = new SharedPersistor(this);
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        healthPath = sharedPersistor.loadObject(HealthPath.class.getName());
        mUser = sharedPersistor.loadObject(User.class.getName());


        if (mBleWrapper == null)
            mBleWrapper = new BleWrapper(this, this);

        if (mBleWrapper.initialize() == false) {
            bleWrapperStatus = false;
        }
        if (deviceDao == null) {
            deviceDao = new DeviceDao(this);
        }
        if (iappReportDao == null) {
            iappReportDao = new ReportDaoImpl(this);
        }

        // 动态注册开关屏广播
        if (keepLiveReceiver == null) {
            keepLiveReceiver = new KeepLiveReceiver(mHandler);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.USER_PRESENT");
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络变化
            registerReceiver(keepLiveReceiver, intentFilter);

        }
        //目前没有用到定位服务
        //initBiaduApi();


        if (restService == null) {
            restService = new RestServiceImpl(this, mHealthServer);
        }
        Log.i(TAG, "onCreate Service");
    }

    public void initBiaduApi() {

        if (locationService == null) {

            locationService = new LocationService(getApplicationContext());

            SDKInitializer.initialize(getApplicationContext());
            // 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            locationService.registerListener(mListener);
            // 注册监听
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (connDevtimeout != null) {
            mHandler.removeCallbacks(connDevtimeout);
            connDevtimeout = null;
        }

        if (mBleWrapper != null && mBleWrapper.isConnected()) {
            mBleWrapper.diconnect();
            mBleWrapper.close();
            mBleWrapper = null;
        }
        try {
            if (null != mWakeLock) {
                mWakeLock.release();
            }

            if (keepLiveReceiver != null) {
                unregisterReceiver(keepLiveReceiver);
                keepLiveReceiver = null;
            }
//目前没有用到定位服务
//            if (locationService != null) {
//                locationService.stop(); // 停止定位服务
//                locationService.unregisterListener(mListener); // 注销掉监听
//            }

        } catch (Exception e) {
            e.printStackTrace();
            keepLiveReceiver = null;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "onBind: ");
        if (mBleWrapper != null && mBleWrapper.isConnected()) {
            mBleWrapper.diconnect();
            mBleWrapper.close();
            mBleWrapper = null;
        }
        ecgBuildHelp.decodeInit();
        return mBinder;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        /**
         * 如果蓝牙还在连接就必须解除连接
         */

        if (connDevtimeout != null) {
            mHandler.removeCallbacks(connDevtimeout);
            connDevtimeout = null;
        }


        if (mBleWrapper != null && mBleWrapper.isConnected()) {
            mBleWrapper.diconnect();
            mBleWrapper.close();
            mBleWrapper = null;
        }

        return super.onUnbind(intent);

    }

    /**
     * 蓝牙自动连接
     */
    private Object bleLock = new Object();
    private Runnable connDevtimeout = new Runnable() {
        @Override
        public void run() {

            synchronized (bleLock) {

                Log.i(TAG, "BLE CONN " + BLE_CONN_NUMBER + "|" + mBleWrapper.isConnected());
                BLE_CONN_NUMBER++;
                if (mBleWrapper.isConnected()) {
                    BLE_CONN_NUMBER = 0;
                } else {
                    mBleWrapper.connect(mAddress);
                    if (BLE_CONN_NUMBER < BLE_TRY_CONN_COUNT) {
                        mHandler.postDelayed(this, BLE_CONN_TIME_INTERVAL);
                    } else {

                        try {
                            mBleWrapper.close();
                            iEcgServiceCallBacks.uiCallNotifiConnEvent(BleDefinedConnStatus.BLE_CONN_FAILED);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        }

    };


    /*****
     *
     * 定位结果回调，
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                if (location.getLocType() == BDLocation.TypeGpsLocation
                        || location.getLocType() == BDLocation.TypeNetWorkLocation
                        || location.getLocType() == BDLocation.TypeOffLineLocation) {
                    GprsBean gprsBean = new GprsBean();
                    gprsBean.setGprs_type(1);
                    gprsBean.setLat("" + location.getLatitude());
                    gprsBean.setLng("" + location.getLongitude());
                    gprsBean.setAddress(location.getAddrStr());
                    gprsBean.setCollection_time(DateUtil.getGMTDateTime("yyyy-MM-dd kk:mm:ss"));

                    Log.i(TAG, "onReceiveLocation: " + location.getAddrStr());
                }
            }
        }

    };

    /* 蓝牙自动连接设备 */
    public synchronized void AutoBleConn() {

        Log.i(TAG, "BLE_CONN_NUMBER:" + BLE_CONN_NUMBER);
        if (BLE_CONN_NUMBER == 0) {
            mHandler.post(connDevtimeout);
        }

    }

    public synchronized void playerRingtone(int what, int flag) {
//目前没有用到定位服务
//        if(locationService!=null){
//            locationService.start();
//        }

        boolean loog = flag == 1 ? true : false;

        if (warningPlayer != null) {
            warningPlayer.stop();
            warningPlayer.release();
            warningPlayer = null;
        }


        if (what == R.raw.notification_004) {

            Log.i(TAG, "warningPlayer start");
            warningPlayer = MediaPlayer.create(ECGService.this, R.raw.notification_004);
            warningPlayer.setLooping(loog);
            warningPlayer.start();


        } else if (what == R.raw.notification_002) {

            Log.i(TAG, "warningPlayer start");
            warningPlayer = MediaPlayer.create(ECGService.this, R.raw.notification_002);
            warningPlayer.setLooping(loog);
            warningPlayer.start();

        }

        closeAlarm();
    }

    /**
     * UI远程服务
     */

    private final IEcgService.Stub mBinder = new IEcgService.Stub() {


        @Override
        public void uiRegisterCallBacks(IEcgServiceCallBacks iEcgCallBacks) throws RemoteException {
            if (iEcgServiceCallBacks == null) {
                Log.i(TAG, "uiRegisterCallBacks");
                iEcgServiceCallBacks = iEcgCallBacks;
            }
        }


        @Override
        public void uiNotifiMarker(boolean _isMarker) throws RemoteException {
                Message msg = new Message();
                msg.what = FinalBisa.NOTIFI_AlAM;
                msg.arg1 = XiXinEventEnum.APP.getValue();
                mHandler.sendMessage(msg);
                Log.w(TAG, "uiNotifiMarker :" + _isMarker);
        }

        @Override
        public void uiInitDrawParam() throws RemoteException {

        }

        @Override
        public String startMonitor(long timeMillis) throws RemoteException {
            String ecgFileName = mDeviceName + "_" + DateUtil.getStringDate("yyyyMMddHHmmss", new Date(timeMillis)) + ".ECD";
            ecdFile = new File(healthPath.getFreedat(), ecgFileName);
            try {

                FileOutputStream fos = new FileOutputStream(ecdFile);
                bos=new BufferedOutputStream(fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fIndex=0;
            isWrite = true;
            return ecgFileName;
        }


        @Override
        public String endMonitor() throws RemoteException {

            isWrite = false;
            fIndex=0;
            try {
                if (bos != null) {
                    bos.write(ecgbyte, 0, fIndex);
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ecdFile.getAbsolutePath();
        }

        @Override
        public void startForeground() throws RemoteException {
            Message msg = new Message();
            msg.what = FinalBisa.SERVER_BACK_STATUS;
            msg.arg1 = FinalBisa.SERVER_BACK_START;
            mHandler.sendMessage(msg);
        }

        @Override
        public void stopForeground() throws RemoteException {
            Message msg = new Message();
            msg.what = FinalBisa.SERVER_BACK_STATUS;
            msg.arg1 = FinalBisa.SERVER_BACK_STOP;
            mHandler.sendMessage(msg);
        }


        @Override
        public void uiDeviceConnected(String address, String devicename) throws RemoteException {
            BLE_CONN_NUMBER = 0;
            iEcgServiceCallBacks.uiCallNotifiConnEvent(BleDefinedConnStatus.BLE_CONN_ING);
            mAddress = address;
            mDeviceName = devicename;
            if (bleWrapperStatus) {
                AutoBleConn();
            }


        }

        @Override
        public void uiDeviceClose() throws RemoteException {

            if (mBleWrapper != null) {
                mBleWrapper.close();
            }

            mAddress = null;


            Log.i(TAG, "CLOSE BLE");

        }

        @Override
        public void uiECGConfig(ECGConfig config) throws RemoteException {
            ecgConfig = config;
        }

    };


    /***
     * 下面是蓝牙回调事件
     */
    @Override
    public void callDeviceFound(BluetoothDevice device, int rssi, byte[] record) {

    }

    /**
     * 蓝牙连接成功事件
     */
    @Override
    public void callDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {

    }

    /**
     * 蓝牙连接失败事件
     */
    @Override
    public void callDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {

        try {
            iEcgServiceCallBacks.uiCallNotifiConnEvent(BleDefinedConnStatus.BLE_CONN_ING);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        AutoBleConn();

        /**
         * 播放铃声
         */
        playerRingtone(R.raw.notification_004, 1);
        Log.i(TAG, "断开连接:");

    }

    private BluetoothGattCharacteristic notifyCharacteristic;

    @Override
    public void callAvailableServices(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService services) {

        if (services != null && services.getCharacteristic(BleDefinedUUIDs.Characteristic.ECG_RATE_STRING) != null) {
            BluetoothGattCharacteristic mNotifyCharacteristic = services.getCharacteristic(BleDefinedUUIDs.Characteristic.ECG_RATE_STRING);
            notifyCharacteristic = mNotifyCharacteristic;
            mBleWrapper.setNotificationForCharacteristic(notifyCharacteristic, true);
            try {
                iEcgServiceCallBacks.uiCallNotifiConnEvent(BleDefinedConnStatus.BLE_CONN_SECCUESS);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void callCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                             List<BluetoothGattCharacteristic> chars) {

    }

    @Override
    public void callCharacteristicsDetails(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                           BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void callGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                    BluetoothGattCharacteristic characteristic) {


    }

    @Override
    public void callSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                    BluetoothGattCharacteristic ch, String description) {

    }

    @Override
    public void callFailedWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                BluetoothGattCharacteristic ch, String description) {

    }

    @Override
    public void callNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, int rssi) {

        if (iEcgServiceCallBacks != null) {
            try {
                iEcgServiceCallBacks.uiNewRssiAvailable(rssi);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 上传数创实时传送的参数
     */


    @Override
    public void callNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
                                              BluetoothGattCharacteristic ch, byte[] rawValue) {

        if (rawValue == null) {
            return;
        }

        /**
         * 15分钟报告开始
         */
        if (isWrite && bos != null) {
            for (int i = 0; i < rawValue.length; i++) {
                ecgbyte[fIndex++] = rawValue[i];
                try {
                    if (fIndex == ecgbyte.length) {
                        bos.write(ecgbyte);
                        fIndex = 0;
                    }

                } catch (IOException e) {
                    try {
                        if (bos != null)
                            bos.close();
                        bos = null;
                        if (ecdFile != null && ecdFile.exists()) {
                            ecdFile.delete();
                            ecdFile = null;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }

        /**
         * 15分钟报告结束
         */


        if (iEcgServiceCallBacks != null) {


            int[] value = new int[rawValue.length];
            for (int i = 0; i < rawValue.length; i++) {

                /**
                 * 处理ECD和RAW文件
                 */
                int[] valueAtr = ecgBuildHelp.decodeByteValue(rawValue[i]);
                value[i] = valueAtr[1] * 65536 + valueAtr[2];

                if (valueAtr[4] != -1 && valueAtr[4] != batteryValue) {
                    if (valueAtr[4] == 0) {
                        batteryValue = 0;
                    } else {
                        batteryValue = valueAtr[4];
                    }
                    int tempBatteryV = Math.round(batteryValue / 25f);
                    try {
                        iEcgServiceCallBacks.uiUpdateDeviceBattert(tempBatteryV);
                    } catch (RemoteException e) {
                        Log.i(TAG, "callNewValueForCharacteristic: " + e.getMessage());
                    }
                }


                RawObject rawObject = is_marker(valueAtr, ecgConfig);
                if (rawObject != null) {

                    Message msg = new Message();
                    msg.what = FinalBisa.NOTIFI_AlAM;
                    msg.arg1 = rawObject.getMarker_type();
                    mHandler.sendMessage(msg);
                }

            }

            try {
                iEcgServiceCallBacks.ecgDrawing(value);
            } catch (RemoteException e) {
                Log.i(TAG, "callNewValueForCharacteristic: " + e.getMessage());
            }

        }
    }


    /**
     * 关闭报警音乐
     */

    private void closeAlarm() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {

                if (warningPlayer != null) {
                    Log.i(TAG, "closeAlarm");
                    warningPlayer.stop();
                    warningPlayer.release();
                    warningPlayer = null;
                }
//目前没有用到定位服务
//                if (locationService != null) {
//                    locationService.stop();
//                }
            }
        };
        mHandler.postDelayed(timeout, BLE_ALARM_INTERVAL);
    }

    /**
     * 处理控件
     */
    private Object serviceLock = new Object();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @SuppressLint("WrongConstant")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case FinalBisa.NOTIFI_AlAM:

                    synchronized (serviceLock) {

                        playerRingtone(R.raw.notification_002, 0);
                        int tempLate = 1;
                        if (msg.arg1 == XiXinEventEnum.DEVICE.getValue()) {
                            tempLate = 2;
                        } else if (msg.arg1 == XiXinEventEnum.PASSIVE.getValue()) {
                            tempLate = 3;
                        }


                        /**
                         * 发送求救短信
                         */

                        Map<String, String> param = new HashMap<String, String>();
                        param.put("tempLate", "" + tempLate);
                        Call call = restService.sendSOS(param);

                        call.enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                                if (iEcgServiceCallBacks != null) {
                                    try {
                                        iEcgServiceCallBacks.uiCallNotifiAlam(FinalBisa.NOTIFI_AlAM_SUCCESS, getString(R.string.server_error));
                                    } catch (RemoteException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String json = response.body().string();
                                final APCServer appServer = Utility.jsonToObject(json, APCServer.class);

                                if (appServer == null) {
                                    return;
                                }
                                if (iEcgServiceCallBacks != null) {

                                    try {
                                        switch (appServer.getCode()) {
                                            case HttpFinal.SUCCESS:
                                                iEcgServiceCallBacks.uiCallNotifiAlam(FinalBisa.NOTIFI_AlAM_SUCCESS, getString(R.string.msg_success_opt));
                                                break;
                                            case HttpFinal.NOT_EVENT:
                                                iEcgServiceCallBacks.uiCallNotifiAlam(FinalBisa.NOTIFI_AlAM_SUCCESS, getString(R.string.msg_not_contacts));
                                                break;
                                            case HttpFinal.NOT_HEALTH_SERVICE:
                                                iEcgServiceCallBacks.uiCallNotifiAlam(FinalBisa.NOTIFI_AlAM_SUCCESS, getString(R.string.msg_not_service));
                                                break;
                                            default:
                                                iEcgServiceCallBacks.uiCallNotifiAlam(FinalBisa.NOTIFI_AlAM_SUCCESS, getString(R.string.msg_success_opt));
                                                break;
                                        }
                                    } catch (RemoteException e1) {
                                        e1.printStackTrace();
                                    }

                                }
                            }
                        });

                    }
                    break;


                case FinalBisa.NEWWPRK_SATAUS_SUCCESS:
                    try {
                        if (iEcgServiceCallBacks != null)
                            iEcgServiceCallBacks.uiUpdateNetWork(FinalBisa.NEWWPRK_SATAUS_SUCCESS);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case FinalBisa.NEWWPRK_SATAUS_FAIL:
                    try {
                        if (iEcgServiceCallBacks != null)
                            iEcgServiceCallBacks.uiUpdateNetWork(FinalBisa.NEWWPRK_SATAUS_FAIL);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case FinalBisa.SERVER_BACK_STATUS:
                    // 开启前台服务
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        if (msg.arg1 == FinalBisa.SERVER_BACK_START) {
                            startForeground(Notificator.FOREGROUND_PUST_ID, new Notification());
                        } else {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                stopForeground(true);
                            } else {
                                stopForeground(Notificator.FOREGROUND_PUST_ID);
                            }

                        }

                    }

                    break;

            }
        }
    };


    public synchronized void removeAutoBleConn() {
        mHandler.removeCallbacks(connDevtimeout);
    }


    private RawObject is_marker(int[] markerAtr, ECGConfig config) {
        // Log.i(TAG, "is_marker: "+config);
        if (markerAtr[0] == XiXinEventEnum.DEVICE.getValue() && config.getManualDeviceAlarm() == 1) {

            RawObject rawObject = new RawObject();
            rawObject.setMarker_type(XiXinEventEnum.DEVICE.getValue());
            rawObject.setEvent_type(0);
            return rawObject;

        }

        if (markerAtr[0] == XiXinEventEnum.PASSIVE.getValue() && config.getAutoAlarm() == 1) {
            RawObject rawObject = new RawObject();
            rawObject.setMarker_type(XiXinEventEnum.PASSIVE.getValue());
            rawObject.setEvent_type(1);
            return rawObject;

        }
        return null;
    }


}
