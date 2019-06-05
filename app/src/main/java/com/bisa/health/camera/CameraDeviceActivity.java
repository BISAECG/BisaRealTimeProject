package com.bisa.health.camera;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.BottomSheetDialog;


import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;


import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.adapter.CameraPlaybackDateRvAdapter;
import com.bisa.health.camera.interfaces.OnItemClickListener;
import com.bisa.health.camera.lib.funsdk.support.FunError;

import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceCaptureListener;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceRecordListener;
import com.bisa.health.camera.lib.funsdk.support.config.DevCmdOPSCalendar;
import com.bisa.health.camera.lib.funsdk.support.config.OPPTZPreset;
import com.bisa.health.camera.lib.funsdk.support.config.SystemInfo;

import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunStreamType;
import com.bisa.health.camera.lib.funsdk.support.utils.FileUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.TalkManager;
import com.bisa.health.camera.lib.funsdk.support.widget.FunVideoView;
import com.bisa.health.camera.lib.funsdk.support.widget.PreviewFunVideoView;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.bisa.health.camera.sdk.DialogInputPasswd;
import com.bisa.health.camera.sdk.UIFactory;
import com.bisa.health.camera.view.OnValueChangeListener;
import com.bisa.health.camera.view.PlaybackDaylongView;
import com.bisa.health.model.User;
import com.lib.EPTZCMD;
import com.lib.FunSDK;
import com.lib.SDKCONST;


