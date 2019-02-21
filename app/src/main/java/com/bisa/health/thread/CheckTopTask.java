package com.bisa.health.thread;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bisa.health.KeepLiveActivity;

import java.util.List;

/**
 * Created by Silence on 2016.12.31
 */
public class CheckTopTask implements Runnable {
    private static final String TAG = "CheckTopTask";
    private Context context;

    public CheckTopTask(Context context) {
        this.context = context;
    }

    public static void startForeground(Context context) {
        try {
            Intent intent = new Intent(context, KeepLiveActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
    }

    @Override
    public void run() {
        boolean foreground = isForeground(context);
        Log.i(TAG, "foreground:" + foreground);
        if (!foreground) {
            startForeground(context);
        }
    }

    private boolean isForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                int myPid = android.os.Process.myPid();
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (runningAppProcessInfo.pid == myPid) {
                        return runningAppProcessInfo.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
        return false;
    }

}