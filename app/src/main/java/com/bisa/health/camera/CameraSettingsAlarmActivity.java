package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.DetectMotion;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.DevSDK;

public class CameraSettingsAlarmActivity extends BaseActivity {
    private Switch switchAlarmOpen;
    private LinearLayout viewAlarmMode;
    private TextView tvAlarmMode;
    private Spinner spinnerAlarmSensitivity;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    private String[] items;
    private boolean[] alarmModes = new boolean[3];
    private boolean[] alarmModesTmp = new boolean[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_alarm);

        switchAlarmOpen = findViewById(R.id.switch_camera_settings_alarm_open);
        viewAlarmMode = findViewById(R.id.llayout_camera_settings_alarm_mode);
        tvAlarmMode = findViewById(R.id.tv_camera_settings_alarm_mode);
        spinnerAlarmSensitivity = findViewById(R.id.spinner_camera_settings_alarm_sensitivity);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchAlarmOpen.setTextOn("");
            switchAlarmOpen.setTextOff("");
            switchAlarmOpen.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchAlarmOpen.setTrackResource(R.drawable.camera_settings_switch_track_selector);
        }

        items = getResources().getStringArray(R.array.camera_settings_alarm_modes);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 删除老的配置信息
        mFunDevice.invalidConfig(DetectMotion.CONFIG_NAME);
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, DetectMotion.CONFIG_NAME, mFunDevice.CurrChannel);
        showDialog(false);

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
                if (funDevice.getId() == mFunDevice.getId()) {
                    dialogDismiss();
                    if(configName.equals(DetectMotion.CONFIG_NAME)) {
                        // 移动侦测
                        DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
                        if ( null != detectMotion ) {
                            switchAlarmOpen.setChecked(detectMotion.Enable);
                            alarmModes[0] = detectMotion.event.SnapEnable;
                            alarmModes[1] = detectMotion.event.RecordEnable;
                            alarmModes[2] = detectMotion.event.VoiceEnable;
                            alarmModesTmp[0] = alarmModes[0];
                            alarmModesTmp[1] = alarmModes[1];
                            alarmModesTmp[2] = alarmModes[2];
                            alarmModeSetText();

                            spinnerAlarmSensitivity.setSelection(changeLevelToUI(detectMotion.Level));
                        }
                    }
                }
            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    showToast(FunError.getErrorStr(errCode));
                }
            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if (funDevice.getId() == mFunDevice.getId()) {
                    if(configName.equals(DetectMotion.CONFIG_NAME)) {
                        DetectMotion detectMotion = (DetectMotion)funDevice.getConfig(DetectMotion.CONFIG_NAME);
                        if(detectMotion != null) {
                            switchAlarmOpen.setChecked(detectMotion.Enable);
                            alarmModeSetText();
                            spinnerAlarmSensitivity.setSelection(changeLevelToUI(detectMotion.Level));
                        }
                    }
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    Toast.makeText(CameraSettingsAlarmActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
                }
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

        switchAlarmOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
                if(detectMotion != null) {
                    if(detectMotion.Enable != isChecked) {
                        detectMotion.Enable = isChecked;
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, detectMotion);
                    }
                }
            }
        });

        viewAlarmMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(CameraSettingsAlarmActivity.this)
                        .setMultiChoiceItems(items, alarmModes, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                alarmModesTmp[which] = isChecked;
                            }
                        })
                        .setPositiveButton(getString(R.string.camera_settings_basic_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
                                if(detectMotion != null) {
                                    alarmModes[0] = alarmModesTmp[0];
                                    alarmModes[1] = alarmModesTmp[1];
                                    alarmModes[2] = alarmModesTmp[2];
                                    detectMotion.event.SnapEnable = alarmModesTmp[0];
                                    detectMotion.event.SnapShotMask = DevSDK.SetSelectHex(
                                            detectMotion.event.SnapShotMask, mFunDevice.CurrChannel,
                                            detectMotion.event.SnapEnable);
                                    detectMotion.event.RecordEnable = alarmModesTmp[1];
                                    detectMotion.event.RecordMask = DevSDK.SetSelectHex(
                                            detectMotion.event.RecordMask, mFunDevice.CurrChannel,
                                            detectMotion.event.RecordEnable);
                                    detectMotion.event.VoiceEnable = alarmModesTmp[2];
                                    detectMotion.event.AlarmOutMask = DevSDK.SetSelectHex(
                                            detectMotion.event.AlarmOutMask, mFunDevice.CurrChannel,
                                            detectMotion.event.VoiceEnable);

                                    FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, detectMotion);
                                }
                            }
                        })
                        .show();
            }
        });

        spinnerAlarmSensitivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
                if(detectMotion != null) {
                    if(detectMotion.Level != changeLevelToDetect(position)) {
                        detectMotion.Level = changeLevelToDetect(position);
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, detectMotion);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void alarmModeSetText() {
        String alarmMode = (alarmModes[0] ? items[0] : "") + (alarmModes[1] ? "," + items[1] : "")
                + (alarmModes[2] ? "," + items[2] : "");
        if(alarmMode.startsWith(",")) {
            alarmMode = alarmMode.substring(1);
        }
        tvAlarmMode.setText(alarmMode);
    }

    /**
     * Level报警灵敏度转换,界面低级/中级/高级(0,1,2),需要和实际级别做一个转换
     * @param level
     * @return
     */
    private int changeLevelToUI(int level) {
        int uiLevel = (level == 0 ? 1 : (level % 2 + level / 2)) - 1;
        return Math.max(0, uiLevel);
    }
    private int changeLevelToDetect(int uiLevel) {
        return (uiLevel+1) * 2;
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