import java.io.File;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@SuppressLint("ClickableViewAccessibility")
public class CameraDeviceActivity extends BaseActivity implements
        OnFunDeviceOptListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener {

    private ImageButton mBtnBack;

    private FunDevice mFunDevice;

    private RelativeLayout rLayoutTop;

    private RelativeLayout rLayoutVideoWnd;
    private FunVideoView mFunVideoView;

    private int funStreamTypeId;

    private SharedPreferences sharedPref;

    private LinearLayout lLayoutRecording;
    private Chronometer chronometerRecordTime;

    private LinearLayout llCtr;

    private ImageButton mIbtnPlay;
    private ImageButton mIbtnMute;
    private ImageButton mIbtnChannels;
    private ImageButton mIbtnFluency;
    private ImageButton mIbtnFullscreen;

    private BottomSheetDialog fluencyBottomDialog;
    private Button btnHD;
    private Button btnSD;


    private RelativeLayout mLayoutDirectionControl;
    private ImageButton mPtz_up;
    private ImageButton mPtz_down;
    private ImageButton mPtz_left;
    private ImageButton mPtz_right;

    private Button mBtnCapture;
    private Button mBtnVoice;
    private Button mBtnRecord;
    private Button mBtnFiles;

    private TextView mTextVideoStat;

    private GridLayout gLayoutPreview;
    private PreviewFunVideoView previewFunVideoView1, previewFunVideoView2, previewFunVideoView3, previewFunVideoView4;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private List<PreviewFunVideoView> previewFunVideoViewList = new ArrayList<>();
    private List<ProgressBar> progressBarList = new ArrayList<>();
    private List<FunDevice> previewDevs;
    private int devGroup;
    private long doubleClickMillis = 0;
    private PreviewFunVideoView currPreviewFunVideoView;

    private ImageButton iBtnPlayback;
    private LinearLayout lLayoutPlayback;
    private RecyclerView rvPlaybackDate;
    private PlaybackDaylongView playbackDaylongView;
    private ImageButton iBtnPlaybackLeft;
    private ImageButton iBtnPlaybackRight;

    private int mChannelCount;
    private boolean isGetSysFirst = true;


    private final int MESSAGE_PLAY_MEDIA = 0x100;
    private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
    private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
    private final int MESSAGE_OPEN_VOICE = 0x104;


    private TalkManager mTalkManager;

    private boolean mCanToPlay = false;

    public String NativeLoginPsw; //本地密码

    private SharedPersistor sharedPersistor;
    private User mUser;
    private String fileDir;

    private boolean isSaveNativePw;


    private OnFunDeviceCaptureListener funDeviceCaptureListener;
    private OnFunDeviceRecordListener funDeviceRecordListener;

    private List<String> recordDateList = new ArrayList<String>();
    private CameraPlaybackDateRvAdapter recordDateAdapter;
    private H264_DVR_FILE_DATA[] recordDatas;
    private int recordDatasIndex = 0;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_device);

        mFunDevice = FunSupport.getInstance().mCurrDevice;

        if (null == mFunDevice) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        sharedPref = getSharedPreferences(String.valueOf(mUser.getUser_guid()), Context.MODE_PRIVATE);
        isSaveNativePw = sharedPref.getBoolean("isSaveNativePw" + mFunDevice.getDevSn(), true);
        funStreamTypeId = sharedPref.getInt("funStreamTypeId" + mFunDevice.getDevSn(), FunStreamType.STREAM_MAIN.getTypeId());

        rLayoutTop = findViewById(R.id.rlayout_camera_device_top);

        mBtnBack = findViewById(R.id.ibtn_camera_device_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rLayoutVideoWnd = findViewById(R.id.rlayoutPlayWnd);

        mFunVideoView = findViewById(R.id.funVideoView);

        mFunVideoView.setStreamType(FunStreamType.getStramType(funStreamTypeId));

        mFunVideoView.setOnPreparedListener(this);
        mFunVideoView.setOnErrorListener(this);
        mFunVideoView.setOnInfoListener(this);
        mFunVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mBtnRecord.isSelected()) {
                    mBtnRecord.callOnClick();
                }
                recordDatasIndex ++;
                if(recordDatasIndex < recordDatas.length) {
                    playbackDaylongView.setRecordScale(recordDatasIndex);
                    playRecordVideoByFile(recordDatasIndex);
                }
            }
        });

        lLayoutRecording = findViewById(R.id.layout_recording);
        chronometerRecordTime = findViewById(R.id.chronometer_camera_device_recordTime);

        llCtr = findViewById(R.id.ll_camera_ctr);

        mIbtnPlay = findViewById(R.id.ibtn_camera_play);
        mIbtnMute = findViewById(R.id.ibtn_camera_mute);
        mIbtnChannels = findViewById(R.id.ibtn_camera_channels);
        mIbtnFluency = findViewById(R.id.ibtn_camera_fluency);
        mIbtnFullscreen = findViewById(R.id.ibtn_camera_fullscreen);


        gLayoutPreview = findViewById(R.id.gLayout_camera_devicesPreview);
        previewFunVideoView1 = findViewById(R.id.funVideoView_preview1);
        previewFunVideoView2 = findViewById(R.id.funVideoView_preview2);
        previewFunVideoView3 = findViewById(R.id.funVideoView_preview3);
        previewFunVideoView4 = findViewById(R.id.funVideoView_preview4);
        previewFunVideoViewList.add(previewFunVideoView1);
        previewFunVideoViewList.add(previewFunVideoView2);
        previewFunVideoViewList.add(previewFunVideoView3);
        previewFunVideoViewList.add(previewFunVideoView4);
        progressBar1 = findViewById(R.id.pgb_camera_preview1);
        progressBar2 = findViewById(R.id.pgb_camera_preview2);
        progressBar3 = findViewById(R.id.pgb_camera_preview3);
        progressBar4 = findViewById(R.id.pgb_camera_preview4);
        progressBarList.add(progressBar1);
        progressBarList.add(progressBar2);
        progressBarList.add(progressBar3);
        progressBarList.add(progressBar4);

        previewFunVideoView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewDevs.size() > devGroup*4) {
                    if(System.currentTimeMillis() - doubleClickMillis < 300) {
                        previewDoubleClick();
                    }
                    else {
                        mFunDevice = previewDevs.get(devGroup*4);
                        deviceReload();
                        if(currPreviewFunVideoView != null) {
                            currPreviewFunVideoView.setSelected(false);
                            currPreviewFunVideoView.setMediaSound(false);
                        }
                        previewFunVideoView1.setSelected(true);
                        currPreviewFunVideoView = previewFunVideoView1;
                        if(mIbtnMute.isSelected()) {
                            previewFunVideoView1.setMediaSound(true);
                        }
                        doubleClickMillis = System.currentTimeMillis();
                    }
                }
            }
        });
        previewFunVideoView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewDevs.size() > 1 + devGroup*4) {
                    if(System.currentTimeMillis() - doubleClickMillis < 300) {
                        previewDoubleClick();
                    }
                    else {
                        mFunDevice = previewDevs.get(1 + devGroup*4);
                        deviceReload();
                        if(currPreviewFunVideoView != null) {
                            currPreviewFunVideoView.setSelected(false);
                            currPreviewFunVideoView.setMediaSound(false);
                        }
                        previewFunVideoView2.setSelected(true);
                        currPreviewFunVideoView = previewFunVideoView2;
                        if(mIbtnMute.isSelected()) {
                            previewFunVideoView2.setMediaSound(true);
                        }
                        doubleClickMillis = System.currentTimeMillis();
                    }
                }
            }
        });
        previewFunVideoView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewDevs.size() > 2 + devGroup*4) {
                    if(System.currentTimeMillis() - doubleClickMillis < 300) {
                        previewDoubleClick();
                    }
                    else {
                        mFunDevice = previewDevs.get(2 + devGroup*4);
                        deviceReload();
                        if(currPreviewFunVideoView != null) {
                            currPreviewFunVideoView.setSelected(false);
                            currPreviewFunVideoView.setMediaSound(false);
                        }
                        previewFunVideoView3.setSelected(true);
                        currPreviewFunVideoView = previewFunVideoView3;
                        if(mIbtnMute.isSelected()) {
                            previewFunVideoView3.setMediaSound(true);
                        }
                        doubleClickMillis = System.currentTimeMillis();
                    }
                }
            }
        });
        previewFunVideoView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewDevs.size() > 3 + devGroup*4) {
                    if(System.currentTimeMillis() - doubleClickMillis < 300) {
                        previewDoubleClick();
                    }
                    else {
                        mFunDevice = previewDevs.get(3 + devGroup*4);
                        deviceReload();
                        if(currPreviewFunVideoView != null) {
                            currPreviewFunVideoView.setSelected(false);
                            currPreviewFunVideoView.setMediaSound(false);
                        }
                        previewFunVideoView4.setSelected(true);
                        currPreviewFunVideoView = previewFunVideoView4;
                        if(mIbtnMute.isSelected()) {
                            previewFunVideoView4.setMediaSound(true);
                        }
                        doubleClickMillis = System.currentTimeMillis();
                    }
                }
            }
        });



        fluencyBottomDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.camera_dialog_bottom_fluency, null);
        btnHD = view.findViewById(R.id.btn_camera_hd);
        btnSD = view.findViewById(R.id.btn_camera_sd);
        fluencyBottomDialog.setContentView(view);

        if(funStreamTypeId == FunStreamType.STREAM_SECONDARY.getTypeId()) {
            mIbtnFluency.setSelected(true);
            btnSD.setSelected(true);
        }
        else {
            btnHD.setSelected(true);
        }


        mIbtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnRecord.isSelected()) {
                    showToast(getString(R.string.camera_recording_tips), Toast.LENGTH_SHORT);
                    return;
                }
                else if(mIbtnChannels.isSelected()) {
                    return;
                }
                if(!mIbtnPlay.isSelected()) {
                    mIbtnPlay.setSelected(true);
                    //mFunVideoView.stopPlayback();
                    mTextVideoStat.setText(R.string.camera_play_pause);
                    mTextVideoStat.setVisibility(View.VISIBLE);
                    pauseMedia();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
                else {
                    mIbtnPlay.setSelected(false);
                    mTextVideoStat.setVisibility(View.GONE);
                    //playRealMedia();
                    resumeMedia();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            }
        });
        mIbtnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIbtnChannels.isSelected()) {
                    if(currPreviewFunVideoView != null) {
                        if(!mIbtnMute.isSelected()) {
                            mIbtnMute.setSelected(true);
                            currPreviewFunVideoView.setMediaSound(true);
                        }
                        else {
                            mIbtnMute.setSelected(false);
                            currPreviewFunVideoView.setMediaSound(false);
                        }
                    }
                }
                else {
                    if(!mIbtnMute.isSelected()) {
                        mIbtnMute.setSelected(true);
                        mFunVideoView.setMediaSound(true);
                    }
                    else {
                        mIbtnMute.setSelected(false);
                        mFunVideoView.setMediaSound(false);
                    }
                }
            }
        });
        mIbtnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnRecord.isSelected()) {
                    showToast(getString(R.string.camera_recording_tips), Toast.LENGTH_SHORT);
                    return;
                }
                else if(iBtnPlayback.isSelected()) {
                    return;
                }
                if(!mIbtnChannels.isSelected()) {
                    mIbtnChannels.setSelected(true);
                    mFunVideoView.stopPlayback();
                    mFunVideoView.getSufaceView().setVisibility(View.GONE);
                    rLayoutVideoWnd.setVisibility(View.GONE);

                    gLayoutPreview.setVisibility(View.VISIBLE);

                    for(PreviewFunVideoView pfvv : previewFunVideoViewList) {
                        if(pfvv.getSufaceView() != null) {
                            pfvv.getSufaceView().setVisibility(View.VISIBLE);
                        }
                    }
                    for(int i=devGroup*4; i<previewDevs.size(); i++) {
                        if(i < 4 + devGroup*4) {
                            progressBarList.get(i - devGroup*4).setVisibility(View.VISIBLE);
                            playPreviewMedia(previewDevs.get(i), previewFunVideoViewList.get(i - devGroup*4));
                        }
                    }

                }
                else {
                    previewDoubleClick();
                }
            }
        });
        mIbtnFluency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIbtnChannels.isSelected() || mBtnRecord.isSelected() || iBtnPlayback.isSelected()) {
                    return;
                }
                fluencyBottomDialog.show();
            }
        });
        mIbtnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2Fullscreen();
            }
        });


        btnHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMainMediaStream();
                funStreamTypeId = FunStreamType.STREAM_MAIN.getTypeId();
                btnHD.setSelected(true);
                btnSD.setSelected(false);
                mIbtnFluency.setSelected(false);
                fluencyBottomDialog.dismiss();
            }
        });
        btnSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSecondaryMediaStream();
                funStreamTypeId = FunStreamType.STREAM_SECONDARY.getTypeId();
                btnSD.setSelected(true);
                btnHD.setSelected(false);
                mIbtnFluency.setSelected(true);
                fluencyBottomDialog.dismiss();
            }
        });

        mTextVideoStat = findViewById(R.id.textVideoStat);


        mBtnCapture = findViewById(R.id.btn_camera_capture);
        mBtnVoice = findViewById(R.id.btn_camera_voice);
        mBtnRecord = findViewById(R.id.btn_camera_record);
        mBtnFiles = findViewById(R.id.btn_camera_files);

        mBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToCapture();
            }
        });
        mBtnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(mIbtnChannels.isSelected()) {
                        if(currPreviewFunVideoView == null) {
                            showToast(getString(R.string.camera_preview_select_tips), Toast.LENGTH_SHORT);
                            return true;
                        }
                    }

                    //Android6.0 以上动态申请权限 当手机系统大于 23 时，才有必要去判断权限是否获取
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 检查该权限是否已经获取
                        int i = ContextCompat.checkSelfPermission(CameraDeviceActivity.this, Manifest.permission.RECORD_AUDIO);
                        // 权限是否已经授权 GRANTED---授权  DINIED---拒绝
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            //没有权限，向用户请求权限
                            ActivityCompat.requestPermissions(CameraDeviceActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                            return true;
                        }
                    }
                    //mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_pressed), null, null);
                    openVoiceChannel();
                    startTalk();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    //mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_selector), null, null);
                    stopTalk(0);
                    closeVoiceChannel();
                }
                return false;
            }

        });
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tryToRecord();
            }
        });
        mBtnFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraDeviceActivity.this, CameraFilesActivity.class);
                intent.putExtra("fileDir", fileDir);
                startActivity(intent);
            }
        });

        mLayoutDirectionControl = findViewById(R.id.layoutDirectionControl);
        mPtz_up = findViewById(R.id.ptz_up);
        mPtz_down = findViewById(R.id.ptz_down);
        mPtz_left = findViewById(R.id.ptz_left);
        mPtz_right = findViewById(R.id.ptz_right);


        mPtz_up.setOnTouchListener(onPtz_up);
        mPtz_down.setOnTouchListener(onPtz_down);
        mPtz_left.setOnTouchListener(onPtz_right);
        mPtz_right.setOnTouchListener(onPtz_left);

        lLayoutPlayback = findViewById(R.id.llayout_camera_playback);
        iBtnPlayback = findViewById(R.id.ibtn_camera_playback);
        iBtnPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIbtnChannels.isSelected()) {
                    showToast(getString(R.string.camera_channels_preview_tips), Toast.LENGTH_SHORT);
                    return;
                }
                else if(mBtnRecord.isSelected()) {
                    showToast(getString(R.string.camera_recording_tips), Toast.LENGTH_SHORT);
                    return;
                }
                if(!iBtnPlayback.isSelected()) {
                    iBtnPlayback.setSelected(true);
                    mLayoutDirectionControl.setVisibility(View.GONE);
                    lLayoutPlayback.setVisibility(View.VISIBLE);

                    mFunVideoView.stopPlayback();
                    mBtnVoice.setEnabled(false);
                    mBtnFiles.setEnabled(false);
                    //requestRecDate();
                    rvPlaybackDate.scrollToPosition(recordDateAdapter.getItemCount()-1);
                    recordDateAdapter.setThisPosition(recordDateAdapter.getItemCount()-1);
                    onSearchFile(Calendar.getInstance().getTime());
                }
                else {
                    iBtnPlayback.setSelected(false);
                    mFunVideoView.stopPlayback();

                    recordDatas = null;
                    playbackDaylongView.clearRecordData();
                    lLayoutPlayback.setVisibility(View.GONE);
                    mLayoutDirectionControl.setVisibility(View.VISIBLE);

                    mBtnVoice.setEnabled(true);
                    mBtnFiles.setEnabled(true);

                    playRealMedia();
                }
            }
        });

        playbackDaylongView = findViewById(R.id.playback_daylong_view);
        rvPlaybackDate = findViewById(R.id.rv_camera_playback_date);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPlaybackDate.setLayoutManager(layoutManager);
        recordDateAdapter = new CameraPlaybackDateRvAdapter(this);
        rvPlaybackDate.setAdapter(recordDateAdapter);
        recordDateAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Date date) {
                mFunVideoView.stopPlayback();
                recordDatas = null;
                playbackDaylongView.clearRecordData();
                onSearchFile(date);
            }
        });

        playbackDaylongView.setOnValueChangeListener(new OnValueChangeListener() {
            @Override
            public synchronized void playRecordFileWithIndex(int index) {
                recordDatasIndex = index;
                playRecordVideoByFile(index);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playbackDaylongView.setRecordScale(index);
                    }
                });
            }
        });
        iBtnPlaybackLeft = findViewById(R.id.ibtn_camera_playback_left);
        iBtnPlaybackRight = findViewById(R.id.ibtn_camera_playback_right);
        iBtnPlaybackLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDatasIndex --;
                if(recordDatas != null) {
                    if(recordDatasIndex < 0) {
                        recordDatasIndex = 0;
                    }
                    else if(recordDatasIndex > recordDatas.length - 1) {
                        recordDatasIndex = recordDatas.length - 1;
                    }
                    if(recordDatasIndex >= 0) {
                        playbackDaylongView.setRecordScale(recordDatasIndex);
                        playRecordVideoByFile(recordDatasIndex);
                    }
                }
            }
        });
        iBtnPlaybackRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDatasIndex ++;
                if(recordDatas != null) {
                    if(recordDatasIndex < 0) {
                        recordDatasIndex = 0;
                    }
                    else if(recordDatasIndex > recordDatas.length - 1) {
                        recordDatasIndex = recordDatas.length - 1;
                    }
                    if(recordDatasIndex < recordDatas.length) {
                        playbackDaylongView.setRecordScale(recordDatasIndex);
                        playRecordVideoByFile(recordDatasIndex);
                    }
                }
            }
        });


        // 注册设备操作回调
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);


        // 允许横竖屏切换
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mTalkManager = new TalkManager(mFunDevice);

        mCanToPlay = false;


        File file = new File(FunPath.getDefaultPath() + mUser.getUser_guid() + File.separator);
        if(!file.exists()) {
            file.mkdir();
        }
        fileDir = FunPath.getDefaultPath() + mUser.getUser_guid() + File.separator + mFunDevice.getDevSn() + File.separator;


        // 如果设备未登录,先登录设备
        if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
            loginDevice();
        } else {
            //requestSystemInfo();
            mCanToPlay = true;
            playRealMedia();
        }

        funDeviceCaptureListener = new OnFunDeviceCaptureListener() {
            @Override
            public void onCaptureSuccess(String picStr) {
                if(picStr.endsWith("last.jpg")) {
                    return;
                }
                toastScreenShotPreview(picStr);
            }

            @Override
            public void onCaptureFailed(int ErrorCode) {
                showToast(FunError.getErrorStr(ErrorCode));
            }
        };


        FunSupport.getInstance().registerOnFunDeviceCaptureListener(funDeviceCaptureListener);
        //FunSupport.getInstance().registerOnFunDeviceRecordListener(funDeviceRecordListener);

        FunSupport.getInstance().requestOtherDevicesLogin(mFunDevice);

        devGroup = getIntent().getIntExtra("position", 0) / 4;

        previewDevs = FunSupport.getInstance().getDeviceList();
    }


    @Override
    protected void onDestroy() {

        //stopMedia();
        previewDevs = null;
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
        FunSupport.getInstance().removeOnFunDeviceCaptureListener(funDeviceCaptureListener);

		 //if ( null != mFunDevice ) {
		     //FunSupport.getInstance().requestDeviceLogout(mFunDevice);
		 //}

		 recordDateAdapter.setOnItemClickListener(null);

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("funStreamTypeId" + mFunDevice.getDevSn(), funStreamTypeId);
        editor.apply();

        super.onDestroy();
    }


    @Override
    protected void onResume() {

        if (mCanToPlay && !mIbtnChannels.isSelected() && !mBtnRecord.isSelected() && !iBtnPlayback.isSelected()) {
            //神代码，接口有尿性，先预热一下
            prePlayRealMedia();
            stopMedia();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playRealMedia();
                }
            }, 300);
        }
