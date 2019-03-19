package com.bisa.health;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.tencent.android.tpush.XGPushManager;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/5/25.
 */

public  class BaseActivity extends FragmentActivity {

    private static final String TAG = "BaseActivity";
    private Toast mToast;
    private BisaApp application;
    private Activity oContext;
    private Dialog loadDialog;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            Log.i(TAG, "onCreate fixOrientation when Oreo, result = " + result);
        }
        super.onCreate(savedInstanceState);
        if (application == null) {
            // 得到Application对象
            application = (BisaApp) getApplication();
        }
        sharedPersistor=new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        oContext = this;// 把当前的上下文对象赋值给BaseActivity
        addActivity();// 调用添加方法
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    // 添加Activity方法
    public void addActivity() {
        application.addActivity(oContext);
    }
    public  void finishActivity(BaseActivity activity){
        application.finishActivity(activity);
    }

    public  void finishActivity(Class clz){
        application.finishActivity(clz);
    }
    //销毁当个Activity方法
    public void currentActivity() {
        application.finishActivity();
        //application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
    }
    //销毁所有Activity方法
    public void exitApp() {
        application.exitApp();
    }

    public void restartApp() {


        XGPushManager.delAccount(oContext, "*");
        XGPushManager.unregisterPush(oContext);
        application.restart(oContext);
        //application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
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
    public void dialogDismiss(){
        try {
            if(loadDialog!=null)
                loadDialog.dismiss();
        }catch (Exception e){
            return;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res!=null&&res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    public void regeditPush(String session){
        if(StringUtils.isEmpty(session)){
            return;
        }
        XGPushManager.bindAccount(oContext,session);
    }

    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            Log.i(TAG, "avoid calling setRequestedOrientation when Oreo.");
            return;
        }

        super.setRequestedOrientation(requestedOrientation);
    }

    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }
}
