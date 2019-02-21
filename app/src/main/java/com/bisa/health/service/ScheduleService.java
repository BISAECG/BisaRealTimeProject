package com.bisa.health.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScheduleService extends JobService {
    private static final String TAG = "ScheduleService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob(): params = [" + params + "]");
        Intent intent = new Intent(getApplicationContext(), ECGService.class);
        startService(intent);
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob(): params = [" + params + "]");
        return false;
    }
}