//			 resumeMedia();

        super.onResume();
    }


    @Override
    protected void onPause() {

        lastCapture();

        //stopTalk(0);
        //closeVoiceChannel();

        //如果预览的时候stopMedia，会把预览的也关掉
        if(!mIbtnChannels.isSelected() && !mBtnRecord.isSelected() && !iBtnPlayback.isSelected()) {
            stopMedia();
        }
//		 pauseMedia();

        super.onPause();
    }

    private void deviceReload() {
        mTalkManager.setFunDevice(mFunDevice);
        fileDir = FunPath.getDefaultPath() + mUser.getUser_guid() + File.separator + mFunDevice.getDevSn() + File.separator;
        FunSupport.getInstance().mCurrDevice = mFunDevice;
    }
    private void previewDoubleClick() {
        mIbtnChannels.setSelected(false);
        for(int i=devGroup*4; i<previewDevs.size(); i++) {
            if(i < 4 + devGroup*4) {
                previewFunVideoViewList.get(i - devGroup*4).stopPlayback();
            }
        }

        if(currPreviewFunVideoView != null) {
            currPreviewFunVideoView.setSelected(false);
            currPreviewFunVideoView = null;
        }

        for(PreviewFunVideoView pfvv : previewFunVideoViewList) {
            pfvv.getSufaceView().setVisibility(View.GONE);
        }

        gLayoutPreview.setVisibility(View.GONE);
        rLayoutVideoWnd.setVisibility(View.VISIBLE);
        mFunVideoView.getSufaceView().setVisibility(View.VISIBLE);

        if(!mFunDevice.hasLogin()) {
            loginDevice();
        }
        else {
            playRealMedia();
        }
    }


    private void switchMainMediaStream() {
        if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
            mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
            // 重新播放
            mFunVideoView.stopPlayback();
            playRealMedia();
        }
    }
    private void switchSecondaryMediaStream() {
        if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
            mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
            // 重新播放
            mFunVideoView.stopPlayback();
            playRealMedia();
        }
    }


    private void tryToRecord() {

        if (!mFunVideoView.isPlaying() || mFunVideoView.isPaused()) {
            showToast(getResources().getString(R.string.media_record_failure_need_playing));
            return;
        }

        if (mFunVideoView.bRecord) {
            mBtnRecord.setSelected(false);
            //mBtnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_record_selector), null, null);
            mFunVideoView.stopRecordVideo();
            chronometerRecordTime.stop();
            lLayoutRecording.setVisibility(View.GONE);

            if(!iBtnPlayback.isSelected()) {
                mBtnFiles.setEnabled(true);
            }

            toastRecordSucess(mFunVideoView.getFilePath());
        } else {
            mBtnRecord.setSelected(true);
            //mBtnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_recording), null, null);
            mBtnFiles.setEnabled(false);

            mFunVideoView.startRecordVideo(fileDir + System.currentTimeMillis() + ".mp4");
            //showToast(getString(R.string.media_record_start));
            lLayoutRecording.setVisibility(View.VISIBLE);

            chronometerRecordTime.setBase(SystemClock.elapsedRealtime());
            chronometerRecordTime.start();
        }

    }

    private void lastCapture() {
        mFunVideoView.captureImage(getCacheDir().getAbsolutePath() + mUser.getUser_guid() + mFunDevice.getDevSn() + "last.jpg");
    }

    /**
     * 视频截图,并延时一会提示截图对话框
     */
    private void tryToCapture() {
        if (!mFunVideoView.isPlaying()) {
            showToast(getResources().getString(R.string.media_capture_failure_need_playing));
            return;
        }

        mFunVideoView.captureImage(fileDir + System.currentTimeMillis() + ".jpg");	//图片异步保存

    }



    /**
     * 显示截图成功对话框
     * @param path
     */
    private void toastScreenShotPreview(final String path) {
        View view = getLayoutInflater().inflate(R.layout.screenshot_preview, null, false);
        ImageView iv = view.findViewById(R.id.iv_screenshot_preview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        iv.setImageBitmap(bitmap);
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_socket_capture_preview)
                .setView(view)
                .setPositiveButton(R.string.device_socket_capture_save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(path);
                                if(file.exists()) {
                                    showToast(getString(R.string.device_socket_capture_save_success));
                                }
                                //File imgPath = new File(FunPath.PATH_PHOTO + File.separator + file.getName());
                                //if (imgPath.exists()) {
                                    //showToast(getString(R.string.device_socket_capture_exist));
                                //} else {
                                    //FileUtils.copyFile(path, FunPath.PATH_PHOTO + File.separator + file.getName());
                                    //showToast(getString(R.string.device_socket_capture_save_success));
                                //}
                            }
                        })
                .setNegativeButton(R.string.device_socket_capture_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FunPath.deleteFile(path);
                                showToast(getString(R.string.device_socket_capture_delete_success));
                            }
                        }).setCancelable(false)
                .show();
    }

    /**
     * 显示录像成功对话框
     * @param
     */
    private void toastRecordSucess(final String path) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_sport_camera_record_success)
                .setMessage(getString(R.string.media_record_stop) + " " + getString(R.string.camera_device_files))
                .setPositiveButton(R.string.device_sport_camera_record_success_open,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.VIEW");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Uri uri;
                                File file = new File(path);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    uri = FileProvider.getUriForFile(CameraDeviceActivity.this, getPackageName() + ".fileProvider", file);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                } else {
                                    uri = Uri.fromFile(file);
                                }
                                intent.setDataAndType(uri, "video/*");
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.device_sport_camera_record_success_cancel, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        // 如果当前是横屏，返回时先回到竖屏
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            //3秒之后设置为根据系统感应
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }, 3000);
            return;
        }
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 检测屏幕的方向：纵向或横向
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            showAsLandscape();
        }
        else if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            showAsPortrait();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void showAsLandscape() {
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 隐藏底部的控制区域
        llCtr.setVisibility(View.GONE);

        // 视频窗口全屏显示
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) rLayoutTop.getLayoutParams();
        lpWnd.height = ViewGroup.LayoutParams.MATCH_PARENT;
        // lpWnd.removeRule(RelativeLayout.BELOW);
        //lpWnd.topMargin = 0;
        rLayoutTop.setLayoutParams(lpWnd);

    }

    private void showAsPortrait() {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 视频显示为小窗口
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) rLayoutTop.getLayoutParams();
        lpWnd.height = UIFactory.dip2px(this, 240);
        //lpWnd.topMargin = UIFactory.dip2px(this, 48);
        // lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
        rLayoutTop.setLayoutParams(lpWnd);

        // 显示底部的控制区域
        llCtr.setVisibility(View.VISIBLE);

    }

    /**
     * 切换视频全屏/小视频窗口(以切横竖屏切换替代)
     */
    private void switch2Fullscreen() {
        // 横屏切换
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void loginDevice() {
        if(!isSaveNativePw) {
            showInputPasswordDialog();
        }
        else {
            showDialog(false);
            FunSupport.getInstance().requestDeviceLogin(mFunDevice);
        }
    }

    private void requestSystemInfo() {
        //showWaitDialog();
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
    }

    // 获取设备预置点列表
    private void requestPTZPreset() {
        FunSupport.getInstance().requestDeviceConfig(mFunDevice, OPPTZPreset.CONFIG_NAME, 0);
    }



    private void playRealMedia() {

        // 显示状态: 正在打开视频...
        mTextVideoStat.setText(R.string.media_player_opening);
        mTextVideoStat.setVisibility(View.VISIBLE);

        if (mFunDevice.isRemote) {
            mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
        }

        mIbtnPlay.setSelected(false);
        // 打开声音
        if(mIbtnMute.isSelected()) {
            mFunVideoView.setMediaSound(true);
        }
        else {
            mFunVideoView.setMediaSound(false);
        }

    }
    private void prePlayRealMedia() {

        if (mFunDevice.isRemote) {
            mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
        }

    }

    private void stopMedia() {
        mFunVideoView.stopPlayback();
        mFunVideoView.stopRecordVideo();
    }

    private void pauseMedia() {
        mFunVideoView.pause();
    }

    private void resumeMedia() {
        mFunVideoView.resume();
    }

    private void switchMediaStream() {
        if (null != mFunVideoView) {
            if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
            } else {
                mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
            }

            // 重新播放
            mFunVideoView.stopPlayback();
            playRealMedia();
        }
    }


    private void startTalk() {
        if (mTalkManager != null) {
            mTalkManager.onStartThread();
            mTalkManager.setTalkSound(false);
        }
    }

    private void stopTalk(int delayTime) {
        if (mTalkManager != null) {
            mTalkManager.onStopThread();
            mTalkManager.setTalkSound(true);
        }
    }

    private void openVoiceChannel(){
        if(currPreviewFunVideoView == null) {
            mFunVideoView.setMediaSound(false);			//关闭本地音频
        }
        else {
            currPreviewFunVideoView.setMediaSound(false);
        }

        mIbtnMute.setSelected(false);

        mTalkManager.onStartTalk();
        mTalkManager.setTalkSound(true);
    }

    private void closeVoiceChannel(){
        mTalkManager.onStopTalk();
        //if(mIbtnMute.isSelected()) {

        //}
        //mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);

        if(currPreviewFunVideoView == null) {
            mFunVideoView.setMediaSound(true);
        }
        else {
            currPreviewFunVideoView.setMediaSound(true);
        }

        mIbtnMute.setSelected(true);
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
                NativeLoginPsw = editText;

                onDeviceSaveNativePws();

                // 重新登录
                //loginDevice();
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


    public void onDeviceSaveNativePws() {
        //FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), NativeLoginPsw);

        // 库函数方式本地保存密码
        //if (FunSupport.getInstance().getSaveNativePassword()) {
            FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", NativeLoginPsw);
            // 如果设置了使用本地保存密码，则将密码保存到本地文件
        //}
    }

    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {
        if (mFunDevice.getId() == funDevice.getId()) {
            dialogDismiss();
            // 登录成功后立刻获取SystemInfo
            // 如果不需要获取SystemInfo,在这里播放视频也可以:playRealMedia();
            //requestSystemInfo();

            File file = new File(fileDir);
            if(!file.exists()) {
                file.mkdir();
            }

            // 设置允许播放标志
            mCanToPlay = true;

            playRealMedia();
        }
    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
        // 设备登录失败
        //hideWaitDialog();
        //showToast(FunError.getErrorStr(errCode));
        if(mFunDevice.getId() == funDevice.getId()) {
            dialogDismiss();

            // 如果账号密码不正确,那么需要提示用户,输入密码重新登录
            if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
                showInputPasswordDialog();
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.camera_connection_failed)
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false)
                        .show();
                //showToast("Device connection failed!");
            }
        }
    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        int channelCount = 0;
        if (SystemInfo.CONFIG_NAME.equals(configName)) {

            if (!isGetSysFirst) {
                return;
            }

            // 更新UI
            //此处为示例如何取通道信息，可能会增加打开视频的时间，可根据需求自行修改代码逻辑
            if (funDevice.channel == null) {
                FunSupport.getInstance().requestGetDevChnName(funDevice);
                requestSystemInfo();
                return;
            }
            channelCount = funDevice.channel.nChnCount;
            // if (channelCount >= 5) {
            // channelCount = 5;
            // }
            if (channelCount > 1) {
                mChannelCount = channelCount;
            }

            //hideWaitDialog();
            //dialogDismiss();


            // 设置允许播放标志
            mCanToPlay = true;

            isGetSysFirst = false;

            //showToast(getType(funDevice.getNetConnectType()));

            // 获取信息成功后,如果WiFi连接了就自动播放
            // 此处逻辑客户自定义
//			if (MyUtils.detectWifiNetwork(this)) {
            playRealMedia();
//			} else {
//				showToast(R.string.meida_not_auto_play_because_no_wifi);
//			}

            // 如果支持云台控制,在获取到SystemInfo之后,获取预置点信息,如果不需要云台控制/预置点功能功能,可忽略之
            //if (mFunDevice.isSupportPTZ()) {
                //requestPTZPreset();
            //}
        }
        else if (configName.equals(DevCmdOPSCalendar.CONFIG_NAME)) {
            DevCmdOPSCalendar calendar = (DevCmdOPSCalendar) funDevice.getConfig(DevCmdOPSCalendar.CONFIG_NAME);
            recordDateList.clear();
            for (int i = 0; i < calendar.getData().size(); i++) {
                recordDateList.add(calendar.getData().get(i).getDispDate());
            }
            recordDateAdapter.setThisPosition(recordDateList.size() - 1);
            rvPlaybackDate.scrollToPosition(recordDateAdapter.getItemCount() - 1);
        }
        mFunDevice.invalidConfig(DevCmdOPSCalendar.CONFIG_NAME);
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
        //showToast(FunError.getErrorStr(errCode));
        if (errCode == -11406) {
            funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
        }
    }


    @Override
    public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

    }


    @Override
    public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
    }

    @Override
    public void onDeviceChangeInfoSuccess(FunDevice funDevice) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceOptionSuccess(FunDevice funDevice, String option) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

        if (funDevice.getId() == mFunDevice.getId()) {
            dialogDismiss();
            recordDatas = datas;

            if (datas.length == 0) {
                showToast("device_camera_video_list_empty");
            } else {
                //showToast("rec files size" + datas.length);
                playbackDaylongView.setData(datas);
                //自动播放第一个
                recordDatasIndex = 0;
                playbackDaylongView.setRecordScale(0);
                playRecordVideoByFile(0);
            }
        }
    }


    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 播放失败
        showToast(FunError.getErrorStr(extra));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
                || FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
            // 不支持高清码流,设置为标清码流重新播放
            mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
            playRealMedia();
        }

        return true;
    }


    @Override
    public boolean onInfo(MediaPlayer arg0, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mTextVideoStat.setText(R.string.media_player_buffering);
            mTextVideoStat.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if(mIbtnChannels.isSelected()) {
                for(int i=devGroup*4; i<previewDevs.size(); i++) {
                    if(i < 4 + devGroup*4) {
                        progressBarList.get(i - devGroup*4).setVisibility(View.GONE);
                    }
                }
            }
            else {
                mTextVideoStat.setVisibility(View.GONE);
            }
        }
        return true;
    }

    private View.OnTouchListener onPtz_up = new View.OnTouchListener() {

        // @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent arg1) {
            boolean bstop = true;
            int nPTZCommand = -1;
            // return false;
            switch (arg1.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    bstop = false;
                    nPTZCommand = EPTZCMD.TILT_UP;
                    break;
                case KeyEvent.ACTION_UP:
                    nPTZCommand = EPTZCMD.TILT_UP;
                    bstop = true;
                    break;
                case KeyEvent.ACTION_MULTIPLE:
                    nPTZCommand = EPTZCMD.TILT_UP;
                    bstop = Math.abs(arg1.getX()) > v.getWidth()
                            || Math.abs(arg1.getY()) > v.getHeight();
                    break;
                default:
                    break;
            }
            onContrlPTZ1(nPTZCommand, bstop);
            return false;
        }
    };
    private View.OnTouchListener onPtz_down = new View.OnTouchListener() {

        // @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent arg1) {
            boolean bstop = true;
            int nPTZCommand = -1;
            // return false;
            switch (arg1.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    bstop = false;
                    nPTZCommand = EPTZCMD.TILT_DOWN;
                    break;
                case KeyEvent.ACTION_UP:
                    bstop = true;
                    nPTZCommand = EPTZCMD.TILT_DOWN;
                    onContrlPTZ1(nPTZCommand, bstop);
                    break;
                case KeyEvent.ACTION_MULTIPLE:
                    nPTZCommand = EPTZCMD.TILT_DOWN;
                    bstop = Math.abs(arg1.getX()) > v.getWidth()
                            || Math.abs(arg1.getY()) > v.getHeight();
                    break;
                default:
                    break;
            }
            onContrlPTZ1(nPTZCommand, bstop);
            return false;
        }
    };
    private View.OnTouchListener onPtz_left = new View.OnTouchListener() {

        // @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent arg1) {
            boolean bstop = true;
            int nPTZCommand = -1;
            // return false;
            switch (arg1.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    bstop = false;
                    nPTZCommand = EPTZCMD.PAN_LEFT;
                    break;
                case KeyEvent.ACTION_UP:
                    bstop = true;
                    nPTZCommand = EPTZCMD.PAN_LEFT;
                    break;
                case KeyEvent.ACTION_MULTIPLE:
                    nPTZCommand = EPTZCMD.PAN_LEFT;
                    bstop = Math.abs(arg1.getX()) > v.getWidth()
                            || Math.abs(arg1.getY()) > v.getHeight();
                    break;
                default:
                    break;
            }
            onContrlPTZ1(nPTZCommand, bstop);
            return false;
        }
    };
    private View.OnTouchListener onPtz_right = new View.OnTouchListener() {

        // @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent arg1) {
            boolean bstop = true;
            int nPTZCommand = -1;
            // return false;
            switch (arg1.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    bstop = false;
                    nPTZCommand = EPTZCMD.PAN_RIGHT;
                    break;
                case KeyEvent.ACTION_UP:
                    bstop = true;
                    nPTZCommand = EPTZCMD.PAN_RIGHT;
                    break;
                case KeyEvent.ACTION_MULTIPLE:
                    nPTZCommand = EPTZCMD.PAN_RIGHT;
                    bstop = Math.abs(arg1.getX()) > v.getWidth()
                            || Math.abs(arg1.getY()) > v.getHeight();
                    break;
                default:
                    break;
            }
            onContrlPTZ1(nPTZCommand, bstop);
            return false;
        }
    };


    private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
        FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
                nPTZCommand, bStop, mFunDevice.CurrChannel);
    }


    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {
        // TODO Auto-generated method stub

    }


    private void onSearchFile(Date date) {
        showDialog(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogDismiss();
            }
        }, 5000);
    }

    private void  requestRecDate(){
        DevCmdOPSCalendar opsCalendar = (DevCmdOPSCalendar) mFunDevice.checkConfig(DevCmdOPSCalendar.CONFIG_NAME);
        opsCalendar.setEvent("*");
        opsCalendar.setFileType("h264");
        opsCalendar.setRev("");
        opsCalendar.setYear(Calendar.getInstance().get(Calendar.YEAR));
        mFunDevice.setConfig(opsCalendar);
        FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, opsCalendar);
    }
    private void playRecordVideoByFile(int index) {
        mFunVideoView.stopPlayback();

        //FunFileData funFileData = new FunFileData(recordDatas[index], new OPCompressPic());
        mFunVideoView.playRecordByFile(mFunDevice.getDevSn(), recordDatas[index], mFunDevice.CurrChannel);
        if(mIbtnMute.isSelected()) {
            mFunVideoView.setMediaSound(true);
        }
        else {
            mFunVideoView.setMediaSound(false);
        }
    }


    private void playPreviewMedia(FunDevice funDevice, PreviewFunVideoView previewFunVideoView) {

        if (funDevice.isRemote) {
            previewFunVideoView.setRealDevice(funDevice.getDevSn(), funDevice.CurrChannel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            previewFunVideoView.setRealDevice(deviceIp, funDevice.CurrChannel);
        }

        // 打开声音
        previewFunVideoView.setMediaSound(false);

    }
}
