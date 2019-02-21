package com.bisa.health.ecg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.ecg.config.ECGConfig;
import com.bisa.health.model.HServer;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.utils.FinalBisa;
import com.google.gson.Gson;

import java.util.Locale;


public class ECGConfigAcitvity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{



    private IRestService restService;
    private HServer hServer;
    private User mUser;
    private static final String TAG = "RunActivity";
    private Locale locale = Locale.getDefault();
    private final Gson gson = new Gson();
    private HealthServer mHealthServer;
    private HealthPath mHealthPath;
    private ECGConfig ecgConfig;

    private Switch autoSwitch;
    private Switch mappSwitch;
    private Switch mdeviceSwitch;

    @Override
    public synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bisa.health.R.layout.ecgset_activity);

        ecgConfig=getIntent().getParcelableExtra(ECGConfig.class.getName());

        autoSwitch=(Switch)this.findViewById(R.id.sw_a);
        autoSwitch.setOnCheckedChangeListener(this);
        autoSwitch.setChecked(ecgConfig.getAutoAlarm()==1);


        mappSwitch=(Switch)this.findViewById(R.id.sw_app);
        mappSwitch.setOnCheckedChangeListener(this);
        mappSwitch.setChecked(ecgConfig.getManualAppAlarm()==1);

        mdeviceSwitch=(Switch)this.findViewById(R.id.sw_m);
        mdeviceSwitch.setOnCheckedChangeListener(this);
        mdeviceSwitch.setChecked(ecgConfig.getManualDeviceAlarm()==1);

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.putExtra(ECGConfig.class.getName(), (Parcelable) ecgConfig);
        this.setResult(FinalBisa.CALL_ECG_CODE, intent);
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if(buttonView==autoSwitch){
          Log.i(TAG, "onCheckedChanged: a");
          ecgConfig.setAutoAlarm(isChecked?1:0);
      }else if(buttonView==mappSwitch){
          Log.i(TAG, "onCheckedChanged: b");
          ecgConfig.setManualAppAlarm(isChecked?1:0);
      }else if(buttonView==mdeviceSwitch){
          Log.i(TAG, "onCheckedChanged: c");
          ecgConfig.setManualDeviceAlarm(1);
      }

    }
}
