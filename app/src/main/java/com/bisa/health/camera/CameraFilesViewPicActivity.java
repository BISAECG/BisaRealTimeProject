package com.bisa.health.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.basic.G;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceFileListener;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.bisa.health.cust.view.ActionBarView;
import com.github.chrisbanes.photoview.PhotoView;
import com.lib.SDKCONST;

import java.io.File;
import java.util.Calendar;


public class CameraFilesViewPicActivity extends BaseActivity {
    private ActionBarView actionBarView;
    private PhotoView photoView;
    private Button btnDownload;

    private FunDevice mFunDevice;
    private H264_DVR_FILE_DATA fileData;

    private OnFunDeviceFileListener onFunDeviceFileListener;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    private int mPosition;

    private String title;

    private String fileDir;
    private String path = FunPath.getTempPicPath();

    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files_view_pic);

        actionBarView = findViewById(R.id.abar_title);
        photoView = findViewById(R.id.photo_view);
        btnDownload = findViewById(R.id.btn_camera_files_viewPic_download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadToPhone();
            }
        });

        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        fileDir = intent.getStringExtra("fileDir");
        calendar.setTimeInMillis(intent.getLongExtra("calendar", 0));
        title = intent.getStringExtra("title");

        actionBarView.setTvTitle(title);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        onFunDeviceFileListener = new OnFunDeviceFileListener() {
            @Override
            public void onDeviceFileDownCompleted(FunDevice funDevice, String path, int nSeq) {
                if(funDevice.getId() == mFunDevice.getId()) {
                    dialogDismiss();

                    File imageFile = new File(path);
                    Uri uri = Uri.fromFile(imageFile);
                    photoView.setImageURI(uri);
                    //Bitmap bitmap = BitmapFactory.decodeFile(path);
                    //photoView.setImageBitmap(bitmap);

                }
            }

            @Override
            public void onDeviceFileDownProgress(int totalSize, int progress, int nSeq) {

            }

            @Override
            public void onDeviceFileDownStart(boolean isStartSuccess, int nSeq) {

            }
        };
        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

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
                if(funDevice.getId() == mFunDevice.getId()) {
                    fileData = datas[mPosition];
                    tryToLoadPic();
                }
            }

            @Override
            public void onDeviceFileListGetFailed(FunDevice funDevice) {
                if(funDevice.getId() == mFunDevice.getId()) {
                    dialogDismiss();
                }
            }
        };


        FunSupport.getInstance().registerOnFunDeviceFileListener(onFunDeviceFileListener);
        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);
        onSearchPicture();
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceFileListener(onFunDeviceFileListener);
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }


    private void tryToLoadPic(){

        //mImageInfo = mFunDevice.mDatas.get(mPosition);

        byte[] data;
        if (fileData != null) {
            data = G.ObjToBytes(fileData);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //if (FunPath.isFileExists(path) >= 0) {
            //FunPath.deleteFile(path);
        //}
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }

        FunSupport.getInstance().requestDeviceDownloadByFile(mFunDevice, data, path, mPosition);

    }

    private void downloadToPhone() {
        //String fileName = mImageInfo.getBeginDateStr() + "_" + mImageInfo.getBeginTimeStr() + ".jpg";
        String fileName = fileData.st_3_beginTime.getDate().getTime() + ".jpg";
        String newPath = fileDir + fileName;
        if (FunPath.isFileExists(path) > 0) {
            File oldFile = new File(path);
            File newFile = new File(newPath);
            if (oldFile.renameTo(newFile)) {
                //String str = getString(R.string.device_sport_camera_download_success);
                showToast(getString(R.string.download_complete));
            }
        } else {
            if (FunPath.isFileExists(newPath) > 0) {
                //showToast(R.string.device_sport_camera_pic_existed);
                showToast(getString(R.string.common_file_exist));
            } else{
                //showToast(R.string.device_sport_camera_load_first);
                showToast(getString(R.string.camera_file_load_first));
            }
        }
    }


    private void onSearchPicture() {
        showDialog(true);

        H264_DVR_FINDINFO findInfo = new H264_DVR_FINDINFO();
        findInfo.st_1_nFileType = SDKCONST.SDK_File_Type.SDK_PIC_ALL;
        initSearchInfo(findInfo, mFunDevice.CurrChannel);
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, findInfo);
    }

    private void initSearchInfo(H264_DVR_FINDINFO info, int channel) {

        info.st_2_startTime.st_0_dwYear = calendar.get(Calendar.YEAR);
        info.st_2_startTime.st_1_dwMonth = calendar.get(Calendar.MONTH) + 1;
        info.st_2_startTime.st_2_dwDay = calendar.get(Calendar.DATE);

        info.st_2_startTime.st_3_dwHour = 0;
        info.st_2_startTime.st_4_dwMinute = 0;
        info.st_2_startTime.st_5_dwSecond = 0;

        info.st_3_endTime.st_0_dwYear = calendar.get(Calendar.YEAR);
        info.st_3_endTime.st_1_dwMonth = calendar.get(Calendar.MONTH) + 1;
        info.st_3_endTime.st_2_dwDay = calendar.get(Calendar.DATE);
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = channel;
    }

}
