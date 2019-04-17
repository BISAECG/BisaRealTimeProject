package com.bisa.health.camera;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.Button;


import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.adapter.CameraPlaybackDateRvAdapter;
import com.bisa.health.camera.interfaces.OnItemClickListener;
import com.bisa.health.camera.lib.funsdk.support.FunDevicePassword;
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

    private FunDevice mFunDevice = new FunDevice();

    private RelativeLayout rLayoutTop;

    private RelativeLayout rLayoutVideoWnd;
    private FunVideoView mFunVideoView;

    private LinearLayout mLayoutRecording;

    private LinearLayout llCtr;

    private ImageButton mIbtnPlay;
    private ImageButton mIbtnMute;
    private ImageButton mIbtnChannels;
    private ImageButton mIbtnFluency;
    private ImageButton mIbtnFullscreen;

    private BottomSheetDialog fluencyBottomDialog;
    private Button btnHD;
    private Button btnSD;
    private Button btnLD;


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

    private LinearLayout linearLayoutPreview;
    private PreviewFunVideoView previewFunVideoView1, previewFunVideoView2, previewFunVideoView3, previewFunVideoView4;
    private List<PreviewFunVideoView> previewFunVideoViewList = new ArrayList<>();
    private List<FunDevice> funDeviceList;//多通道预览

    private ImageButton iBtnPlayback;
    private LinearLayout lLayoutPlayback;
    private RecyclerView rvPlaybackDate;
    private PlaybackDaylongView playbackDaylongView;

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

    private boolean playingFlagBl = true;
    private boolean muteFlagBl = true;
    private boolean previewFlagBl = false;
    private boolean firstPreviewFlagBl = true;
    private boolean recordingFlagBl = false;
    private boolean playbackFlagBl = false;

    private OnFunDeviceCaptureListener funDeviceCaptureListener;
    private OnFunDeviceRecordListener funDeviceRecordListener;

    private List<String> recordDateList = new ArrayList<String>();
    private CameraPlaybackDateRvAdapter recordDateAdapter;
    private H264_DVR_FILE_DATA[] recordDatas;
    private int recordDatasIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_device);

        mFunDevice = FunSupport.getInstance().mCurrDevice;

        if (null == mFunDevice) {
            finish();
            return;
        }

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

        mFunVideoView.setOnPreparedListener(this);
        mFunVideoView.setOnErrorListener(this);
        mFunVideoView.setOnInfoListener(this);
        mFunVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recordDatasIndex ++;
                if(recordDatasIndex < recordDatas.length) {
                    playbackDaylongView.setRecordScale(recordDatasIndex);
                    playRecordVideoByFile(recordDatasIndex);
                }
            }
        });

        mLayoutRecording = findViewById(R.id.layout_recording);

        llCtr = findViewById(R.id.ll_camera_ctr);

        mIbtnPlay = findViewById(R.id.ibtn_camera_play);
        mIbtnMute = findViewById(R.id.ibtn_camera_mute);
        mIbtnChannels = findViewById(R.id.ibtn_camera_channels);
        mIbtnFluency = findViewById(R.id.ibtn_camera_fluency);
        mIbtnFullscreen = findViewById(R.id.ibtn_camera_fullscreen);

        funDeviceList = FunSupport.getInstance().getDeviceList();
        linearLayoutPreview = findViewById(R.id.lLayout_camera_devicesPreview);
        previewFunVideoView1 = findViewById(R.id.previewFunVideoView1);
        previewFunVideoView2 = findViewById(R.id.previewFunVideoView2);
        previewFunVideoView3 = findViewById(R.id.previewFunVideoView3);
        previewFunVideoView4 = findViewById(R.id.previewFunVideoView4);
        previewFunVideoViewList.add(previewFunVideoView1);
        previewFunVideoViewList.add(previewFunVideoView2);
        previewFunVideoViewList.add(previewFunVideoView3);
        previewFunVideoViewList.add(previewFunVideoView4);

        fluencyBottomDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_bottom_definition, null);
        btnHD = view.findViewById(R.id.btn_camera_hd);
        btnSD = view.findViewById(R.id.btn_camera_sd);
        btnLD = view.findViewById(R.id.btn_camera_ld);
        fluencyBottomDialog.setContentView(view);


        mIbtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playingFlagBl) {
                    playingFlagBl = false;
                    pauseMedia();
                    mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_play));
                }
                else {
                    playingFlagBl = true;
                    mFunVideoView.stopPlayback();
                    playRealMedia();
                    mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_pause));
                }
            }
        });
        mIbtnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(muteFlagBl) {
                    muteFlagBl = false;
                    openVoiceChannel();
                    mIbtnMute.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_unmute));
                }
                else {
                    muteFlagBl = true;
                    closeVoiceChannel();
                    mIbtnMute.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_mute));
                }
            }
        });
        mIbtnChannels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!previewFlagBl) {
                    previewFlagBl = true;
                    mFunVideoView.stopPlayback();
                    mFunVideoView.getSufaceView().setVisibility(View.GONE);
                    rLayoutVideoWnd.setVisibility(View.GONE);
                    if(!firstPreviewFlagBl) {
                        for(PreviewFunVideoView p : previewFunVideoViewList) {
                            p.getSufaceView().setVisibility(View.VISIBLE);
                        }
                    }
                    for(int i=0; i<funDeviceList.size(); i++) {
                        if(i<4) {
                            previewFunVideoViewList.get(i).setRealDevice(funDeviceList.get(i));
                            final int t = i;
                            previewFunVideoViewList.get(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mFunDevice = funDeviceList.get(t);
                                    mIbtnChannels.callOnClick();
                                }
                            });
                        }
                        else {
                            break;
                        }
                    }
                    linearLayoutPreview.setVisibility(View.VISIBLE);

                }
                else {
                    previewFlagBl = false;
                    for(int i=0; i<funDeviceList.size(); i++) {
                        if(i<4) {
                            previewFunVideoViewList.get(i).stopPlayback();
                        }
                        else {
                            break;
                        }
                    }
                    for(PreviewFunVideoView p : previewFunVideoViewList) {
                        p.getSufaceView().setVisibility(View.GONE);
                    }
                    linearLayoutPreview.setVisibility(View.GONE);
                    mFunVideoView.getSufaceView().setVisibility(View.VISIBLE);
                    rLayoutVideoWnd.setVisibility(View.VISIBLE);

                    playRealMedia();
                }
                firstPreviewFlagBl = false;
            }
        });
        mIbtnFluency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                mIbtnFluency.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_hd_sim));
                switchMainMediaStream();
                mFunVideoView.setMediaPlayHD();
                fluencyBottomDialog.dismiss();
            }
        });
        btnSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIbtnFluency.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_sd_sim));
                switchMainMediaStream();
                mFunVideoView.setMediaPlayLD();
                fluencyBottomDialog.dismiss();
            }
        });
        btnLD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIbtnFluency.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_ld_sim));
                //mFunVideoView.setMediaPlayLD();
                switchSecondaryMediaStream();
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
                    if(muteFlagBl) {
                        showToast(getString(R.string.camera_voice_not_open));
                        return true;
                    }
                    mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_pressed), null, null);
                    startTalk();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_normal), null, null);
                    stopTalk(500);
                }
                return true;
            }

        });
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recordingFlagBl) {
                    recordingFlagBl = true;
                    mBtnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_recording), null, null);
                }
                else {
                    recordingFlagBl = false;
                    mBtnRecord.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_record_selector), null, null);
                }
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
                if(recordingFlagBl) {
                    showToast(getString(R.string.camera_recording_tips));
                    return;
                }
                if(!playbackFlagBl) {
                    playbackFlagBl = true;
                    iBtnPlayback.setImageDrawable(getResources().getDrawable(R.drawable.camera_ptz_small));
                    mLayoutDirectionControl.setVisibility(View.GONE);
                    lLayoutPlayback.setVisibility(View.VISIBLE);
                    mBtnVoice.setEnabled(false);
                    mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_pressed), null, null);
                    requestRecDate();
                    onSearchFile(Calendar.getInstance().getTime());
                }
                else {
                    playbackFlagBl = false;
                    iBtnPlayback.setImageDrawable(getResources().getDrawable(R.drawable.camera_playback));
                    lLayoutPlayback.setVisibility(View.GONE);
                    mLayoutDirectionControl.setVisibility(View.VISIBLE);
                    mBtnVoice.setEnabled(true);
                    mBtnVoice.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.camera_func_voice_normal), null, null);
                    mFunVideoView.stopPlayback();
                    playRealMedia();
                }
            }
        });
        rvPlaybackDate = findViewById(R.id.rv_camera_playback_date);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPlaybackDate.setLayoutManager(layoutManager);
        recordDateAdapter = new CameraPlaybackDateRvAdapter(this, recordDateList);
        rvPlaybackDate.setAdapter(recordDateAdapter);
        recordDateAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Date date) {
                onSearchFile(date);
            }
        });

        playbackDaylongView = findViewById(R.id.playback_daylong_view);
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


        // 注册设备操作回调
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);


        // 允许横竖屏切换
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        mTalkManager = new TalkManager(mFunDevice);

        mCanToPlay = false;

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        fileDir = FunPath.getDefaultPath() + mUser.getUser_guid() + File.separator;
        File file = new File(fileDir);
        if(!file.exists()) {
            file.mkdir();
        }

        // 如果设备未登录,先登录设备
        if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
            loginDevice();
        } else {
            requestSystemInfo();
        }

        funDeviceCaptureListener = new OnFunDeviceCaptureListener() {
            @Override
            public void onCaptureSuccess(String picStr) {
                if(picStr.endsWith(mFunDevice.getDevSn() + ".jpg")) {
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
    }


    @Override
    protected void onDestroy() {

        stopMedia();

        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
        FunSupport.getInstance().removeOnFunDeviceCaptureListener(funDeviceCaptureListener);

		 if ( null != mFunDevice ) {
		     FunSupport.getInstance().requestDeviceLogout(mFunDevice);
		 }

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        lastCapture();
        super.onStop();
    }

    @Override
    protected void onResume() {

        if (mCanToPlay && !previewFlagBl) {
            playRealMedia();
        }
//			 resumeMedia();

        super.onResume();
    }


    @Override
    protected void onPause() {

        stopTalk(0);
        closeVoiceChannel();

        if(!previewFlagBl) {
            stopMedia();
        }
//		 pauseMedia();

        super.onPause();
    }


    private void switchMainMediaStream() {
        if (null != mFunVideoView) {
            if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
                // 重新播放
                mFunVideoView.stopPlayback();
                playRealMedia();
            }

        }
    }
    private void switchSecondaryMediaStream() {
        if (null != mFunVideoView) {
            if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
                // 重新播放
                mFunVideoView.stopPlayback();
                playRealMedia();
            }

        }
    }


    private void tryToRecord() {

        if (!mFunVideoView.isPlaying() || mFunVideoView.isPaused()) {
            showToast(getResources().getString(R.string.media_record_failure_need_playing));
            return;
        }


        if (mFunVideoView.bRecord) {
            mFunVideoView.stopRecordVideo();
            mLayoutRecording.setVisibility(View.GONE);
            toastRecordSucess(mFunVideoView.getFilePath());
        } else {
            mFunVideoView.startRecordVideo(fileDir + System.currentTimeMillis() + ".mp4");
            mLayoutRecording.setVisibility(View.VISIBLE);
            showToast(getResources().getString(R.string.media_record_start));
        }

    }

    private void lastCapture() {
        if (!mFunVideoView.isPlaying()) {
            return;
        }

        mFunVideoView.captureImage(fileDir + mFunDevice.getDevSn() + ".jpg");	//图片异步保存

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
        ImageView iv = (ImageView) view.findViewById(R.id.iv_screenshot_preview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv.setImageBitmap(bitmap);
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_socket_capture_preview)
                .setView(view)
                .setPositiveButton(R.string.device_socket_capture_save,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(path);
                                File imgPath = new File(FunPath.PATH_PHOTO + File.separator
                                        + file.getName());
                                if (imgPath.exists()) {
                                    showToast(getString(R.string.device_socket_capture_exist));
                                } else {
                                    FileUtils.copyFile(path, FunPath.PATH_PHOTO + File.separator
                                            + file.getName());
                                    showToast(getString(R.string.device_socket_capture_save_success));
                                }
                            }
                        })
                .setNegativeButton(R.string.device_socket_capture_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FunPath.deleteFile(path);
                                showToast(getString(R.string.device_socket_capture_delete_success));
                            }
                        })
                .show();
    }

    /**
     * 显示录像成功对话框
     * @param
     */
    private void toastRecordSucess(final String path) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_sport_camera_record_success)
                .setMessage(getString(R.string.media_record_stop) + path)
                .setPositiveButton(R.string.device_sport_camera_record_success_open,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.intent.action.VIEW");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                String type = "video/*";
                                Uri uri = Uri.fromFile(new File(path));
                                intent.setDataAndType(uri, type);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.device_sport_camera_record_success_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
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
        //showDialog(false);
        FunSupport.getInstance().requestDeviceLogin(mFunDevice);
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

        // 打开声音
        mFunVideoView.setMediaSound(true);

    }

    private void stopMedia() {
        if (null != mFunVideoView) {
            mFunVideoView.stopPlayback();
            mFunVideoView.stopRecordVideo();
        }
    }

    private void pauseMedia() {
        if (null != mFunVideoView) {
            mFunVideoView.pause();
        }
    }

    private void resumeMedia() {
        if (null != mFunVideoView) {
            mFunVideoView.resume();
        }
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

    private Handler mHandler = new Handler();

    private void startTalk() {
        if (mTalkManager != null && mFunVideoView != null) {
            mTalkManager.onStartThread();
            mTalkManager.setTalkSound(false);
        }
    }

    private void stopTalk(int delayTime) {
        if (mTalkManager != null && mFunVideoView != null) {
            mTalkManager.onStopThread();
            mTalkManager.setTalkSound(true);
        }
    }

    private void openVoiceChannel(){
        mFunVideoView.setMediaSound(false);			//关闭本地音频

        mTalkManager.onStartTalk();
        mTalkManager.setTalkSound(true);
    }

    private void closeVoiceChannel(){
        mTalkManager.onStopTalk();
        mFunVideoView.setMediaSound(true);
        //mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);
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
                if (null != mFunDevice) {
                    NativeLoginPsw = editText;

                    onDeviceSaveNativePws();

                    // 重新登录
                    loginDevice();
                }
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
        FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),
                NativeLoginPsw);
        // 库函数方式本地保存密码
        if (FunSupport.getInstance().getSaveNativePassword()) {
            FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", NativeLoginPsw);
            // 如果设置了使用本地保存密码，则将密码保存到本地文件
        }
    }

    @Override
    public void onDeviceLoginSuccess(final FunDevice funDevice) {
        if (null != mFunDevice && null != funDevice) {
            if (mFunDevice.getId() == funDevice.getId()) {

                // 登录成功后立刻获取SystemInfo
                // 如果不需要获取SystemInfo,在这里播放视频也可以:playRealMedia();
                requestSystemInfo();
            }
        }
    }

    @Override
    public void onDeviceLoginFailed(final FunDevice funDevice, final Integer errCode) {
        // 设备登录失败
        //hideWaitDialog();
        //showToast(FunError.getErrorStr(errCode));
        //dialogDismiss();

        // 如果账号密码不正确,那么需要提示用户,输入密码重新登录
        if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
            showInputPasswordDialog();
        }
    }

    @Override
    public void onDeviceGetConfigSuccess(final FunDevice funDevice, final String configName, final int nSeq) {
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
            if (mFunDevice.isSupportPTZ()) {
                requestPTZPreset();
            }
        }
        else if (configName.equals(DevCmdOPSCalendar.CONFIG_NAME)) {
            DevCmdOPSCalendar calendar = (DevCmdOPSCalendar) funDevice.getConfig(DevCmdOPSCalendar.CONFIG_NAME);
            recordDateList.clear();
            for (int i = 0; i < calendar.getData().size(); i++) {
                recordDateList.add(calendar.getData().get(i).getDispDate());
            }
            recordDateAdapter.setThisPosition(recordDateList.size() - 1);
            recordDateAdapter.notifyDataSetChanged();
            rvPlaybackDate.scrollToPosition(recordDateAdapter.getItemCount() - 1);
        }
        mFunDevice.invalidConfig(DevCmdOPSCalendar.CONFIG_NAME);
    }

    @Override
    public void onDeviceGetConfigFailed(final FunDevice funDevice, final Integer errCode) {
        //showToast(FunError.getErrorStr(errCode));
        if (errCode == -11406) {
            funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
        }
    }


    @Override
    public void onDeviceSetConfigSuccess(final FunDevice funDevice, final String configName) {

    }


    @Override
    public void onDeviceSetConfigFailed(final FunDevice funDevice, final String configName, final Integer errCode) {
    }

    @Override
    public void onDeviceChangeInfoSuccess(final FunDevice funDevice) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceChangeInfoFailed(final FunDevice funDevice, final Integer errCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceOptionSuccess(final FunDevice funDevice, final String option) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceOptionFailed(final FunDevice funDevice, final String option, final Integer errCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice) {


    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {
        dialogDismiss();

        if (null != funDevice
                && null != mFunDevice
                && funDevice.getId() == mFunDevice.getId()) {
            recordDatas = datas;

            if (datas.length == 0) {
                showToast("device_camera_video_list_empty");
            } else {
                showToast("rec files size" + datas.length);
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

        if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
                || FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
            // 不支持高清码流,设置为标清码流重新播放
            if (null != mFunVideoView) {
                mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
                playRealMedia();
            }
        }

        return true;
    }


    @Override
    public boolean onInfo(MediaPlayer arg0, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mTextVideoStat.setText(R.string.media_player_buffering);
            mTextVideoStat.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mTextVideoStat.setVisibility(View.GONE);
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
        showDialog(true);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int time[] = { calendar.get(Calendar.YEAR),
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
        mFunVideoView.setMediaSound(true);
    }
}
