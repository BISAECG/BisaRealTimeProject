package com.bisa.health.camera;

import android.content.Context;

import com.basic.G;
import com.bisa.health.camera.lib.funsdk.support.FunLog;
import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.utils.SharedParamMng;
import com.bisa.health.camera.lib.sdk.struct.SInitParam;
import com.lib.EFUN_ATTR;
import com.lib.FunSDK;

public class CameraSdkInit {
    // 应用证书,请在开放平台注册应用之后替换以下4个字段
    private static final String APP_UUID = "0f16ed53820847ddb0b148ffa876e7d8";
    private static final String APP_KEY = "39cb860b743947d9837453ead4ad59d9";
    private static final String APP_SECRET = "cb342be4e3714287907715237f88037e";
    private static final int APP_MOVECARD = 6;

    public static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";
    public static final int SERVER_PORT = 15010; // 更新版本的服务器端口

    private static int userID;


    public static void init(Context context) {
        int result = 0;

        // 初始化目录
        FunPath.init(context, context.getPackageName());

        SharedParamMng mSharedParam = new SharedParamMng(context);


        // 库初始化1
        SInitParam param = new SInitParam();
        param.st_0_nAppType = SInitParam.LOGIN_TYPE_MOBILE;
        result = FunSDK.Init(0, G.ObjToBytes(param));


        // 降低隐藏到后台时cpu使用及耗电
        //FunSDK.SetApplication((MyApplication)mContext.getApplicationContext());
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

        // 初始化APP证书(APP启动后调用一次即可)
        FunSDK.XMCloundPlatformInit(
                APP_UUID,        // uuid
                APP_KEY, // App Key
                APP_SECRET, // App Secret
                APP_MOVECARD); // moveCard

    }

    public static void setUserID(int userID1) {
        userID = userID1;
    }
    public static int getUserID() {
        return userID;
    }

}
