package com.bisa.health.cust;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.bisa.health.ecg.UiUpCallShowView;
import com.bisa.health.utils.DisplayUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SecondSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	/**
	 * 是否处于绘制状态
	 */
	private static boolean mIsDrawing = false;

	/**
	 * 是否处于绘制状态
	 */
	private static boolean IsDrawingClean = false;
	/**
	 * 帮助类
	 */
	private static SurfaceHolder mHolder;
	/**
	 * 画布
	 */
	private static Canvas mCanvas;
	/**
	 * 路径
	 */
	private Path mPath;
	/**
	 * 画笔
	 */
	private static Paint mPaint;

	/**
	 * 心电图3通道平均高度
	 */
	private int ChHeight;

	/**
	 * Y轴的平均点
	 */
	private float SCALE_AVG_Y;

	/**
	 * X轴的平均点
	 */
	private float SCALE_AVG_X;

	/**
	 * 数据长度
	 */
	private float DAT_LENGTH;


	/**
	 * 3通道的偏移量
	 */
	private float CH1_Y_OFFSET = 0;
	private float CH2_Y_OFFSET = 0;
	private float CH3_Y_OFFSET = 0;

	/**
	 * X轴点数的偏移量
	 */
	private float X1_ACG;

	/**
	 * 画布逐点锁定坐标值
	 */
	private float lock_x = 0;
	private float _lock_x = 0;
	private float lock_y = 0;
	/**
	 * 三通道XY坐标点
	 */
	private float IoldX = 0;
	private float IoldY2 = 0;
	private float IoldY1 = 0;
	private float IoldY3 = 0;

	private static int _width=0;
	private static int _height=0;
	 private static  UiUpCallShowView NULL_CALLBACK;
	/**
	 * 画图数据
	 */
	private static final BlockingQueue<int[]> queue = new LinkedBlockingQueue<int[]>(10);

	private static int iCount = 0;

	private String TAG = "SecondSurfaceView";

	public SecondSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initView();
	}

	public SecondSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SecondSurfaceView(Context context) {
		super(context);
		initView();
	}
	
	public synchronized void setNullCallBack(UiUpCallShowView CALLBACK){
		NULL_CALLBACK=CALLBACK;
	}
	
	private void initView() {
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		//setFocusable(true);
		//setFocusableInTouchMode(true);
		// this.setKeepScreenOn(true);

		// mPath=new Path();
		mPaint = new Paint();
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(3f);

		ChHeight = getHeight() / DisplayUtil.CHCOUNT;
		SCALE_AVG_Y = ChHeight / DisplayUtil.SCALE_Y;
		SCALE_AVG_X = getWidth() / SCALE_AVG_Y;// x轴数量
		DAT_LENGTH = SCALE_AVG_X / 5;
		int btufferLength = (int) Math.ceil(DAT_LENGTH) * 250;
		X1_ACG = btufferLength / (float) getWidth();
		iCount=0;
		CH1_Y_OFFSET = getHeight() - ChHeight * 4;
		CH2_Y_OFFSET = getHeight() - ChHeight * 3;
		CH3_Y_OFFSET = getHeight() - ChHeight * 2;
		_width=getWidth();
		_height=getHeight();
	
	}

	public synchronized void initView(int mWidth, int mHeight, ViewGroup.LayoutParams layoutParams) {


		super.setLayoutParams(layoutParams);

		ChHeight = mHeight / DisplayUtil.CHCOUNT;
		SCALE_AVG_Y = ChHeight / DisplayUtil.SCALE_Y;
		SCALE_AVG_X =mWidth / SCALE_AVG_Y;// x轴数量
        DAT_LENGTH = SCALE_AVG_X / 5;
		int btufferLength = (int) Math.ceil(DAT_LENGTH) * 250;
		X1_ACG = btufferLength / (float) mWidth;
		iCount=0;
		CH1_Y_OFFSET = mHeight- ChHeight * 4;
		CH2_Y_OFFSET = mHeight - ChHeight * 3;
		CH3_Y_OFFSET = mHeight - ChHeight * 2;
		_width=mWidth;
		_height=mHeight;
	}

	public synchronized void push(int[] v) {
		queue.offer(v);
	} // push

	public synchronized int[] pop() {
		return queue.poll();
	}

	public synchronized void mIsDrawing(boolean isDrawing){
		mIsDrawing=isDrawing;
		Log.i("----", "DRAW STOP");
	}
	
	@Override
	public void run() {
		initView();
		while (mIsDrawing) {
			int[] value = pop();
			// Log.i(TAG, "value:"+(value==null));
			if (value == null) {
				continue;
			}

			WaveDraw(value);
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		Log.i("----", "START DRAW");
		queue.clear();
		mIsDrawing = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mIsDrawing = false;
		queue.clear();
		Log.i("----", "START END");
	}

	public synchronized void WaveDraw(int[] ecgValue) {


		 if(lock_x==0){
		 _lock_x=(ecgValue.length/X1_ACG);
		 }else{
		 _lock_x=(IoldX+(ecgValue.length/X1_ACG));
		 }


		mCanvas = mHolder.lockCanvas(new Rect((int)lock_x, (int)lock_y, (int)_lock_x, _height));

		if (mCanvas == null) {
			return;
		}

		for (int j = 0; j < ecgValue.length; j++) {

			float x = (iCount++) / X1_ACG;
			int v = ecgValue[j];

			int v1 = (v / 65536)+100;
			int v2 = (v & 0x0000FFFF)+100;
			int v3 = (v2 - v1 + 500)+100;



			NULL_CALLBACK.uiBpmCallBack(v2,v1,v3);

			float y1 = _height - ((_height * v1) / 1200);
			float y2 = _height - ((_height * v2) / 1200);
			float y3 = _height - ((_height * v3) / 1200);


			if (x > (ecgValue.length/X1_ACG)*3) {

				mPaint.setColor(Color.parseColor("#cfe2f3"));
				mCanvas.drawLine(IoldX, IoldY1 + CH1_Y_OFFSET, x, y1 + CH1_Y_OFFSET, mPaint);
				mPaint.setColor(Color.GREEN);
				mCanvas.drawLine(IoldX, IoldY2 + CH2_Y_OFFSET, x, y2 + CH2_Y_OFFSET, mPaint);
				mPaint.setColor(Color.WHITE);
				mCanvas.drawLine(IoldX, IoldY3 + CH3_Y_OFFSET, x, y3 + CH3_Y_OFFSET, mPaint);
			}

			IoldX = x;
			IoldY2 = y2;
			IoldY1 = y1;
			IoldY3 = y3;
			lock_x = x;


			if (IoldX > _width) {

				IoldX=1;
				lock_x = 0;
				lock_y = 0;
				iCount = 0;
				IsDrawingClean = true;
				break;
			}

		}
		mHolder.unlockCanvasAndPost(mCanvas);// unlock

		if (IsDrawingClean) {
			IsDrawingClean = false;
			cleanBg();
		}

	}

	/**
	 * 清除内容
	 */
	public void cleanBg() {

		mCanvas = mHolder.lockCanvas();
		if (mCanvas == null) {
			return;
		}
//		mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
//		mCanvas.drawPaint(mPaint);  
//        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC)); 
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);  
	//	mCanvas.drawColor(Color.BLACK, Mode.CLEAR);
		mHolder.unlockCanvasAndPost(mCanvas);// unlock
	}
}
