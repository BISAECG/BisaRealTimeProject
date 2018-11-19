package com.bisa.health.ecg;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.bisa.health.BaseActivity;

public class CameraActivity extends BaseActivity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("----","CameraActivity>>>>>onCreate"+"|task:"+getTaskId());
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        Log.i("----","CameraActivity>>>>>onDestroy"+"|task:"+getTaskId());
        super.onDestroy();
    }
}
