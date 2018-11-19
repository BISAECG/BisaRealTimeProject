package com.bisa.health.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bisa.health.KeepLiveActivity;
import com.bisa.health.thread.CheckTopTask;
import com.bisa.health.utils.FinalBisa;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

public class KeepLiveReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Handler mServiceHandler;

    private CheckTopTask mCheckTopTask ;

    public KeepLiveReceiver() {
    }

    public KeepLiveReceiver(Handler mServiceHandler) {
        this.mServiceHandler = mServiceHandler;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        mCheckTopTask= new CheckTopTask(context);
        Log.i("----", "onReceive(): context = [" + context + "], intent = [" + intent + "]");
        String action = intent.getAction();
        // 这里可以启动一些服务
        try {
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                CheckTopTask.startForeground(context);
                mHandler.postDelayed(mCheckTopTask, 3000);
            } else if ("android.intent.action.USER_PRESENT".equals(action) || "android.intent.action.SCREEN_ON".equals(action)) {
                KeepLiveActivity onePxActivity = KeepLiveActivity.instance != null ? KeepLiveActivity.instance.get() : null;
                if (onePxActivity != null) {
                    onePxActivity.finishSelf();
                }
                mHandler.removeCallbacks(mCheckTopTask);
            }else if("android.net.conn.CONNECTIVITY_CHANGE".equals(action)){
                ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {

                    Log.w(TAG, "onReceive: NEWWPRK_SATAUS_SUCCESS");
                    switch (networkInfo.getType()) {
                        case TYPE_MOBILE:
                            mServiceHandler.sendEmptyMessage(FinalBisa.NEWWPRK_SATAUS_SUCCESS);
                            break;
                        case TYPE_WIFI:
                            mServiceHandler.sendEmptyMessage(FinalBisa.NEWWPRK_SATAUS_SUCCESS);
                            break;
                        default:
                            break;
                    }
                } else {
                    Log.w(TAG, "onReceive: NEWWPRK_SATAUS_FAIL");
                    mServiceHandler.sendEmptyMessage(FinalBisa.NEWWPRK_SATAUS_FAIL);
                }

            }else if("com.bisa.helath.ecg.config".equals(action)){
                mServiceHandler.sendEmptyMessage(FinalBisa.ECG_CONFIG);
            }
        } catch (Exception e) {
            Log.e("----", "e:" + e);
        }
    }

}
