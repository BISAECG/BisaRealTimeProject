package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.GeneralGeneral;
import com.bisa.health.camera.lib.funsdk.support.config.OPStorageManager;
import com.bisa.health.camera.lib.funsdk.support.config.StorageInfo;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.utils.FileUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.List;

public class CameraSettingsStorageActivity extends BaseActivity {
    private TextView tvCapacity;
    private TextView tvRemainingCapacity;
    private RadioGroup radioGroup;
    private RadioButton rbStop;
    private RadioButton rbCycle;
    private Button btnFormatting;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    private OPStorageManager mOPStorageManager;

    private final String[] DEV_CONFIGS = {
            // SD卡存储容量信息
            StorageInfo.CONFIG_NAME,

            // 录像满时停止录像或循环录像
            GeneralGeneral.CONFIG_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_storage);

        tvCapacity = findViewById(R.id.tv_camera_settings_storage_capacity);
        tvRemainingCapacity = findViewById(R.id.tv_camera_settings_storage_remaining_capacity);
        radioGroup = findViewById(R.id.radioGroup_camera_settings_storage);
        rbStop = findViewById(R.id.radioBtn_camera_settings_storage_stop);
        rbCycle = findViewById(R.id.radioBtn_camera_settings_storage_cycle);
        btnFormatting = findViewById(R.id.btn_camera_settings_storage_formatting);

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
                    dialogDismiss();
                    refreshStorageConfig();
                }

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    Toast.makeText(CameraSettingsStorageActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if (funDevice.getId() == mFunDevice.getId()) {
                    if (OPStorageManager.CONFIG_NAME.equals(configName) && null != mOPStorageManager) {
                        // 请求格式化下一个分区
                        if ( !requestFormatPartition(mOPStorageManager.getPartNo() + 1) ) {

                            // 所有分区格式化完成之后,重新获取设备磁盘信息
                            tryGetStorageConfig();
                        }
                        else {
                            dialogDismiss();
                        }
                    } else if ( GeneralGeneral.CONFIG_NAME.equals(configName) ) {
                        // 设置录像满时，选择停止录像或循环录像成功
                        refreshStorageConfig();
                    }
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    Toast.makeText(CameraSettingsStorageActivity.this, FunError.getErrorStr(errCode), Toast.LENGTH_LONG).show();
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

        tryGetStorageConfig();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtn_camera_settings_storage_stop:
                        trySetOverWrite(false);
                        break;
                    case R.id.radioBtn_camera_settings_storage_cycle:
                        trySetOverWrite(true);
                        break;
                }
            }
        });

        btnFormatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CameraSettingsStorageActivity.this)
                        .setMessage(getString(R.string.camera_settings_storage_formatting_tips))
                        .setPositiveButton(getString(R.string.camera_settings_basic_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (requestFormatPartition(0)) {
                                    showDialog(false);
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.camera_settings_basic_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }

    private void trySetOverWrite(boolean overWrite) {
        GeneralGeneral generalInfo = (GeneralGeneral)mFunDevice.getConfig(GeneralGeneral.CONFIG_NAME);
        if ( null != generalInfo ) {
            if (overWrite && generalInfo.getOverWrite() == GeneralGeneral.OverWriteType.StopRecord) {
                //录像满时，循环录像
                generalInfo.setOverWrite(GeneralGeneral.OverWriteType.OverWrite);
                FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, generalInfo);
            }
            else if(!overWrite && generalInfo.getOverWrite() == GeneralGeneral.OverWriteType.OverWrite) {
                //录像满时，停止录像
                generalInfo.setOverWrite(GeneralGeneral.OverWriteType.StopRecord);
                FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, generalInfo);
            }
        }
    }

    private void refreshStorageConfig() {
        StorageInfo storageInfo = (StorageInfo)mFunDevice.getConfig(StorageInfo.CONFIG_NAME);
        if ( null != storageInfo ) {
            int totalSpace = 0;
            int remainSpace = 0;
            List<StorageInfo.Partition> partitions = storageInfo.getPartitions();
            for ( StorageInfo.Partition partition : partitions ) {
                if ( partition.IsCurrent ) {
                    // 获取当前分区的大小
                    int partTotalSpace = MyUtils.getIntFromHex(partition.TotalSpace);
                    int partRemainSpace = MyUtils.getIntFromHex(partition.RemainSpace);

                    // 累加总大小
                    totalSpace += partTotalSpace;
                    remainSpace += partRemainSpace;
                }
            }

            tvCapacity.setText(FileUtils.FormetFileSize(totalSpace, 2));
            tvRemainingCapacity.setText(FileUtils.FormetFileSize(remainSpace, 2));
        }

        GeneralGeneral generalInfo = (GeneralGeneral)mFunDevice.getConfig(GeneralGeneral.CONFIG_NAME);
        if ( null != generalInfo ) {
            if(generalInfo.getOverWrite() == GeneralGeneral.OverWriteType.OverWrite) {
                rbCycle.setChecked(true);
                //mRbRecordCycle.setChecked(true);
            }
            else {
                rbStop.setChecked(true);
                //mRbRecordStop.setChecked(true);
            }
        }
    }

    /**
     * 请求格式化指定的分区
     * @param iPartition
     * @return
     */
    private boolean requestFormatPartition(int iPartition) {
        StorageInfo storageInfo = (StorageInfo)mFunDevice.getConfig(StorageInfo.CONFIG_NAME);
        if ( null != storageInfo && iPartition < storageInfo.PartNumber ) {
            if ( null == mOPStorageManager ) {
                mOPStorageManager = new OPStorageManager();
                mOPStorageManager.setAction("Clear");
                mOPStorageManager.setSerialNo(0);
                mOPStorageManager.setType("Data");
            }

            mOPStorageManager.setPartNo(iPartition);

            return FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, mOPStorageManager);
        }
        return false;
    }

    private void tryGetStorageConfig() {
        showDialog(false);
        for ( String configName : DEV_CONFIGS ) {

            // 删除老的配置信息
            mFunDevice.invalidConfig(configName);

            // 重新搜索新的配置信息
            FunSupport.getInstance().requestDeviceConfig(mFunDevice, configName);
        }
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
