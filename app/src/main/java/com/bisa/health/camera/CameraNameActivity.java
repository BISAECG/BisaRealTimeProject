package com.bisa.health.camera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.MainActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.bean.StringUtils;
import com.bisa.health.cust.view.ActionBarView;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.model.BleNamePreSuffix;
import com.bisa.health.model.Device;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.DeviceTypeEnum;

public class CameraNameActivity extends BaseActivity {
    private EditText etName;
    private Button btnSave;
    private ImageButton ibtnClear;

    private ActionBarView actionBarView;

    private FunDevice funDevice;
    private DeviceDao deviceDao;
    private SharedPersistor sharedPersistor;
    private User mUser;

    private long backPressMs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_name);

        etName = findViewById(R.id.et_camera_name);
        btnSave = findViewById(R.id.btn_camera_name_save);
        ibtnClear = findViewById(R.id.ibtn_camera_name_clear);

        actionBarView = findViewById(R.id.abar_title);

        funDevice = FunSupport.getInstance().mCurrDevice;
        if(funDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        deviceDao = new DeviceDao(this);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)) {
                    ibtnClear.setVisibility(View.VISIBLE);
                }
                else {
                    ibtnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ibtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDevice();
                Intent intent = new Intent(CameraNameActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        actionBarView.setOnItemSelectListenerBack(new ActionBarView.OnActionClickListener() {
            @Override
            public void onActionClick() {

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
        deviceDao.upOrSaveBySn(device);
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - backPressMs > 300) {
            backPressMs = System.currentTimeMillis();
            showToast(getString(R.string.camera_pw_backPress_tips));
        }
        else {
            super.onBackPressed();
        }
    }

}
