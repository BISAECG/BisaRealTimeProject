package com.bisa.health.rest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bisa.health.KillLoginOutActivity;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/8/14.
 */

public class TokenInterceptor implements Interceptor{

    private static final String TAG = "TokenInterceptor";
    public static final int HTTP_CODE_AUTH = 401;

    private Context context;

    public TokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response originalResponse = chain.proceed(request);
        if(originalResponse.code()== HTTP_CODE_AUTH){
            if(context instanceof Activity) {
                Log.i(TAG, "intercept: "+context.getClass().getName());
                final Activity self = (Activity) context;
                self.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ActivityUtil.startActivity(self, KillLoginOutActivity.class, false, ActionEnum.NULL);
                    }
                });
            }else{
                Log.i(TAG, "intercept: "+context.getClass().getName());
            }

        }
        return originalResponse;
    }
}
