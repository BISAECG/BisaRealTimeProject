package com.bisa.health.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ToastUtil {
    private static ToastUtil instance=null;
    private Context context;
    private Toast mToast;
    private ToastUtil(){}


    private ToastUtil(Context context){
        this.context=context;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    private static final String TAG = "ToastUtil";
    public static ToastUtil getInstance(Context context) {

        if (instance == null) {
            synchronized (ToastUtil.class) {
                if (instance == null) {//2
                    instance = new ToastUtil(context);
                }
            }
        }
        instance.context(context);
        return instance;
    }

    public ToastUtil context(Context context){

        if(this.context==null||!context.getClass().getName().equals(this.context.getClass().getName())){
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            this.context=context;
        }
        return this;
    }


    public void show(final String text,int duration){
                mToast.setDuration(duration);
                mToast.setText(text);
                mToast.show();
    }
    public void show(final String text){
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.show();
    }

    public void hide(){

      mToast.cancel();

    }
}
