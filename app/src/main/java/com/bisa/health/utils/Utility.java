package com.bisa.health.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2017/8/2.
 */

public class Utility {
    // 网络检查
    public static boolean isConnectInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    public static <T> T jsonToObject(String json, Type type) {

        try {
            return new Gson().fromJson(json, type);

        } catch (Exception e) {
            return null;
        }

    }
    public static String hideStrOfPwd(String str){
        if(str.length()>7) {
            String first = str.substring(0, 3);
            String textPwd = "****";
            String last = str.substring(str.length() - 3);
            return first + textPwd + last;
        }
        return str;
    }

}
