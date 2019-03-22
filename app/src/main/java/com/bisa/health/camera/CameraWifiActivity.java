package com.bisa.health.camera;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basic.G;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;

import com.bisa.health.camera.lib.funsdk.support.models.FunDevType;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunDeviceBuilder;
import com.bisa.health.camera.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.bisa.health.camera.sdk.DeviceWifiManager;
import com.bisa.health.camera.sdk.FunPath;
import com.bisa.health.camera.sdk.MyUtils;
import com.bisa.health.camera.sdk.SInitParam;
import com.bisa.health.camera.sdk.StringUtils;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.IpUtil;
import com.lib.ECONFIG;
import com.lib.EDEV_OPTERATE;
import com.lib.EFUN_ATTR;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.lib.EUIMSG.DEV_SLEEP;


public class CameraWifiActivity extends BaseActivity {
    private EditText etWifiSsid, etWifiPw;
    private Button btnConfirm;

    private int mFunUserHandler = -1;
    // 应用证书,请在开放平台注册应用之后替换以下4个字段
    private static final String APP_UUID = "0f16ed53820847ddb0b148ffa876e7d8";
    private static final String APP_KEY = "39cb860b743947d9837453ead4ad59d9";
    private static final String APP_SECRET = "cb342be4e3714287907715237f88037e";
    private static final int APP_MOVECARD = 6;

    public static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";
    public static final int SERVER_PORT = 15010; // 更新版本的服务器端口

    // 局域网内的设备列表
    private List<FunDevice> mLanDeviceList = new ArrayList<FunDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_wifi);

        etWifiSsid = findViewById(R.id.et_camera_wifi_ssid);
        etWifiPw = findViewById(R.id.et_camera_wifi_pw);

        btnConfirm = findViewById(R.id.btn_camera_wifi_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSetting();
                ActivityUtil.startActivity(CameraWifiActivity.this, CameraSearchActivity.class, ActionEnum.NULL);
            }
        });

        String currSSID = getConnectWifiSSID();
        etWifiSsid.setText(currSSID);

        init(getApplicationContext());


    }

    private String getConnectWifiSSID() {
        try {
            WifiManager wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifimanager.getConnectionInfo().getSSID().replace("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void init(Context context) {
        int result = 0;

        Context mContext = context;

        // 初始化目录
        FunPath.init(context, context.getPackageName());

        //mSharedParam = new SharedParamMng(context);
        // 导入保存的参数配置
        //loadParams();

        System.out.println("--funsdk init--");
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
        //FunSDK.SetApplication((MyApplication)mContext.getApplicationContext());
        // 库初始化2
        //FunSDK.MyInitNetSDK();

        // 设置临时文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, FunPath.getDefaultPath());
        // 设置设备更新文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.UPDATE_FILE_PATH, FunPath.getDeviceUpdatePath());
        // 设置SDK相关配置文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.CONFIG_PATH,FunPath.getDeviceConfigPath());
        // 设置以互联网的方式访问
        //result = FunSDK.SysInitNet(SERVER_IP, SERVER_PORT);
        //FunLog.i(TAG, "FunSDK.SysInitNet : " + result);

        result = FunSDK.SysInitAsAPModle(FunPath.getDeviceApPath());


        // 初始化APP证书(APP启动后调用一次即可)
        FunSDK.XMCloundPlatformInit(
                APP_UUID,        // uuid
                APP_KEY, // App Key
                APP_SECRET, // App Secret
                APP_MOVECARD); // moveCard

        // 创建/注册库接口操作句柄
        mFunUserHandler = FunSDK.RegUser(new IFunSDKResult() {
            @Override
            public int OnFunSDKResult(Message msg, MsgContent msgContent) {
                if(msg.what == EUIMSG.DEV_AP_CONFIG) {
                    if (msg.arg1 >= 0) {
                        SDK_CONFIG_NET_COMMON_V2 common = new SDK_CONFIG_NET_COMMON_V2();
                        G.BytesToObj(common, msgContent.pData);
                        FunDevice funDevice = addLanDevice(common);

                        Toast.makeText(CameraWifiActivity.this, funDevice.devSn, Toast.LENGTH_LONG).show();
                    }
                }
                return 0;
            }
        });

    }

    // 开始快速配置
    private void startQuickSetting() {

        try {
            WifiManager wifiManage = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManage.getConnectionInfo();
            DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

            if ( null == wifiInfo ) {
                //showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            String ssid = wifiInfo.getSSID().replace("\"", "");
            if ( StringUtils.isStringNULL(ssid) ) {
                //showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }

            ScanResult scanResult = DeviceWifiManager.getInstance(this).getCurScanResult(ssid);
            if ( null == scanResult ) {
                //showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }


            int pwdType = MyUtils.getEncrypPasswordType(scanResult.capabilities);
            String wifiPwd = etWifiPw.getText().toString().trim();

            if ( pwdType != 0 && StringUtils.isStringNULL(wifiPwd) ) {
                // 需要密码
                //showToast(R.string.device_opt_set_wifi_info_error);
                return;
            }


            StringBuffer data = new StringBuffer();
            data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);

            String submask;
            if (wifiDhcp.netmask == 0) {
                submask = "255.255.255.0";
            } else {
                submask = IpUtil.formatIpAddress(wifiDhcp.netmask);
            }

            String mac = wifiInfo.getMacAddress();
            StringBuffer info = new StringBuffer();
            info.append("gateway:").append(IpUtil.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
                    .append(IpUtil.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
                    .append(" dns1:").append(IpUtil.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
                    .append(IpUtil.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
                    .append(" ");


            startWiFiQuickConfig(ssid,
                    data.toString(), info.toString(),
                    IpUtil.formatIpAddress(wifiDhcp.gateway),
                    pwdType, 0, mac, -1);

            //FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startWiFiQuickConfig(String ssid,
                                        String data, String info, String gw_ipaddr, int type, int isbroad, String mac, int nTimeout) {
        int nGetType = 2; // 2代配置
        int result = FunSDK.DevStartAPConfig(mFunUserHandler,
                nGetType,
                ssid, data, info, gw_ipaddr, type, isbroad, mac, nTimeout);
        return (result == 0);
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

    public FunDevice findLanDevice(String devSn) {
        for (FunDevice funDevice : mLanDeviceList) {
            if (devSn.equals(funDevice.getDevSn())) {
                return funDevice;
            }
        }
        return null;
    }
}
