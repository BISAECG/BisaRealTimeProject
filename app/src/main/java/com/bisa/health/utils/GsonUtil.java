package com.bisa.health.utils;

import android.util.Log;

import com.bisa.health.model.support.convert.MyEnumAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2018/8/10.
 */

public class GsonUtil {
    private static final String TAG = "GsonUtil";
    private Gson gson=null;
    private GsonUtil(){
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapterFactory(new MyEnumAdapterFactory());
         gson = gb.create();
    }
    private static volatile GsonUtil instance = null;
    public static GsonUtil getInstance() {
        if (instance == null) {
            synchronized(GsonUtil.class) {
                if (instance == null) {
                    instance = new GsonUtil();
                }
            }
        }
        return instance;
    }

    public  <T> T parse(String json, Type type) {


        Log.i(TAG, "parse: 1");
        try {
            Log.i(TAG, "parse: 2");
            return this.gson.fromJson(json, type);

        } catch (Exception e) {
            
            Log.i(TAG, "parse: "+e.getMessage());
            return null;
        }

    }
}
