package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.G;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.adapter.CameraFilesCameraRecordLvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceFileListener;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.widget.RecordFunVideoView;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.bisa.health.cust.view.ActionBarView;
import com.lib.SDKCONST;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFilesViewRecordActivity extends BaseActivity {
    private FunDevice mFunDevice;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private ActionBarView actionBarView;
    private RecordFunVideoView mFunVideoView;
    private RelativeLayout rlayoutProgress;
    private TextView tvCurrTime;
    private TextView tvDuration;
    private SeekBar seekBar;
    private ListView lvRecords;
    private CameraFilesCameraRecordLvAdapter recordAdapter;

    private OnFunDeviceOptListener onFunDeviceOptListener;
    private OnFunDeviceFileListener onFunDeviceFileListener;


    private final int MESSAGE_REFRESH_PROGRESS = 0x100;
    private final int MESSAGE_SEEK_PROGRESS = 0x101;
    private final int MESSAGE_SET_IMAGE = 0x102;

    private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private int maxProgress = 100;

    private String fileDir;

    private String[] items;

    private boolean isFirstTime = true;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MESSAGE_REFRESH_PROGRESS:
                {
                    refreshProgress();
                    resetProgressInterval();
                }
                break;
                case MESSAGE_SEEK_PROGRESS:
                {
                    seekRecordVideo(msg.arg1);
                }
                break;
                case MESSAGE_SET_IMAGE:
                {
                    if (recordAdapter != null) {
                        recordAdapter.setBitmapTempPath((String) msg.obj);
                    }
                }
                break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files_view_record);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        actionBarView = findViewById(R.id.abar_title);

        calendar.setTimeInMillis(getIntent().getLongExtra("calendar", 0));
        actionBarView.setTvTitle(sdf.format(calendar.getTime()));

        mFunVideoView = findViewById(R.id.funRecVideoView);
        mFunVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dialogDismiss();
                if(isFirstTime) {
                    mFunVideoView.stopPlayback();
                    isFirstTime = false;
                }
                else {
                    refreshPlayInfo();
                    String path = mFunVideoView.captureImage(null);
                    Message message = new Message();
                    message.what = MESSAGE_SET_IMAGE;
                    message.obj = path;
                    mHandler.sendMessageDelayed(message, 200);

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

            }
        });
        mFunVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                dialogDismiss();
                showToast(getResources().getString(R.string.media_play_error) + " : " + FunError.getErrorStr(extra));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                return true;
            }
        });
        mFunVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        rlayoutProgress = findViewById(R.id.rlayout_camera_files_view_record_videoProgressArea);

        tvCurrTime = findViewById(R.id.tv_camera_files_view_record_currTime);
        tvDuration = findViewById(R.id.tv_camera_files_view_record_durationTime);
        seekBar = findViewById(R.id.seekbar_camera_files_view_record);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (null != mHandler) {
                        mHandler.removeMessages(MESSAGE_SEEK_PROGRESS);
                        Message msg = Message.obtain();
                        msg.what = MESSAGE_SEEK_PROGRESS;
                        msg.arg1 = progress;
                        mHandler.sendMessageDelayed(msg, 300);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fileDir = getIntent().getStringExtra("fileDir");
        items = new String[]{getString(R.string.common_download)};

        lvRecords = findViewById(R.id.lv_camera_files_camera_records);
        lvRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playRecordVideoByFile(recordAdapter.getItem(position));
                recordAdapter.setPlayingIndex(position);
            }
        });
        lvRecords.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(CameraFilesViewRecordActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    H264_DVR_FILE_DATA recordFile = recordAdapter.getItem(position);
                                    if (recordFile != null) {
                                        byte[] data = G.ObjToBytes(recordFile);
                                        String path = fileDir + recordFile.st_3_beginTime.getDate().getTime() + ".mp4";
                                        File file = new File(path);
                                        if (file.exists()) {
                                            Toast.makeText(CameraFilesViewRecordActivity.this, getString(R.string.common_file_exist), Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            FunSupport.getInstance().requestDeviceDownloadByFile(mFunDevice, data, path, position);
                                        }
                                    }

                                }
                            }
                        })
                        .show();
                return true;
            }
        });


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
                if (funDevice.getId() == mFunDevice.getId()) {

                    List<H264_DVR_FILE_DATA> files = new ArrayList<>();

                    Collections.addAll(files, datas);

                    recordAdapter = new CameraFilesCameraRecordLvAdapter(CameraFilesViewRecordActivity.this, files);
                    lvRecords.setAdapter(recordAdapter);

                    playRecordVideoByFile(files.get(0));

                    //recordAdapter.setPlayingIndex(0);

                }
            }

            @Override
            public void onDeviceFileListGetFailed(FunDevice funDevice) {
                if (funDevice.getId() == mFunDevice.getId()) {
                    dialogDismiss();
                }
            }
        };

        onFunDeviceFileListener = new OnFunDeviceFileListener() {
            @Override
            public void onDeviceFileDownCompleted(FunDevice funDevice, String path, int nSeq) {
                dialogDismiss();
                Toast.makeText(CameraFilesViewRecordActivity.this, getString(R.string.download_complete), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeviceFileDownProgress(int totalSize, int progress, int nSeq) {

            }

            @Override
            public void onDeviceFileDownStart(boolean isStartSuccess, int nSeq) {
                if (isStartSuccess){
                    Toast.makeText(CameraFilesViewRecordActivity.this, getString(R.string.download_start), Toast.LENGTH_LONG).show();
                    showDialog(true);
                }else{
                    Toast.makeText(CameraFilesViewRecordActivity.this, getString(R.string.download_faile), Toast.LENGTH_LONG).show();
                }
            }
        };

        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);
        FunSupport.getInstance().registerOnFunDeviceFileListener(onFunDeviceFileListener);

        onSearchFile();
    }

    @Override
    protected void onDestroy() {
        // 停止视频播放
        mFunVideoView.stopPlayback();

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        FunSupport.getInstance().removeOnFunDeviceFileListener(onFunDeviceFileListener);
        super.onDestroy();
    }

    private void refreshPlayInfo() {

        int startTm = mFunVideoView.getStartTime();
        int endTm = mFunVideoView.getEndTime();
        maxProgress = endTm - startTm;

        if (startTm > 0 && endTm > startTm) {
            tvCurrTime.setText(sdf1.format(new Date((long) startTm * 1000)));
            tvDuration.setText(sdf1.format(new Date((long) endTm * 1000)));
            seekBar.setMax(endTm - startTm);
            seekBar.setProgress(0);

            //rlayoutProgress.setVisibility(View.VISIBLE);
            resetProgressInterval();
        } else {
            //rlayoutProgress.setVisibility(View.GONE);
            cleanProgressInterval();
        }
    }
    private void refreshProgress() {
        int posTm = mFunVideoView.getPosition();
        if ( posTm > 0 ) {
            tvCurrTime.setText(sdf1.format(new Date((long) posTm*1000)));

            seekBar.setProgress(posTm - mFunVideoView.getStartTime());
        }
    }
    private void resetProgressInterval() {
        if ( null != mHandler ) {
            mHandler.removeMessages(MESSAGE_REFRESH_PROGRESS);
            mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_PROGRESS, 500);
        }
    }
    private void cleanProgressInterval() {
        if ( null != mHandler ) {
            mHandler.removeMessages(MESSAGE_REFRESH_PROGRESS);
        }
    }
    private void seekRecordVideo(int progress) {
        //int seekPos = mFunVideoView.getStartTime() + progress;
        int seekposbyfile = (progress*100)/maxProgress;
        mFunVideoView.seekbyfile(seekposbyfile);
    }

    private void onSearchFile() {
        showDialog(false);

        int[] time = { calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE) };

        H264_DVR_FINDINFO info = new H264_DVR_FINDINFO();
        info.st_1_nFileType = SDKCONST.FileType.SDK_RECORD_ALL;
        info.st_2_startTime.st_0_dwYear = time[0];
        info.st_2_startTime.st_1_dwMonth = time[1];
        info.st_2_startTime.st_2_dwDay = time[2];
        info.st_2_startTime.st_3_dwHour = 0;
        info.st_2_startTime.st_4_dwMinute = 0;
        info.st_2_startTime.st_5_dwSecond = 0;
        info.st_3_endTime.st_0_dwYear = time[0];
        info.st_3_endTime.st_1_dwMonth = time[1];
        info.st_3_endTime.st_2_dwDay = time[2];
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = mFunDevice.CurrChannel;
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, info);
    }

    private void playRecordVideoByFile(H264_DVR_FILE_DATA data) {

        showDialog(true);

        mFunVideoView.stopPlayback();

        mFunVideoView.playRecordByFile(mFunDevice.getDevSn(), data, mFunDevice.CurrChannel);
        mFunVideoView.setMediaSound(true);
    }
}
