package com.bisa.health.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2018/4/12.
 */

public class BleConnDiaLogUtil {

    private static BleConnDiaLogUtil instance=null;
    private static Dialog dialog;;
    private BleConnDiaLogUtil(){}

    public View.OnClickListener mOnClickListener;

    public static BleConnDiaLogUtil getInstance() {

        if (instance == null) {
            synchronized (BleConnDiaLogUtil.class) {
                if (instance == null) {//2
                    instance = new BleConnDiaLogUtil();
                }
            }
        }
        return instance;
    }

    public BleConnDiaLogUtil  setOnClickBLEListener(@Nullable View.OnClickListener l){
        this.mOnClickListener=l;
        return this;
    }

    public void show(Activity activity,boolean isCancel){



        if(dialog!=null&&dialog.isShowing()){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.blerestartconn_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img_bleico);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_title);// 提示文字
        Button btn_connble=(Button) v.findViewById(R.id.btn_connble);
        btn_connble.setOnClickListener(this.mOnClickListener);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                activity, R.anim.rotate);

        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(activity.getResources().getString(R.string.ble_close_title));// 设置加载信息
        dialog= new Dialog(activity, R.style.loading_dialog);// 创建自定义样式dialog

        dialog.setCancelable(isCancel);// 不可以用“返回键”取消
        dialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局

        try {
            if(dialog!=null&&!activity.isFinishing())
                dialog.show();
        }catch (Exception e){
            return;
        }



    }
    public void dismiss(){

        try {
            if(dialog!=null)
                dialog.dismiss();
        }catch (Exception e){
            return;
        }
    }
}
