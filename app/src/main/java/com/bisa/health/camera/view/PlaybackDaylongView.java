package com.bisa.health.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.bisa.health.R;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;

public class PlaybackDaylongView extends View {
    private final String[] halfhours = {"00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00",
            "03:30", "04:00", "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00",
            "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00",
            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00",
            "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00",
            "23:30"};

    private final float SCALE_WIDTH_BIG = 3;//大刻度线宽度
    private final float SCALE_WIDTH_SMALL = 2;//小刻度线宽度
    private final float MID_LINE_WIDTH = 5;//指针线宽度


    private float maxScaleHeight;//大刻度长度
    private float minScaleHeight;//小刻度长度
    private float scaleSpace;//刻度间距
    private float scaleSpaceUnit;//每大格刻度间距
    private float height, width;//view高宽

    private float maxScaleStartY;//大刻度起始Y位置
    private float maxScaleStopY;
    private float minScaleStartY;//小刻度起始Y位置
    private float minScaleStopY;

    private int max;//最大刻度
    private int min;//最小刻度

    private float scaleWidth;//刻度尺宽度

    private float borderLeft, borderRight;//左右边界值坐标
    private float midX;//当前中心刻度x坐标
    private float originMidX;//初始中心刻度x坐标
    private float minX;//最小刻度x坐标,从最小刻度开始画刻度

    private float lastX;

    private float originValue;//初始刻度对应的值
    private float currentValue;//当前刻度对应的值

    private Paint paint;//画笔

    private Context context;

    private List<RecScaleValue> recordValueList = new ArrayList<>();//录像数据刻度值


    private VelocityTracker velocityTracker;//速度监测
    private float velocity;//当前滑动速度
    private float a = 500000;//加速度
    private boolean continueScroll;//是否继续滑动

    private boolean isMeasured;

    private OnValueChangeListener onValueChangeListener;


    public PlaybackDaylongView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PlaybackDaylongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public PlaybackDaylongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        //初始化画笔
        paint = new Paint();
        //paint.setAntiAlias(true);
        paint.setDither(true);

        isMeasured = false;
        min = 0;
        max = 48;

        scaleSpace = 30;
        scaleSpaceUnit = scaleSpace * 6;
        scaleWidth = max * scaleSpaceUnit;

