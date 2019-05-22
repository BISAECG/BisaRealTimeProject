package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.interfaces.CameraUpgradeListener;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.OPTimeQuery;
import com.bisa.health.camera.lib.funsdk.support.config.OPTimeSetting;
import com.bisa.health.camera.lib.funsdk.support.config.StatusNetInfo;
import com.bisa.health.camera.lib.funsdk.support.config.SystemInfo;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.widget.TimeTextView;
import com.bisa.health.camera.lib.sdk.bean.DefaultConfigBean;
import com.bisa.health.camera.lib.sdk.bean.HandleConfigData;
import com.bisa.health.camera.lib.sdk.bean.JsonConfig;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.sdk.UIFactory;
import com.lib.ECONFIG;
import com.lib.EDEV_JSON_ID;
import com.lib.FunSDK;
import com.lib.MsgContent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CameraSettingsCommonActivity extends BaseActivity {
    private ImageView ivDevSNCode;

    private TextView tvDevSn;
    private TextView tvDevVer;
    private TextView tvDevSWVer;
    private TextView tvDevPubDate;
    private TextView tvDevTime;
    private TextView tvDevNetModel;
    private TextView tvDevUpdate;

    private Button btnFactoryReset;
    private Button btnReboot;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;
    private CameraUpgradeListener cameraUpgradeListener;

    private DefaultConfigBean mdefault;

    private int checkUpgrade = -1;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_common);

        ivDevSNCode = findViewById(R.id.iv_camera_settings_common_qrCode);

        tvDevSn = findViewById(R.id.tv_camera_settings_common_sn);
        tvDevVer = findViewById(R.id.tv_camera_settings_common_devVer);
        tvDevSWVer = findViewById(R.id.tv_camera_settings_common_swVer);
        tvDevPubDate = findViewById(R.id.tv_camera_settings_common_pubDate);
        tvDevTime = findViewById(R.id.timeTextView_camera_settings_common_devTime);
        tvDevTime.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvDevTime.getPaint().setAntiAlias(true);
        tvDevNetModel = findViewById(R.id.tv_camera_settings_common_netModel);
        tvDevUpdate = findViewById(R.id.tv_camera_settings_common_devUpdate);
        btnFactoryReset = findViewById(R.id.btn_camera_settings_common_factoryReset);
        btnReboot = findViewById(R.id.btn_camera_settings_common_reboot);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mdefault = new DefaultConfigBean();

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
                if (funDevice.getId() == mFunDevice.getId()
                        && (SystemInfo.CONFIG_NAME.equals(configName)
                        || StatusNetInfo.CONFIG_NAME.equals(configName)
                        || OPTimeQuery.CONFIG_NAME.equals(configName))) {
                    dialogDismiss();
                    refreshSystemInfo();
                }
            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    //Toast.makeText(CameraSettingsCommonActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if(mFunDevice.getId() == funDevice.getId() && OPTimeSetting.CONFIG_NAME.equals(configName)) {
                    dialogDismiss();
                    // 重新获取时间
                    FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, new OPTimeQuery());
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    Toast.makeText(CameraSettingsCommonActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
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

        cameraUpgradeListener = new CameraUpgradeListener() {
            @Override
            public void devCheckUpgrade(Message msg, MsgContent msgContent) {
                if (msg.arg1 < 0) {
                    tvDevUpdate.setText(R.string.camera_devcheckupdate_failed);
                    tvDevUpdate.setEnabled(false);
                    return;
                }
                checkUpgrade = msg.arg1;
                if (checkUpgrade == 0) {
                    tvDevUpdate.setText(R.string.camera_devcheckupdate_versionlast);
                } else {
                    tvDevUpdate.setText(R.string.camera_devcheckupdate_clickupdate);
                    findViewById(R.id.llayout_camera_device_update).setClickable(true);
                }
            }

            @Override
            public void devStartUpgrade(Message msg, MsgContent msgContent) {
                if (msg.arg1 < 0) {
                    Toast.makeText(CameraSettingsCommonActivity.this, "Failed to Update!!", Toast.LENGTH_LONG).show();
                }
                isUpdating = true;
            }

            @Override
            public void devStopUpgrade() {
                isUpdating = false;
            }

            @Override
            public void devOnUpgradeProgress(Message msg, MsgContent msgContent) {
                switch (msg.arg1) {
                    case ECONFIG.EUPGRADE_STEP_DOWN:
                        if (msg.arg2 < 0 || msg.arg2 > 100) {
                            //Log.e("devicedewnload", "Error");
                        } else {
                            tvDevUpdate.setText("Downloading...." + Integer.toString(msg.arg2) + "%");
                        }
                        /* 正在下载升级包 */
                        break;
                    case ECONFIG.EUPGRADE_STEP_UP:
                        if (msg.arg2 < 0 || msg.arg2 > 100) {
                            //Log.e("deviceupdoad", "Error");
                        } else {
                            tvDevUpdate.setText("UpLoading...." + Integer.toString(msg.arg2) + "%");
                        }
                        break;
                    case ECONFIG.EUPGRADE_STEP_UPGRADE:
                        if (msg.arg2 < 0 || msg.arg2 > 100) {
                            //Log.e("deviceupdate", "Error");
                        } else {
                            tvDevUpdate.setText("Updating...." + Integer.toString(msg.arg2) + "%");
                        }
                        /* 正在升级 */
                        break;
                    case ECONFIG.EUPGRADE_STEP_COMPELETE:
                        //System.out.println("complete");
                        if (msg.arg2 < 0) {
                            //System.out.println("complete1");
                            //Log.e("deviceupdatecomplete", "Error");
                            break;
                        }
                        tvDevUpdate.setText(R.string.camera_devcheckupdate_versionlast);
                        checkUpgrade = 0;
                        // getString(FunSDK.TS("complete)
                        /* 升级完成 */
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void devSetJson(Message msg, MsgContent msgContent) {
                if (msg.arg1 < 0) {
                    Toast.makeText(CameraSettingsCommonActivity.this, FunError.getErrorStr(msg.arg1), Toast.LENGTH_LONG).show();
                }else {
                    if (msgContent.str.equals(JsonConfig.OPERATION_DEFAULT_CONFIG)) {
                        JSONObject object = new JSONObject();
                        object.put("Action", "Reboot");
                        FunSDK.DevCmdGeneral(FunSupport.getInstance().getHandler(), mFunDevice.getDevSn(), EDEV_JSON_ID.OPMACHINE, JsonConfig.OPERATION_MACHINE, 1024, 5000,
                                HandleConfigData.getSendData(JsonConfig.OPERATION_MACHINE, "0x1", object).getBytes(), -1, 0);
                        Toast.makeText(CameraSettingsCommonActivity.this, R.string.camera_settings_factoryReset_success, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        btnFactoryReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CameraSettingsCommonActivity.this)
                        .setTitle(R.string.camera_settings_common_factoryReset)
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mdefault.setAllConfig(1);
                                FunSDK.DevSetConfigByJson(FunSupport.getInstance().getHandler(), mFunDevice.getDevSn(), JsonConfig.OPERATION_DEFAULT_CONFIG, HandleConfigData.getSendData(JsonConfig.OPERATION_DEFAULT_CONFIG, "0x1", mdefault), -1 , 20000, mFunDevice.getId());
                            }
                        })
                        .setNeutralButton(R.string.common_cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });
        btnReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CameraSettingsCommonActivity.this)
                        .setTitle(R.string.camera_settings_common_reboot)
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject object = new JSONObject();
                                object.put("Action", "Reboot");
                                FunSDK.DevCmdGeneral(FunSupport.getInstance().getHandler(), mFunDevice.getDevSn(), EDEV_JSON_ID.OPMACHINE, JsonConfig.OPERATION_MACHINE, 1024, 5000,
                                        HandleConfigData.getSendData(JsonConfig.OPERATION_MACHINE, "0x1", object).getBytes(), -1, 0);
                                Toast.makeText(CameraSettingsCommonActivity.this, R.string.camera_settings_common_rebooting, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);
        FunSupport.getInstance().registerCameraUpgradeListener(cameraUpgradeListener);

        requestSystemInfo();

        FunSupport.getInstance().requestDeviceUpdateCheck(mFunDevice.getDevSn());

    };

    private void requestSystemInfo() {
        showDialog(false);
        mFunDevice.invalidConfig(SystemInfo.CONFIG_NAME);
        // 获取系统信息
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);

        mFunDevice.invalidConfig(StatusNetInfo.CONFIG_NAME);
        // 获取NAT状态
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, StatusNetInfo.CONFIG_NAME);

        // 获取时间
        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, new OPTimeQuery());
    }

    private void refreshSystemInfo() {
        if ( null != mFunDevice ) {
            SystemInfo systemInfo = (SystemInfo)mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
            if ( null != systemInfo ) {
                // 序列号
                tvDevSn.setText(systemInfo.getSerialNo());

                // 设备型号
                tvDevVer.setText(systemInfo.getHardware());

                // 软件版本号
                tvDevSWVer.setText(systemInfo.getSoftwareVersion());

                // 发布时间
                tvDevPubDate.setText(systemInfo.getBuildTime());

                // 设备运行时间
                tvDevTime.setText(systemInfo.getDeviceRunTimeWithFormat());

                // 设备连接方式
                tvDevNetModel.setText(getConnectTypeStringId(mFunDevice.getNetConnectType()));

                // 生成二维码
                Bitmap qrCodeBmp = UIFactory.createCode(systemInfo.getSerialNo(), 600, 0xff202020);
                if ( null != qrCodeBmp ) {
                    ivDevSNCode.setImageBitmap(qrCodeBmp);
                }
            }

            StatusNetInfo netInfo = (StatusNetInfo)mFunDevice.getConfig(StatusNetInfo.CONFIG_NAME);
            if ( null != netInfo ) {
                tvDevNetModel.setText(netInfo.getNatStatus());
            }

            OPTimeQuery showDevtimeQuery = (OPTimeQuery)mFunDevice.getConfig(OPTimeQuery.CONFIG_NAME);
            if (null != showDevtimeQuery) {
                String mOPTimeQuery = showDevtimeQuery.getOPTimeQuery();
                tvDevTime.setText(mOPTimeQuery);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date;
                try {
                    date = sdf.parse(mOPTimeQuery);
                    ((TimeTextView) tvDevTime).setDevSysTime(date.getTime());
                    ((TimeTextView) tvDevTime).onStartTimer();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onUpdate(View v) {

        if (!isUpdating) {
            //FunSDK.DevStartUpgrade(mHandler, mFunDevice.devSn, checkUpgrade, 0);
            FunSupport.getInstance().requestDevStartUpgrade(mFunDevice.getDevSn(), checkUpgrade);
        }else {
            //FunSDK.DevStopUpgrade(mHandler, mFunDevice.devSn, 0);
            FunSupport.getInstance().requestDevStopUpgrade(mFunDevice.getDevSn());
        }
    }

    // 0: p2p 1:转发 2IP直连
    private int getConnectTypeStringId(int netConnectType) {
        if ( netConnectType == 0 ) {
            return R.string.camera_net_connect_type_p2p;
        } else if ( netConnectType == 1 ) {
            return R.string.camera_net_connect_type_transmit;
        } else if ( netConnectType == 2 ) {
            return R.string.camera_net_connect_type_ip;
        } else if ( netConnectType == 5) {
            return R.string.camera_net_connect_type_rps;
        }

        return R.string.camera_net_connect_type_unknown;
    }

    public void syncTime(View v){
        new AlertDialog.Builder(this)
                .setTitle(R.string.camera_settings_common_syncDevTime)
                .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog(false);
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        String sysTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()).format(cal.getTime());
                        syncDevTime(sysTime);
                        syncDevZone(cal);
                    }
                })
                .setNeutralButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    //同步设备时间（这个时间同步 设备端如果开启了NTP服务器同步的话，
    // 这个设置是不起作通的，因为设备会到服务器那边同步时间，
    // 所以这个时候只需要同步时区就可以了）
    private void syncDevTime(String sysTime) {
        OPTimeSetting devtimeInfo = (OPTimeSetting)mFunDevice.checkConfig(OPTimeSetting.CONFIG_NAME);
        devtimeInfo.setmSysTime(sysTime);

        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, devtimeInfo);
    }

    //同步设备时区
    private void syncDevZone(Calendar calendar) {
        if (calendar == null) {
            return;
        }
        float zoneOffset = (float) calendar.get(java.util.Calendar.ZONE_OFFSET);
        float zone = (float) (zoneOffset / 60.0 / 60.0 / 1000.0);// 时区，东时区数字为正，西时区为负
        FunSupport.getInstance().requestSyncDevZone(mFunDevice, (int) (-zone * 60));
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        FunSupport.getInstance().removeCameraUpgradeListener(cameraUpgradeListener);
        super.onDestroy();
    }
}
