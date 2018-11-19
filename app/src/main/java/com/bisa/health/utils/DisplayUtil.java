package com.bisa.health.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * dp、sp 转换为 px 的工具类
 *
 * @author fxsky 2012.11.12
 *
 */
public class DisplayUtil {
	
	
	/**
	 * 文件长度
	 */
	
	public static final int ECG_LENGTH=15000;
	  
	/***
	 * 心电图参数
	 */
    public static final int CHCOUNT=3;// 通道数量
    public  static final int SCALE_Y=8;// Y轴数量
    public  static final int RATE=250;// Y轴数量
    public static final int ECG_ONE_SECOND=25;
    public static final int ECG_MAX_POINT=1000;//ecg最大点数
    public static final int ECG_MID_POINT=ECG_MAX_POINT/2;//ecg中间数
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param
     * @param
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip(Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int)scale;
    }
	
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int calcTextWidth(Paint paint, String demoText) {
        return (int) paint.measureText(demoText);
    }

    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    public static DisplayMetrics getDisplay(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }
}
