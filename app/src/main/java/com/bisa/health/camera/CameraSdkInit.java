package com.bisa.health.camera;

import android.content.Context;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;

public class CameraSdkInit {
    private static boolean isInited = false;

    public static void init(Context context) {
        if(!isInited) {
            FunSupport.getInstance().init(context);
            isInited = true;
        }
    }

    public static void unInit() {
        if(isInited) {
            FunSupport.getInstance().term();
            isInited = false;
        }
    }

}
