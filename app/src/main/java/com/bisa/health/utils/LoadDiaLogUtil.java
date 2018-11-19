package com.bisa.health.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bisa.health.R;

/**
 * Created by Administrator on 2018/4/12.
 */

public class LoadDiaLogUtil {

    private  static final String TAG = "LoadDiaLogUtil";
    private static LoadDiaLogUtil instance=null;
    private static Dialog loadDialog;;
    
    private LoadDiaLogUtil(){}

    public static LoadDiaLogUtil getInstance() {

        if (instance == null) {
            synchronized (LoadDiaLogUtil.class) {
                if (instance == null) {//2
                    instance = new LoadDiaLogUtil();
                }
            }
        }
        return instance;
    }

    public void show(Activity activity,boolean isCancel){

        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                activity, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        if(loadDialog!=null&&loadDialog.isShowing()){
            return;
        }

        loadDialog = new Dialog(activity, R.style.loading_dialog);// 创建自定义样式dialog
        loadDialog.setCancelable(isCancel);// 不可以用“返回键”取消
        loadDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        try {
            if(loadDialog!=null&&!activity.isFinishing())
                loadDialog.show();
        }catch (Exception e){
            return;
        }

    }
    public void dismiss(){

        try {
            if(loadDialog!=null)
                loadDialog.dismiss();
        }catch (Exception e){
            return;
        }
    }
}
