package com.bisa.health.camera.lib.funsdk.support.widget;

import android.content.Context;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.opengl.GLSurfaceView;

import android.util.AttributeSet;

import android.widget.LinearLayout;

import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunStreamType;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;

import com.lib.FunSDK;
import com.video.opengl.GLSurfaceView20;


//import com.android.gl2jni.GL2JNIView;

public class PreviewFunVideoView extends LinearLayout {

	private final String TAG = "FunVideoView";

	private final int STAT_STOPPED = 0;
	private final int STAT_PLAYING = 1;

	private int mPlayStat = STAT_STOPPED;
	private FunStreamType mStreamType = FunStreamType.STREAM_SECONDARY;
	private String mVideoUrl = null;
	private int mPlayerHandler = 0;
	private int mUserID = -1;
	private GLSurfaceView mSufaceView = null;
	private boolean mInited = false;


    private int mPlayPosition = 0;
    private boolean mIsPrepared = false;
    private int mChannel = 0;

    private OnPreparedListener mPreparedListener = null;
    private OnCompletionListener mCompletionListener = null;
    private OnErrorListener mErrorListener = null;
    private OnInfoListener mInfoListener = null;

    private float FistXLocation;
    private float FistYlocation;
    private boolean Istrigger;
    private final int LENTH = 1;
    private long time;
    private Context mContext;

    // 是否已经调用过底层接口播放了
	private boolean mIsPlaying = false;



	public PreviewFunVideoView(Context context) {
		super(context);
        mContext = context;
		init();
	}

	public PreviewFunVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
        mContext = context;
		init();
	}

	public PreviewFunVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mContext = context;
		init();
	}

	public void setOnPreparedListener(OnPreparedListener listener) {
		mPreparedListener = listener;
	}


	public void setOnCompletionListener(OnCompletionListener listener) {
		mCompletionListener = listener;
	}

	public void setOnInfoListener(OnInfoListener listener) {
		mInfoListener = listener;
	}


	private void init() {
        if (!isInEditMode()) {
            if ( mUserID == -1 ) {
            	mUserID = FunSupport.getInstance().getHandler();
            }

            mIsPlaying = false;

			//FunSupport.getInstance().registerFunVideoViewListener(this);
        }
	}

	public GLSurfaceView getSufaceView() {
		return mSufaceView;
	}

	private void initSurfaceView() {

		if ( null == mSufaceView ) {
			// 1. 普通摄像头视频播放
     		mSufaceView = new GLSurfaceView20(getContext());
     		//mSufaceView.setLongClickable(true);

             LayoutParams lp = new LayoutParams(
                     LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
             this.addView(mSufaceView, lp);
            //不加下面这句，部分7.0 以上系统不布局及绘制自定义的SufaceView
            mSufaceView.requestLayout();
         }
	}


	private void release() {
		stopPlayback();

		if ( -1 != mUserID ) {
			//FunSupport.getInstance().removeFunVideoViewListener(this);

			mUserID = -1;
		}

		mSufaceView = null;
	}


	/**
	 * 设置播放地址
	 * @param path
	 */
	public void setVideoPath(String path) {
		mVideoUrl = path;
		mPlayStat = STAT_PLAYING;
		openVideo();
	}
	
	/**
	 * 通过设备的IP(如果是AP连接设备)或者设备的序列号SN(如果是互联网连接设备)播放设备视频
	 * @param devSn 设备的IP(AP连接时)或者设备SN(互联网连接时)
	 */
	public void setRealDevice(String devIp, int channel) {
		String playUrl = null;
		mChannel = channel;
		if ( MyUtils.isIp(devIp) ) {
			// 如果传入的IP地址,需要添加端口
			playUrl = "real://" + devIp + ":34567";
		} else {
			playUrl = "real://" + devIp;
		}
		setVideoPath(playUrl);
	}


	
	/**
	 * 停止视频播放
	 */
	public void stopPlayback() {
		if ( mPlayerHandler != 0 ) {
			FunSDK.MediaStop(mPlayerHandler);
			mPlayerHandler = 0;
		}
		mVideoUrl = null;
		mIsPlaying = false;
	}

	
	/**
	 * 获取当前播放进度,单位秒
	 * @return
	 */
	public int getPosition() {
		return mPlayPosition;
	}

	
	private String getPlayPath(String url) {
		if ( url.contains("://") ) {
			return url.substring(mVideoUrl.indexOf("://") + 3);
		}
		return url;
	}
	
	private void openVideo() {
		if ( !mInited
				|| null == mVideoUrl 
				|| mPlayStat != STAT_PLAYING
				|| null == mSufaceView ) {
			return;
		}
		
		mIsPrepared = false;
		mPlayPosition = 0;
		
		String playPath = getPlayPath(mVideoUrl);
		if ( mVideoUrl.startsWith("real://") ) {
			if ( !mIsPlaying ) {
				// 播放实时视频
				mPlayerHandler = FunSDK.MediaRealPlay(
						mUserID,
						playPath,
						mChannel,
						mStreamType.getTypeId(), mSufaceView, 0);
//                FunSDK.SetIntAttr(mPlayerHandler, EFUN_ATTR.EOA_SET_MEDIA_DATA_USER, getUserId());
//                FunSDK.MediaSetFluency(mPlayerHandler, SDKCONST.EDECODE_TYPE.EDECODE_REAL_TIME_STREAM0, 0); //设置流畅度（实时<-->流畅）
			}
			mIsPlaying = true;
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
        if (changed || !mInited) {
        	
        	initSurfaceView();
        	
            mInited = true;

            if ( mPlayStat == STAT_PLAYING
                    && null != mVideoUrl ) {
                openVideo();
            }
        }
	}

	
	@Override
	protected void onDetachedFromWindow() {
		release();
		super.onDetachedFromWindow();
	}



    public void setMediaSound(boolean bSound) {
        FunSDK.MediaSetSound(mPlayerHandler, bSound ? 100 : 0, 0);
    }


}
