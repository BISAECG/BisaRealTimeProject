package com.bisa.health;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;

/**
 * Created by Administrator on 2018/5/25.
 */

public  class BaseFragment extends android.support.v4.app.Fragment{

    private Toast mToast;
    private BisaApp application;
    private Activity oContext;
    private Dialog loadDialog;;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        sharedPersistor=new SharedPersistor(context);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        oContext = getActivity();// 把当前的上下文对象赋值给BaseActivity
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }



    /* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
    public void showToast(String text,int duration) {
        mToast.setText(text);
        mToast.setDuration(duration);
        mToast.show();
    }

    /* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
    public void showToast(String text) {
        mToast.setText(text);
        mToast.show();
    }

    public void showDialog(boolean isCancel){

        LayoutInflater inflater = LayoutInflater.from(oContext);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                oContext, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);

        if(loadDialog!=null&&loadDialog.isShowing()){
            return;
        }

        loadDialog = new Dialog(oContext, R.style.loading_dialog);// 创建自定义样式dialog
        loadDialog.setCancelable(isCancel);// 不可以用“返回键”取消
        loadDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        try {
            if(loadDialog!=null&&!oContext.isFinishing())
                loadDialog.show();
        }catch (Exception e){
            return;
        }

    }

    public void dismissDialog(){
        try {
            if(loadDialog!=null)
                loadDialog.dismiss();
        }catch (Exception e){
            return;
        }
    }



}
