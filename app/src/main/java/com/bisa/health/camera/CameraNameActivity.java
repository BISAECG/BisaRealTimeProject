package com.bisa.health.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.bean.StringUtils;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.model.BleNamePreSuffix;
import com.bisa.health.model.Device;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.DeviceTypeEnum;

public class CameraNameActivity extends BaseActivity {
    private EditText etName;
    private Button btnSave;

    private FunDevice funDevice;
    private DeviceDao deviceDao;
    private SharedPersistor sharedPersistor;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_name);

        etName = findViewById(R.id.et_camera_name);
        btnSave = findViewById(R.id.btn_camera_name_save);

        funDevice = FunSupport.getInstance().mCurrDevice;
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        deviceDao = new DeviceDao(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDevice();
                finish();
            }
        });
    }

    private void saveDevice() {
        if(!StringUtils.isStringNULL(etName.getText().toString())) {
            funDevice.devName = etName.getText().toString();
        }
        SharedPreferences sharedPref = getSharedPreferences(String.valueOf(mUser.getUser_guid()), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(funDevice.getDevSn(), funDevice.toJson().toString());
        editor.apply();

        BleNamePreSuffix bleEnum = BleNamePreSuffix.valueOf(DeviceTypeEnum.CAMERA.vlaueOf());
        Device device = new Device();
        device.setClzName(bleEnum.getClzName());
        device.setDevname(getResources().getString(bleEnum.getDevName()));
        device.setDevnum(funDevice.getDevSn());
        device.setMacadderss(funDevice.getDevMac());
        device.setUser_guid(mUser.getUser_guid());
        device.setIcoflag(DeviceTypeEnum.CAMERA.vlaueOf());
        device.setCustName(funDevice.getDevName());
        deviceDao.add(device);
    }
}
