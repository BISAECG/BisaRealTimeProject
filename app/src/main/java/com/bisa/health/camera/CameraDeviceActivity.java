package com.bisa.health.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.lib.funsdk.support.FunDevicePassword;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunLog;
import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.OPPTZControl;
import com.bisa.health.camera.lib.funsdk.support.config.OPPTZPreset;
import com.bisa.health.camera.lib.funsdk.support.config.SystemInfo;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevType;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunStreamType;
import com.bisa.health.camera.lib.funsdk.support.utils.FileUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.TalkManager;
import com.bisa.health.camera.lib.funsdk.support.widget.FunVideoView;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.sdk.DialogInputPasswd;
import com.bisa.health.camera.sdk.UIFactory;
import com.bisa.health.model.User;
import com.lib.EPTZCMD;
import com.lib.FunSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import static com.bisa.health.camera.lib.funsdk.support.models.FunDevType.EE_DEV_SPORTCAMERA;

@SuppressLint("ClickableViewAccessibility")
public class CameraDeviceActivity extends BaseActivity implements View.OnClickListener,
        OnFunDeviceOptListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener {

    private ImageButton mBtnBack;

    private FunDevice mFunDevice = new FunDevice();

    private RelativeLayout mLayoutVideoWnd;
    private FunVideoView mFunVideoView;

    private LinearLayout mLayoutRecording;

    private TextView mTextStreamType;

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
    private AlertDialog alert;
    private AlertDialog.Builder builder;

    private String preset;
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
    private boolean recordingFlagBl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera_device);

        String devSn = getIntent().getStringExtra("FUN_DEVICE_SN");

        SharedPreferences sharedPref = getSharedPreferences("camera", Context.MODE_PRIVATE);
        String devJsonStr = sharedPref.getString(devSn, "");
        try {
            JSONObject jsonObject = new JSONObject(devJsonStr);
            mFunDevice.initWith(jsonObject);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        if (null == mFunDevice) {
            finish();
            return;
        }

        mBtnBack = findViewById(R.id.ibtn_camera_device_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLayoutVideoWnd = findViewById(R.id.layoutPlayWnd);

        mLayoutRecording = findViewById(R.id.layout_recording);

        llCtr = findViewById(R.id.ll_camera_ctr);

        mIbtnPlay = findViewById(R.id.ibtn_camera_play);
        mIbtnMute = findViewById(R.id.ibtn_camera_mute);
        mIbtnChannels = findViewById(R.id.ibtn_camera_channels);
        mIbtnFluency = findViewById(R.id.ibtn_camera_fluency);
        mIbtnFullscreen = findViewById(R.id.ibtn_camera_fullscreen);

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
                mFunVideoView.setMediaPlayHD();
                fluencyBottomDialog.dismiss();
            }
        });
        btnSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIbtnFluency.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_sd_sim));
                mFunVideoView.setMediaPlaySD();
                fluencyBottomDialog.dismiss();
            }
        });
        btnLD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIbtnFluency.setImageDrawable(getResources().getDrawable(R.drawable.camera_ctr_ld_sim));
                mFunVideoView.setMediaPlayLD();
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
                    stopTalk(0);
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
        mPtz_left.setOnTouchListener(onPtz_left);
        mPtz_right.setOnTouchListener(onPtz_right);

        //mLayoutControls = (LinearLayout) findViewById(R.id.layoutFunctionControl);
        //mLayoutChannel = (LinearLayout) findViewById(R.id.layoutChannelBtn);

        mFunVideoView = (FunVideoView) findViewById(R.id.funVideoView);


        mFunVideoView.setOnTouchListener(new OnVideoViewTouchListener());
        mFunVideoView.setOnPreparedListener(this);
        mFunVideoView.setOnErrorListener(this);
        mFunVideoView.setOnInfoListener(this);

        mTextStreamType = (TextView) findViewById(R.id.textStreamStat);


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
    }


    @Override
    protected void onDestroy() {

        stopMedia();

        FunSupport.getInstance().removeOnFunDeviceOptListener(this);

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
    protected void onResume() {

        if (mCanToPlay) {
            playRealMedia();
        }
//			 resumeMedia();

        super.onResume();
    }


    @Override
    protected void onPause() {

        stopTalk(0);
        closeVoiceChannel();

        stopMedia();
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


    @Override
    public void onClick(View v) {
        if (v.getId() >= 1000 && v.getId() < 1000 + mChannelCount) {
            mFunDevice.CurrChannel = v.getId() - 1000;
            mFunVideoView.stopPlayback();
            playRealMedia();
        }
        switch (v.getId()) {
            case 1101: {
                startDevicesPreview();
            }
            break;

            //case R.id.btnStop: // 停止播放
            //{
                //stopMedia();
            //}
            //break;
            //case R.id.btnStream: // 切换码流
            //{
                //switchMediaStream();
            //}
            //break;
            //case R.id.Btn_Talk_Switch:
            //{
                //OpenVoiceChannel();
            //}
            //break;
            //case R.id.btn_quit_voice:
            //{
                //CloseVoiceChannel(500);
           // }
            //break;
            //case R.id.btnDevCapture: // 远程设备图像列表
            //{
                //startPictureList();
            //}
            //break;
            //case R.id.btnDevRecord: // 远程设备录像列表
            //{
                //startRecordList();
            //}
            //break;
            //case R.id.btnFullscreen: // 横竖屏切换
            //{
                //switchOrientation();
            //}
            //break;
            //case R.id.btnSettings: // 系统设置/系统信息
            //{
                //startDeviceSetup();
            //}
            //break;


            //case R.id.btnFishEyeInfo:
            //{
                // 显示鱼眼信息
                //showFishEyeInfo();
            //}
            //break;
            default:
                break;
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

    /**
     * 视频截图,并延时一会提示截图对话框
     */
    private void tryToCapture() {
        if (!mFunVideoView.isPlaying()) {
            showToast(getResources().getString(R.string.media_capture_failure_need_playing));
            return;
        }

        final String path = mFunVideoView.captureImage(fileDir + System.currentTimeMillis() + ".jpg");	//图片异步保存

        if (!TextUtils.isEmpty(path)) {
            Message message = new Message();
            message.what = MESSAGE_TOAST_SCREENSHOT_PREVIEW;
            message.obj = path;
            mHandler.sendMessageDelayed(message, 200);			//此处延时一定时间等待图片保存完成后显示，也可以在回调成功后显示
        }
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
                                FunLog.e("test", "------------startActivity------" + uri.toString());
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
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = ViewGroup.LayoutParams.MATCH_PARENT;
        // lpWnd.removeRule(RelativeLayout.BELOW);
        //lpWnd.topMargin = 0;
        mLayoutVideoWnd.setLayoutParams(lpWnd);

    }

    private void showAsPortrait() {
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 视频显示为小窗口
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = UIFactory.dip2px(this, 240);
        //lpWnd.topMargin = UIFactory.dip2px(this, 48);
        // lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
        mLayoutVideoWnd.setLayoutParams(lpWnd);

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

    /**
     * 打开设备配置
     */
    private void startDeviceSetup() {
        Intent intent = new Intent();
        intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
        //intent.setClass(this, ActivityGuideDeviceSetup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /***
     * 打开 多通道预览
     */
    private void startDevicesPreview(){
        Intent intent = new Intent();
        intent.putExtra("FUNDEVICE_ID", mFunDevice.getId());
        //intent.setClass(this, ActivityGuideDevicePreview.class);
        startActivityForResult(intent, 0);
    }

    private class OnVideoViewTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            System.out.println("TTT-->>> event = " + event.getAction());
            if (event.getAction() == MotionEvent.ACTION_UP) {

            }

            return false;
        }

    }

    private void loginDevice() {
        //showWaitDialog();
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

    private void startPictureList() {
        Intent intent = new Intent();
        intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
        intent.putExtra("FILE_TYPE", "jpg");
        if (mFunDevice.devType == EE_DEV_SPORTCAMERA) {
            //intent.setClass(this, ActivityGuideDeviceSportPicList.class);
        } else {
            //intent.setClass(this, ActivityGuideDevicePictureList.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startRecordList() {
        Intent intent = new Intent();
        intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
        intent.putExtra("FILE_TYPE", "h264;mp4");
        //intent.setClass(this, ActivityGuideDeviceRecordList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

        // 设置当前播放的码流类型
        if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
            //mTextStreamType.setText(R.string.media_stream_secondary);
        } else {
            //mTextStreamType.setText(R.string.media_stream_main);
        }
    }
    // 添加通道选择按钮
    @SuppressWarnings("ResourceType")
    private void addChannelBtn(int channelCount) {

        int m = UIFactory.dip2px(this, 5);
        int p = UIFactory.dip2px(this, 3);
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams layoutParamsT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsT.setMargins(m, m, m, m);
        textView.setLayoutParams(layoutParamsT);
        textView.setText(R.string.device_opt_channel);
        textView.setTextSize(UIFactory.dip2px(this, 10));
        textView.setTextColor(getResources().getColor(R.color.theme_color));
        //mLayoutChannel.addView(textView);

        Button bt = new Button(this);
        bt.setId(1101);
        bt.setTextColor(getResources().getColor(R.color.theme_color));
        bt.setPadding(p, p, p, p);
        bt.setLayoutParams(layoutParamsT);
        bt.setText(R.string.device_camera_channels_preview_title);
        bt.setOnClickListener(this);
        //mLayoutChannel.addView(bt);

        for (int i = 0; i < channelCount; i++) {
            Button btn = new Button(this);
            btn.setId(1000 + i);
            btn.setTextColor(getResources().getColor(R.color.theme_color));
            btn.setPadding(p, p, p, p);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(UIFactory.dip2px(this, 40),
                    UIFactory.dip2px(this, 40));
            layoutParams.setMargins(m, m, m, m);
            btn.setLayoutParams(layoutParams);
            btn.setText(String.valueOf(i));
            btn.setOnClickListener(this);
            //mLayoutChannel.addView(btn);
        }

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

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PLAY_MEDIA:
                {
                    playRealMedia();
                }
                break;
                case MESSAGE_AUTO_HIDE_CONTROL_BAR:
                {
                    //hideVideoControlBar();
                }
                break;
                case MESSAGE_TOAST_SCREENSHOT_PREVIEW:
                {
                    String path = (String) msg.obj;
                    toastScreenShotPreview(path);
                }
                break;
                case MESSAGE_OPEN_VOICE:
                {
                    mFunVideoView.setMediaSound(true);
                }
                default:
                    break;
            }
        }
    };

    private View.OnTouchListener mIntercomTouchLs = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            try {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    startTalk();
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    stopTalk(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    private void startTalk() {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
            mTalkManager.onStartThread();
            mTalkManager.setTalkSound(false);
        }
    }

    private void stopTalk(int delayTime) {
        if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
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

    private void showFishEyeInfo() {
        if ( null != mFunVideoView ) {
            String fishEyeInfo = mFunVideoView.getFishEyeFrameJSONString();
            Intent intent = new Intent();
            //intent.setClass(this, ActivityDeviceFishEyeInfo.class);
            intent.putExtra("FISH_EYE_INFO", fishEyeInfo);
            intent.putExtra("DEVICE_SN", mFunDevice.getDevSn());
            this.startActivity(intent);
        }
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
        System.out.println("TTT---->>>> loginsuccess");

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

                addChannelBtn(channelCount);
            }

            //hideWaitDialog();

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
        } else if (OPPTZPreset.CONFIG_NAME.equals(configName)) {

        } else if (OPPTZControl.CONFIG_NAME.equals(configName)) {
            Toast.makeText(getApplicationContext(), R.string.user_set_preset_succeed, Toast.LENGTH_SHORT).show();

            // 重新获取预置点列表
//			requestPTZPreset();
        }
    }

    private String getType(int i){
        switch (i) {
            case 0:
                return "P2P";
            case 1:
                return "Forward";
            case 2:
                return "IP";
            case 5:
                return "RPS";
            default:
                return "";
        }
    }

    @Override
    public void onDeviceGetConfigFailed(final FunDevice funDevice, final Integer errCode) {
        //showToast(FunError.getErrorStr(errCode));
        if (errCode == -11406) {
            funDevice.invalidConfig(OPPTZPreset.CONFIG_NAME);
        }
    }


    @Override
    public void onDeviceSetConfigSuccess(final FunDevice funDevice,
                                         final String configName) {

    }


    @Override
    public void onDeviceSetConfigFailed(final FunDevice funDevice,
                                        final String configName, final Integer errCode) {
        if (OPPTZControl.CONFIG_NAME.equals(configName)) {
            Toast.makeText(getApplicationContext(), R.string.user_set_preset_fail, Toast.LENGTH_SHORT).show();
        }
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }


    @Override
    public void onPrepared(MediaPlayer arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // 播放失败
        //showToast(getResources().getString(R.string.media_play_error)
                //+ " : "
                //+ FunError.getErrorStr(extra));

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
                    Log.i("test", "onPtz_up -- KeyEvent.ACTION_DOWN");
                    bstop = false;
                    nPTZCommand = EPTZCMD.TILT_UP;
                    break;
                case KeyEvent.ACTION_UP:
                    Log.i("test", "onPtz_up -- KeyEvent.ACTION_UP");
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
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        mFunDevice.CurrChannel = arg1;
        System.out.println("TTTT----"+mFunDevice.CurrChannel);
        if (mCanToPlay) {
            playRealMedia();
        }
    }


    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {
        // TODO Auto-generated method stub

    }
}
