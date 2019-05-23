package com.bisa.health.camera;

import android.app.Activity;
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
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunFileData;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class CameraFilesViewPicActivity extends BaseActivity {
    private PhotoView photoView;
    private Button btnDownload;

    private FunDevice mFunDevice;
    private FunFileData mImageInfo;

    private OnFunDeviceFileListener onFunDeviceFileListener1;

    private int mPosition;

    private String fileDir;
    private String path = FunPath.getTempPicPath();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files_view_pic);

        photoView = findViewById(R.id.photo_view);
        btnDownload = findViewById(R.id.btn_camera_files_viewPic_download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadToPhone();
            }
        });

        mPosition = getIntent().getIntExtra("position", 0);
        fileDir = getIntent().getStringExtra("fileDir");

        mFunDevice = FunSupport.getInstance().mCurrDevice;

        onFunDeviceFileListener1 = new OnFunDeviceFileListener() {
            @Override
            public void onDeviceFileDownCompleted(FunDevice funDevice, String path, int nSeq) {
                if(funDevice.getId() == mFunDevice.getId()) {
                    dialogDismiss();

                    File imageFile = new File(path);
                    Uri uri = Uri.fromFile(imageFile);
                    photoView.setImageURI(uri);
                }
            }

            @Override
            public void onDeviceFileDownProgress(int totalSize, int progress, int nSeq) {

            }

            @Override
            public void onDeviceFileDownStart(boolean isStartSuccess, int nSeq) {

            }
        };

        FunSupport.getInstance().registerOnFunDeviceFileListener(onFunDeviceFileListener1);

        tryToLoadPic();
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceFileListener(onFunDeviceFileListener1);
        super.onDestroy();
    }

    private void tryToLoadPic(){

        mImageInfo = mFunDevice.mDatas.get(mPosition);

        byte[] data;
        if (mImageInfo != null) {
            data = G.ObjToBytes(mImageInfo.getFileData());
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

        showDialog(true);
        FunSupport.getInstance().requestDeviceDownloadByFile(mFunDevice, data, path, mPosition);
    }

    private void downloadToPhone() {
        String fileName = mImageInfo.getBeginDateStr() + "_" + mImageInfo.getBeginTimeStr() + ".jpg";
        String newPath = fileDir + fileName;
        if (mImageInfo != null && FunPath.isFileExists(path) > 0) {
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
}
