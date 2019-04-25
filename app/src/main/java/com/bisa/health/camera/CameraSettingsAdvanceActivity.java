package com.bisa.health.camera;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.RecordParam;
import com.bisa.health.camera.lib.funsdk.support.config.SimplifyEncode;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.SDKCONST;

public class CameraSettingsAdvanceActivity extends BaseActivity {
    private Switch switchAudioEnable;
    private Spinner spinnerVideoQuality;
    private Spinner spinnerRecordMode;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_advance);

        switchAudioEnable = findViewById(R.id.switch_camera_settings_advance_audioEnable);
        spinnerVideoQuality = findViewById(R.id.spinner_camera_settings_advance_videoQuality);
        spinnerRecordMode = findViewById(R.id.spinner_camera_settings_advance_recordMode);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchAudioEnable.setTextOn("");
            switchAudioEnable.setTextOff("");
            switchAudioEnable.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchAudioEnable.setTrackResource(R.drawable.camera_settings_switch_track_selector);
        }

        mFunDevice = FunSupport.getInstance().mCurrDevice;

        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    if(configName.equals(SimplifyEncode.CONFIG_NAME) || configName.equals(RecordParam.CONFIG_NAME)) {
                        dialogDismiss();
                        refreshConfig();
                    }
                }

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    Toast.makeText(CameraSettingsAdvanceActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    if(configName.equals(SimplifyEncode.CONFIG_NAME) || configName.equals(RecordParam.CONFIG_NAME)) {
                        refreshConfig();
                    }
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    Toast.makeText(CameraSettingsAdvanceActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
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

        switchAudioEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
                if ( null != simplifyEncode ) {
                    if(simplifyEncode.mainFormat.AudioEnable != isChecked) {
                        simplifyEncode.mainFormat.AudioEnable = isChecked;
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simplifyEncode);
                    }
                }
            }
        });

        spinnerVideoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
                if (null != simplifyEncode) {
                    if(simplifyEncode.mainFormat.video.Quality != 6 - position) {
                        // 清晰度
                        simplifyEncode.mainFormat.video.Quality = 6 - position;
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simplifyEncode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerRecordMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
                if (null != recordParam) {
                    int selection;
                    if(getIntRecordMode(recordParam.getRecordMode()) == 0) {
                        boolean bNoramlRecord = MyUtils.getIntFromHex(recordParam.mask[0][0]) == 7;
                        selection = bNoramlRecord ? 2 : 0;
                    }
                    else {
                        selection = getIntRecordMode(recordParam.getRecordMode());
                    }

                    if(selection != position) {
                        int mode = position;
                        recordParam.recordMode = getStringRecordMode(mode == 2 ? 0 : mode);
                        // 如果是联动配置的话，把普通录像去掉
                        for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {
                            recordParam.mask[i][0] = MyUtils.getHexFromInt(mode == 0 ? 6 : 7);
                        }
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, recordParam);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 删除老的配置信息
        mFunDevice.invalidConfig(SimplifyEncode.CONFIG_NAME);
        mFunDevice.invalidConfig(RecordParam.CONFIG_NAME);
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, SimplifyEncode.CONFIG_NAME);
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, RecordParam.CONFIG_NAME, mFunDevice.CurrChannel);
        showDialog(false);

    }

    private void refreshConfig() {
        SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
        if (null != simplifyEncode) {
            // 清晰度
            spinnerVideoQuality.setSelection(6 - simplifyEncode.mainFormat.video.Quality);
            switchAudioEnable.setChecked(simplifyEncode.mainFormat.AudioEnable);
        }

        RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
        if ( null != recordParam ) {
            if(getIntRecordMode(recordParam.getRecordMode()) == 0) {
                boolean bNoramlRecord = MyUtils.getIntFromHex(recordParam.mask[0][0]) == 7;
                spinnerRecordMode.setSelection(bNoramlRecord ? 2 : 0);
            }
            else {
                spinnerRecordMode.setSelection(getIntRecordMode(recordParam.getRecordMode()));
            }

        }
    }

    private String getStringRecordMode(int i) {
        if (i == 1) {
            return "ClosedRecord";
        } else if (i == 2) {
            return "ManualRecord";
        } else {
            return "ConfigRecord";
        }
    }

    private int getIntRecordMode(String s) {
        if (s.equals("ClosedRecord")) {
            return 1;
        } else if (s.equals("ManualRecord")) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
