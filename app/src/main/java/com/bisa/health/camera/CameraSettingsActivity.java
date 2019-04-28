package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.sdk.DialogInputPasswd;
import com.lib.FunSDK;

public class CameraSettingsActivity extends BaseActivity {
    private LinearLayout viewBasic;
    private LinearLayout viewPwMng;
    private LinearLayout viewAlarm;
    private LinearLayout viewStorage;
    private LinearLayout viewAdvance;
    private LinearLayout viewCommon;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings);

        viewBasic = findViewById(R.id.llayout_camera_settings_basic);
        viewPwMng = findViewById(R.id.llayout_camera_settings_pwMng);
        viewAlarm = findViewById(R.id.llayout_camera_settings_alarm);
        viewStorage = findViewById(R.id.llayout_camera_settings_storage);
        viewAdvance = findViewById(R.id.llayout_camera_settings_advance);
        viewCommon = findViewById(R.id.llayout_camera_settings_common);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewBasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsBasicActivity.class);
                startActivity(intent);
            }
        });
        viewPwMng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsPwMngActivity.class);
                startActivity(intent);
            }
        });
        viewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsAlarmActivity.class);
                startActivity(intent);
            }
        });
        viewStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsStorageActivity.class);
                startActivity(intent);
            }
        });
        viewAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsAdvanceActivity.class);
                startActivity(intent);
            }
        });
        viewCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraSettingsActivity.this, CameraSettingsCommonActivity.class);
                startActivity(intent);
            }
        });

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                }
            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();

                    // 如果账号密码不正确,那么需要提示用户,输入密码重新登录
                    if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
                        showInputPasswordDialog();
                    }
                    else {
                        new AlertDialog.Builder(CameraSettingsActivity.this)
                                .setTitle(R.string.camera_connection_failed)
                                .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).setCancelable(false)
                                .show();
                    }
                }
            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

            }

            @Override
            public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

            }

            @Override
            public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

            }

            @Override
            public void onDeviceFileListGetFailed(FunDevice funDevice) {

            }
        };

        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);

        if(!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
            FunSupport.getInstance().requestDeviceLogin(mFunDevice);
            showDialog(false);
        }

    }

    /**
     * 显示输入设备密码对话框
     */
    private void showInputPasswordDialog() {
        DialogInputPasswd inputDialog = new DialogInputPasswd(this,
                getResources().getString(R.string.device_login_input_password), "", R.string.common_confirm,
                R.string.common_cancel) {

            @Override
            public boolean confirm(String editText) {
                // 重新以新的密码登录
                FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", editText);

                // 重新登录
                showDialog(false);
                FunSupport.getInstance().requestDeviceLogin(mFunDevice);

                return super.confirm(editText);
            }

            @Override
            public void cancel() {
                super.cancel();

                // 取消输入密码,直接退出
                finish();
            }

        };

        inputDialog.show();
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        if ( null != mFunDevice ) {
            FunSupport.getInstance().requestDeviceLogout(mFunDevice);
        }
        super.onDestroy();
    }
}
