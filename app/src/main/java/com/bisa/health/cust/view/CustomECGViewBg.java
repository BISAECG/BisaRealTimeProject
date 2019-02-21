package com.bisa.health.cust.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.bisa.health.ecg.config.ECGViewConfig;

public class CustomECGViewBg extends View {

    Paint paint = null;
    private int TIME_TEXT_OFFSET = 10;

    public CustomECGViewBg(Context context) {
        super(context);
    }

    public CustomECGViewBg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomECGViewBg(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(0xffEBEBEB);
            paint.setAntiAlias(true);
        }


        int width = this.getWidth();
        int height = this.getHeight();
        paint.setAlpha(255);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        Rect rect=new Rect(0,0,width,height);
        canvas.drawRect(rect,paint);
        paint.setStrokeWidth(1);
        float ecgDrawMinUnitWidth=height/(ECGViewConfig.CHCOUNT*ECGViewConfig.DrawDefaultScale);//刻度-2 2的格子数
        float unitXMinHeight=ecgDrawMinUnitWidth/5;//计算一个格子的像素
        paint.setAlpha(100);
        for(int y=0;y<=ECGViewConfig.DrawDefaultScale*ECGViewConfig.CHCOUNT*5;y++){
            canvas.drawLine(0, y*unitXMinHeight,width,  y*unitXMinHeight, paint);
        }
        paint.setAlpha(150);
        for(int y=0;y<=ECGViewConfig.DrawDefaultScale*ECGViewConfig.CHCOUNT*5;y++){
            if(y%5==0){
                canvas.drawLine(0, y*unitXMinHeight,width,  y*unitXMinHeight, paint);
            }

        }
        float xCount=width/unitXMinHeight;
        paint.setAlpha(50);
        for(int x=0;x<xCount;x++){
            canvas.drawLine(x*unitXMinHeight,0,x*unitXMinHeight, height, paint);

        }
        paint.setAlpha(150);
        for(int x=0;x<xCount;x++){
            if(x%5==0){
                canvas.drawLine(x*unitXMinHeight,0,x*unitXMinHeight, height, paint);
            }
        }


    }

}
