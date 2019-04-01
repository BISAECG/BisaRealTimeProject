package com.bisa.health.camera;

import android.content.Context;

import com.bisa.health.camera.lib.funsdk.support.FunSupport;

public class CameraSdkInit {
    private static boolean isInit = false;

    public static void init(Context context) {
        if(!isInit) {
            FunSupport.getInstance().init(context);
            isInit = true;
        }
    }
}
