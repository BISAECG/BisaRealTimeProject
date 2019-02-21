package com.bisa.health.cust.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.bisa.health.ecg.UiUpCallShowView;
import com.bisa.health.ecg.config.ECGViewConfig;

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
    private  Canvas mCanvas;
    /**
     * 路径
     */
    private Path mPath;
    /**
     * 画笔
     */
    private  Paint mPaint;


    /**
     * 画布逐点锁定坐标值
     */
    private float lock_x = 0;
    private float _lock_x = 0;



    /**
     * 三通道XY坐标点
     */
    private float IoldX = 0;
    private float IoldY2 = 0;
    private float IoldY1 = 0;
    private float IoldY3 = 0;

    private  int _width=0;
    private  int _height=0;
    private UiUpCallShowView NULL_CALLBACK;

    /**
     * 画图数据
     */
    private static final BlockingQueue<int[]> queue = new LinkedBlockingQueue<int[]>(10);

    private int iCount = 0;

    private String TAG = "----";

    public SecondSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView(getWidth(),getHeight());
    }

    public SecondSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(getWidth(),getHeight());
    }

    public SecondSurfaceView(Context context) {
        super(context);
        initView(getWidth(),getHeight());
    }

    public synchronized void setNullCallBack(UiUpCallShowView CALLBACK){
        NULL_CALLBACK=CALLBACK;
    }
    public void initView(int width, int height, ViewGroup.LayoutParams layoutParams) {
        super.setLayoutParams(layoutParams);
        initView(width,height);
    }
    private void initView(int width, int height) {
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
        _width=width;
        _height=height;
         mHeight=height/3;
         ecgDrawMinUnitWidth=mHeight/8;//每个格子的宽度
         ecgDrawHeight=ecgDrawMinUnitWidth*20;//画布的实际高度
         scaleAvg=ecgDrawHeight/ECGViewConfig.ECGDefaultMaxPoint;//画布正比例数据平均数
         drawMidNumber=mHeight/2;//画布中间像素
         unitXMinHeight=ecgDrawMinUnitWidth/5;//计算一个格子的像素
         oneSecondWidth=unitXMinHeight*ECGViewConfig.ECGDefaultOneSecond;//1秒钟的实际像素
         xFlag=oneSecondWidth/ECGViewConfig.RATE;
         int mLength= (int) (width*xFlag);
    }
    float ecgDrawMinUnitWidth;
    float ecgDrawHeight;
    float scaleAvg;
    float drawMidNumber;
    float unitXMinHeight;
    float oneSecondWidth;
    float xFlag;
    float mHeight;
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
        initView(getWidth(),getHeight());
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


        try {

            if(lock_x==0){
                cleanBg();
                _lock_x=(ecgValue.length*xFlag);
            }else{
                _lock_x=(IoldX+(ecgValue.length*xFlag));
            }
            mCanvas = mHolder.lockCanvas(new Rect((int)lock_x,0, (int)_lock_x, _height));
            if (mCanvas == null) {
                return;
            }
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            for (int j = 0; j < ecgValue.length; j++) {

                float x = (++iCount)*xFlag;
                int v = ecgValue[j];

                int v1 = (v / 65536);
                int v2 = (v & 0x0000FFFF);
                int v3 = (v2 - v1 + 500);

                NULL_CALLBACK.uiBpmCallBack(v2,v1,v3);
                float ecgPoint1=(v1-ECGViewConfig.ECGMidNumber)*scaleAvg;
                float ecgPoint2=(v2-ECGViewConfig.ECGMidNumber)*scaleAvg;
                float ecgPoint3=(v3-ECGViewConfig.ECGMidNumber)*scaleAvg;
                float y1=drawMidNumber-ecgPoint1;
                float y2=drawMidNumber+mHeight-ecgPoint2;
                float y3=drawMidNumber+(mHeight*2)-ecgPoint3;
                if (iCount > 2) {
                mPaint.setColor(Color.parseColor("#cfe2f3"));
                mCanvas.drawLine(IoldX, IoldY1, x, y1, mPaint);

                mPaint.setColor(Color.GREEN);
                mCanvas.drawLine(IoldX, IoldY2, x, y2, mPaint);

                mPaint.setColor(Color.WHITE);
                mCanvas.drawLine(IoldX, IoldY3, x, y3, mPaint);
                }

                IoldX = x;
                IoldY2 = y2;
                IoldY1 = y1;
                IoldY3 = y3;
                lock_x = x;

                if (IoldX > _width) {
                    IoldX=0;
                    lock_x = 0;
                    iCount = 0;
                    break;
                }

            }


        }catch (Exception e){

        }finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);// unlock
            }
        }



//        if (IsDrawingClean) {
//            IsDrawingClean = false;
//            cleanBg();
//        }

    }


    /**
     * 清除内容
     */
    public void cleanBg() {

        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas(null);
            //		mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
//		mCanvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            //	mCanvas.drawColor(Color.BLACK, Mode.CLEAR);
        }catch (Exception e) {
            Log.e(TAG,e.getMessage() );
        }finally {
            if(canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }

    }
}
