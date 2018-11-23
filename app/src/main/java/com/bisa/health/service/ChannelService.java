package com.bisa.health.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.bisa.health.utils.Notificator;

/**
 * 虚假的渠道服务，用于 api>18 开启前台服务
 */
public class ChannelService extends Service {
   // private static final String TAG = "ChannelService";
    private static final String TAG = "----";
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand(): intent = [" + intent.toUri(0) + "], flags = [" + flags + "], startId = [" + startId + "]");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2&&Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
            startForeground(Notificator.FOREGROUND_PUST_ID, new Notification());
        }
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