        originValue = scaleWidth / 2;
        currentValue = originValue;
    }


    //设置value变化监听
    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();

            maxScaleHeight = height * 3 / 5;
            minScaleHeight = maxScaleHeight / 2;

            maxScaleStartY = (height - maxScaleHeight) / 3;
            maxScaleStopY = maxScaleStartY + maxScaleHeight;
            minScaleStartY = maxScaleStartY + ((maxScaleHeight - minScaleHeight) / 2);
            minScaleStopY = minScaleStartY + minScaleHeight;

            borderLeft = width / 2 - ((min + max) / 2 - min) * scaleSpaceUnit;
            borderRight = width / 2 + ((min + max) / 2 - min) * scaleSpaceUnit;

            midX = width / 2;
            originMidX = midX;
            minX = borderLeft;

            isMeasured = true;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.camera_playback_scale));
        //画刻度线
        for (int i = min; i <= max; i++) {
            //画大刻度线
            paint.setStrokeWidth(SCALE_WIDTH_BIG);
            canvas.drawLine(minX + (i - min) * scaleSpaceUnit, maxScaleStartY, minX + (i - min) * scaleSpaceUnit, maxScaleStopY, paint);

            if (i == max) {
                break;//最后一条不画小刻度线
            }

            //画小刻度线
            paint.setStrokeWidth(SCALE_WIDTH_SMALL);
            for (int j = 1; j < 6; j++) {
                canvas.drawLine(minX + (i - min) * scaleSpaceUnit + scaleSpace * j, minScaleStartY,
                        minX + (i - min) * scaleSpaceUnit + scaleSpace * j, minScaleStopY, paint);
            }

            //画刻度数字，最后一条不画
            Rect rect = new Rect();
            String str = halfhours[i];
            paint.setTextSize(23);
            paint.getTextBounds(str, 0, str.length(), rect);
            float w = rect.width();
            canvas.drawText(str, minX + (i - min) * scaleSpaceUnit - w / 2 - SCALE_WIDTH_BIG / 2, maxScaleStopY + 23, paint);

        }


        //画指针线
        paint.setStrokeWidth(MID_LINE_WIDTH);
        paint.setColor(getResources().getColor(R.color.camera_playback_daylong_mid));
        canvas.drawLine(width / 2, 0, width / 2, height, paint);

        //画record记录
        if(recordValueList.size() > 0) {
            paint.setColor(getResources().getColor(R.color.camera_playback_record));
            //paint.setStyle(Paint.Style.FILL);
            for(RecScaleValue value : recordValueList) {
                //刻度1小格为5分钟，300秒，转换为float类型
                paint.setStrokeWidth(value.endValue - value.startValue);
                canvas.drawLine(minX + value.startValue, 0, minX + value.startValue, height, paint);
            }
        }
    }

    public void setData(H264_DVR_FILE_DATA[] datas) {
        recordValueList.clear();
        for(H264_DVR_FILE_DATA data : datas) {
            recordValueList.add(new RecScaleValue((data.getLongStartTime() / 300f) * scaleSpace, (data.getLongEndTime() / 300f) * scaleSpace));
        }
        invalidate();
    }

    public void setRecordScale(int index) {
        currentValue = recordValueList.get(index).startValue;
        midX = originMidX + (scaleWidth / 2 - currentValue);
        minX = midX - scaleWidth / 2;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                continueScroll = false;
                //初始化速度追踪
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                float offsetX = lastX - x;
                minX -= offsetX;
                midX -= offsetX;
                lastX = x;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                confirmBorder();
                //当前滑动速度
                velocityTracker.computeCurrentVelocity(1000);
                velocity = velocityTracker.getXVelocity();
                float minVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
                if (Math.abs(velocity) > minVelocity) {
                    continueScroll = true;
                    continueScroll();
                } else {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                lastX = x;
                calculateCurrentScale();
                break;
            case MotionEvent.ACTION_CANCEL:
                lastX = x;
                velocityTracker.recycle();
                velocityTracker = null;
                break;
        }
        return true;
    }

    //计算当前刻度
    private void calculateCurrentScale() {
        float offset = midX - originMidX;

        if (offset > scaleWidth / 2) {
            currentValue = scaleWidth;
        } else if (offset < - scaleWidth / 2) {
            currentValue = 0;
        } else {
            currentValue = originValue - offset;
        }

        if(onValueChangeListener != null && recordValueList.size() > 0) {
            //在录像的区域中选择，第一个、最后一个之间
            if(currentValue > recordValueList.get(0).startValue - scaleSpace / 2 && currentValue < recordValueList.get(recordValueList.size() - 1).endValue) {
                for(int i=0; i<recordValueList.size(); i++) {
                    if(currentValue < recordValueList.get(i).endValue) {
                        onValueChangeListener.playRecordFileWithIndex(i);
                        break;
                    }
                }
            }

        }
    }

    //指针线超出范围时 重置回边界处
    private void confirmBorder() {
        if (midX < borderLeft) {
            midX = borderLeft;
            minX = borderLeft - (borderRight - borderLeft) / 2;
            postInvalidate();
        } else if (midX > borderRight) {
            midX = borderRight;
            minX = borderLeft + (borderRight - borderLeft) / 2;
            postInvalidate();
        }
    }

    //手指抬起后继续惯性滑动
    private void continueScroll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                float velocityAbs = 0;//速度绝对值
                if (velocity > 0 && continueScroll) {
                    velocity -= 50;
                    minX += velocity * velocity / a;
                    midX += velocity * velocity / a;
                    velocityAbs = velocity;
                } else if (velocity < 0 && continueScroll) {
                    velocity += 50;
                    minX -= velocity * velocity / a;
                    midX -= velocity * velocity / a;
                    velocityAbs = -velocity;
                }
                calculateCurrentScale();
                confirmBorder();
                postInvalidate();
                if (continueScroll && velocityAbs > 0) {
                    post(this);
                } else {
                    continueScroll = false;
                }
            }
        }).start();
    }
}
