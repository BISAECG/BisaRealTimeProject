package com.bisa.health.ecg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.config.ECGSetConfig;
import com.bisa.health.model.HServer;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.rest.service.IRestService;
import com.google.gson.Gson;

import java.util.Locale;


public class ECGSetAcitvity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{


    private SharedPersistor sharedPersistor;
    private IRestService restService;
    private HServer hServer;
    private User mUser;
    private static final String TAG = "RunActivity";
    private Locale locale = Locale.getDefault();
    private final Gson gson = new Gson();
    private HealthServer mHealthServer;
    private HealthPath mHealthPath;
    private ECGSetConfig ecgSetConfig;

    private Switch autoSwitch;
    private Switch mappSwitch;
    private Switch mdeviceSwitch;

    @Override
    public synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bisa.health.R.layout.ecgset_activity);
        if(sharedPersistor==null)
            sharedPersistor=new SharedPersistor(this.getApplicationContext());
        ecgSetConfig=sharedPersistor.loadObject(ECGSetConfig.class.getName());

        autoSwitch=(Switch)this.findViewById(R.id.sw_a);
        autoSwitch.setOnCheckedChangeListener(this);
        autoSwitch.setChecked(ecgSetConfig.isAutoAlarm());


        mappSwitch=(Switch)this.findViewById(R.id.sw_app);
        mappSwitch.setOnCheckedChangeListener(this);
        mappSwitch.setChecked(ecgSetConfig.isManualAppAlarm());

        mdeviceSwitch=(Switch)this.findViewById(R.id.sw_m);
        mdeviceSwitch.setOnCheckedChangeListener(this);
        mdeviceSwitch.setChecked(ecgSetConfig.isManualDeviceAlarm());

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if(buttonView==autoSwitch){
          Log.i(TAG, "onCheckedChanged: a");
          ecgSetConfig.setAutoAlarm(isChecked);
      }else if(buttonView==mappSwitch){
          Log.i(TAG, "onCheckedChanged: b");
          ecgSetConfig.setManualAppAlarm(isChecked);
      }else if(buttonView==mdeviceSwitch){
          Log.i(TAG, "onCheckedChanged: c");
          ecgSetConfig.setManualDeviceAlarm(isChecked);
      }
      sharedPersistor.saveObject(ecgSetConfig);
        Intent intent = new Intent();
        intent.setAction("com.bisa.helath.ecg.config");
        //也可以像注释这样写
        sendBroadcast(intent);//发送标准广播
    }
}
