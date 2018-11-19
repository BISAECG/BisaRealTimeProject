package com.bisa.health.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2017/8/3.
 */

public class DiaLogUtil {

    /**
     * 以百分比控制对话框大小
     *
     * @param dialog
     * @param activity
     * @param percent
     */
    public static void DialogShow(Dialog dialog, Activity activity,int percent){
        if(!activity.isFinishing()&&!dialog.isShowing()) {
            dialog.show();
            if(percent!=-1){
                DisplayMetrics dm = DisplayUtil.getDisplay(activity);
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = dm.widthPixels - (dm.widthPixels / percent); //设置宽度
                lp.height = dm.heightPixels - (dm.heightPixels / percent);
                dialog.getWindow().setAttributes(lp);
            }

        }
    }

    public static void DialogDismiss(Dialog dialog, Activity activity){
        if(!activity.isFinishing()&&dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void DialogDismiss(DialogInterface dialog, Activity activity){
        if(!activity.isFinishing()) {
            dialog.dismiss();
        }
    }

    public static void showPopupWindow(final Context context) {
        // 获取WindowManager
        final WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置flag
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_killout_layout, null);
        TextView tv_itemdialog_ok = (TextView) mView.findViewById(R.id.positiveButton);
        tv_itemdialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏弹窗
                mWindowManager.removeView(mView);
            }
        });


        mWindowManager.addView(mView, params);
    }

}
