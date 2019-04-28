package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.lib.funsdk.support.FunDevicePassword;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.AVEncVideoWidget;
import com.bisa.health.camera.lib.funsdk.support.config.CameraParam;
import com.bisa.health.camera.lib.funsdk.support.config.ChannelTitle;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.SDK_TitleDot;
import com.bisa.health.model.User;

public class CameraSettingsBasicActivity extends BaseActivity {
    private LinearLayout viewDevRename;
    private TextView tvDevRename;
    private Switch switchUpDown;
    private Switch switchTimeOSD;
    private Switch switchOSD;
    private Switch switchSaveNativePw;

    private TextView tvOSD;
    private ChannelTitle mSetChannelTitle;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;


    private SharedPersistor sharedPersistor;
    private User mUser;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private boolean isSaveNativePw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_basic);

        viewDevRename = findViewById(R.id.llayout_camera_settings_basic_rename);
        tvDevRename = findViewById(R.id.tv_camera_settings_basic_rename);
        switchUpDown = findViewById(R.id.switch_camera_settings_basic_upDown);
        switchTimeOSD = findViewById(R.id.switch_camera_settings_basic_timeOSD);
        switchOSD = findViewById(R.id.switch_camera_settings_basic_OSD);
        switchSaveNativePw = findViewById(R.id.switch_camera_settings_basic_saveNativePw);
        tvOSD = findViewById(R.id.tv_camera_settings_basic_osd);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switchUpDown.setTextOn("");
            switchUpDown.setTextOff("");
            switchUpDown.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchUpDown.setTrackResource(R.drawable.camera_settings_switch_track_selector);

            switchTimeOSD.setTextOn("");
            switchTimeOSD.setTextOff("");
            switchTimeOSD.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchTimeOSD.setTrackResource(R.drawable.camera_settings_switch_track_selector);

            switchOSD.setTextOn("");
            switchOSD.setTextOff("");
            switchOSD.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchOSD.setTrackResource(R.drawable.camera_settings_switch_track_selector);

            switchSaveNativePw.setTextOn("");
            switchSaveNativePw.setTextOff("");
            switchSaveNativePw.setThumbResource(R.drawable.camera_settings_switch_thumb_selector);
            switchSaveNativePw.setTrackResource(R.drawable.camera_settings_switch_track_selector);
        }

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());

        tvDevRename.setText(mFunDevice.getDevName());

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
                    refreshConfig();

                    if (ChannelTitle.CONFIG_NAME.equals(configName)) {
                        if (null != mSetChannelTitle) {
                            setChannelTitleDot();
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
                    refreshConfig();
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    showToast(FunError.getErrorStr(errCode));
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

        // 删除老的配置信息
        mFunDevice.invalidConfig(CameraParam.CONFIG_NAME);
        mFunDevice.invalidConfig(AVEncVideoWidget.CONFIG_NAME);

        FunSupport.getInstance().requestDeviceConfig(mFunDevice, CameraParam.CONFIG_NAME, mFunDevice.CurrChannel);
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, AVEncVideoWidget.CONFIG_NAME, mFunDevice.CurrChannel);
        showDialog(false);

        sharedPref = getSharedPreferences(String.valueOf(mUser.getUser_guid()), Context.MODE_PRIVATE);
        isSaveNativePw = sharedPref.getBoolean("isSaveNativePw" + mFunDevice.getDevSn(), true);
        editor = sharedPref.edit();

        switchSaveNativePw.setChecked(isSaveNativePw);

        viewDevRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(CameraSettingsBasicActivity.this);
                editText.setText(mFunDevice.getDevName());
                editText.selectAll();
                new AlertDialog.Builder(CameraSettingsBasicActivity.this).setTitle(getString(R.string.camera_settings_basic_rename))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.camera_settings_basic_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvDevRename.setText(editText.getText().toString());
                                mFunDevice.devName = editText.getText().toString();

                                editor.putString(mFunDevice.getDevSn(), mFunDevice.toJson().toString());
                                editor.apply();

                                //同时修改水印
                                if(switchOSD.isChecked()) {
                                    AVEncVideoWidget avEnc = (AVEncVideoWidget)mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
                                    if (avEnc != null) {
                                        avEnc.setChannelTitle(editText.getText().toString());
                                        setOSDName(editText.getText().toString());
                                        if(mSetChannelTitle == null) {
                                            mSetChannelTitle = new ChannelTitle();
                                        }
                                        mSetChannelTitle.setChannelTitle(avEnc.getChannelTitle());
                                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, avEnc);
                                        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, mSetChannelTitle);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.camera_settings_basic_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        switchUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CameraParam cameraParam = (CameraParam)mFunDevice.getConfig(CameraParam.CONFIG_NAME);
                if(cameraParam != null) {
                    if(cameraParam.getPictureFlip() != isChecked) {
                        cameraParam.setPictureFlip(isChecked);
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParam);
                    }
                }
            }
        });
        switchTimeOSD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AVEncVideoWidget avEnc = (AVEncVideoWidget)mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
                // switch of time
                if (avEnc != null) {
                    if(avEnc.timeTitleAttribute.EncodeBlend != isChecked) {
                        avEnc.timeTitleAttribute.EncodeBlend = isChecked;
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, avEnc);
                    }
                }
            }
        });
        switchOSD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AVEncVideoWidget avEnc = (AVEncVideoWidget)mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
                // switch of OSD
                if (avEnc != null) {
                    if(avEnc.channelTitleAttribute.EncodeBlend != isChecked) {
                        avEnc.channelTitleAttribute.EncodeBlend = isChecked;
                        avEnc.setChannelTitle(mFunDevice.getDevName());
                        setOSDName(mFunDevice.getDevName());
                        //设置自定义水印等于设备名称
                        if(mSetChannelTitle == null) {
                            mSetChannelTitle = new ChannelTitle();
                        }
                        mSetChannelTitle.setChannelTitle(avEnc.getChannelTitle());
                        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, avEnc);
                        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, mSetChannelTitle);
                    }
                }
            }
        });
        switchSaveNativePw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isSaveNativePw != isChecked) {
                    isSaveNativePw = isChecked;
                    editor.putBoolean("isSaveNativePw" + mFunDevice.getDevSn(), isSaveNativePw);
                    editor.apply();
                    if(isSaveNativePw == false) {
                        FunDevicePassword.getInstance().removeDevicePassword(mFunDevice.getDevSn());
                    }
                }
            }
        });
    }

    private void refreshConfig() {
        CameraParam cameraParam = (CameraParam)mFunDevice.getConfig(CameraParam.CONFIG_NAME);
        if (null != cameraParam) {
            // 图像上下翻转
            switchUpDown.setChecked(cameraParam.getPictureFlip());
        }

        AVEncVideoWidget avEnc = (AVEncVideoWidget)mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
        if (null != avEnc) {
            // 水印开关
            switchOSD.setChecked(avEnc.channelTitleAttribute.EncodeBlend);
            // 时间开关
            switchTimeOSD.setChecked(avEnc.timeTitleAttribute.EncodeBlend);
        }
    }

    private void setOSDName(String name) {
        tvOSD.setText(name);
        float fontWidth = tvOSD.getPaint().measureText(name);
        int reach = (int) fontWidth % 8;
        if (reach != 0) {
            tvOSD.setWidth((int) (fontWidth + 8 - reach));
        } else {
            tvOSD.setWidth((int) fontWidth);
        }
    }

    private void setChannelTitleDot() {
        byte[] pixels = MyUtils.getPixelsToDevice(tvOSD);
        if (null == pixels) {
            return;
        }
        SDK_TitleDot mTitleDot = new SDK_TitleDot(tvOSD.getWidth(), tvOSD.getHeight());

        G.SetValue(mTitleDot.st_3_pDotBuf, pixels);
        mTitleDot.st_0_width = (short)tvOSD.getWidth();
        mTitleDot.st_1_height = (short)tvOSD.getHeight();
        FunSupport.getInstance().requestDeviceTitleDot(mFunDevice, mTitleDot);
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
