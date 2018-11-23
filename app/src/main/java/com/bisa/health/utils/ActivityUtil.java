package com.bisa.health.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bisa.health.R;
import com.bisa.health.model.enumerate.ActionEnum;

/**
 * Created by Administrator on 2018/4/13.
 */

public  class ActivityUtil {

    private static void toActivity(int flag, Activity activity, Class<?> clz, boolean isFinish, Bundle bundle, ActionEnum actionEnum) {




        if (isFinish) {
            activity.finish();
        }

        Intent mainIntent = new Intent(activity.getApplicationContext(), clz);
        if (bundle != null) {
            mainIntent.putExtras(bundle);
        }
        if(flag!=-1) {
            mainIntent.addFlags(flag);
        }

        activity.startActivity(mainIntent);

        if(actionEnum==ActionEnum.NEXT){
            activity.overridePendingTransition(R.anim.right_in,R.anim.left_out);
        }else if(actionEnum==ActionEnum.BACK){
            activity.overridePendingTransition(R.anim.right_out,R.anim.left_in);
        }else if(actionEnum==ActionEnum.DOWN){
            activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
        }else{
            activity.overridePendingTransition(0,0);
        }


    }
    private static void toActivityResult(Intent mainIntent,int code, Activity activity, Bundle bundle, ActionEnum actionEnum) {

        activity.startActivityForResult(mainIntent,code);

        if(actionEnum==ActionEnum.NEXT){
            activity.overridePendingTransition(R.anim.right_in,R.anim.left_out);
        }else if(actionEnum==ActionEnum.BACK){
            activity.overridePendingTransition(R.anim.right_out,R.anim.left_in);
        }else if(actionEnum==ActionEnum.DOWN){
            activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
        }else{
            activity.overridePendingTransition(0,0);
        }


    }
    public static void startActivityResult(Intent mainIntent,int code,Activity activity, Bundle bundle,ActionEnum actionEnum) {
        toActivityResult(mainIntent,code,activity,bundle,actionEnum);
    }
    public static void startActivityResult(Intent mainIntent,int code,Activity activity,ActionEnum actionEnum) {
        toActivityResult(mainIntent,code,activity,null,actionEnum);
    }

    public static void startActivity(Activity activity, Class<?> clz,ActionEnum actionEnum) {
        toActivity(-1,activity,clz,false,null,actionEnum);
    }
    public static void startActivity(Activity activity, Class<?> clz, boolean isFinish,ActionEnum actionEnum) {
        toActivity(-1,activity,clz,isFinish,null,actionEnum);
    }
    public static void startActivity(Activity activity, Class<?> clz, boolean isFinish, Bundle bundle,ActionEnum actionEnum) {
        toActivity(-1,activity,clz,isFinish,bundle,actionEnum);
    }
    public static void startActivity(int flag, Activity activity, Class<?> clz, boolean isFinish, Bundle bundle,ActionEnum actionEnum) {
        toActivity(flag,activity,clz,isFinish,bundle,actionEnum);
    }
    public static void startActivity(int flag, Activity activity, Class<?> clz, boolean isFinish,ActionEnum actionEnum) {
        toActivity(flag,activity,clz,isFinish,null,actionEnum);
    }

    public static void finishNotAnim(Activity activity){

        if(!activity.isFinishing()){
            activity.finish();
            activity.overridePendingTransition(0,0);
        }

    }

    public static void finishAnim(Activity activity,ActionEnum actionEnum){

        activity.finish();

        if(actionEnum==ActionEnum.BACK){
            activity.overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }else if(actionEnum==ActionEnum.DOWN){
            activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
        }else if(actionEnum==ActionEnum.NEXT) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }


    }

    public static void startActivity(Activity activity,Intent intent,boolean isFinish,ActionEnum actionEnum){

        if(isFinish){
            activity.finish();
        }
        activity.startActivity(intent);
        if(actionEnum==ActionEnum.NEXT){
            activity.overridePendingTransition(R.anim.right_in,R.anim.left_out);
        }else if(actionEnum==ActionEnum.BACK){
            activity.overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }else if(actionEnum==ActionEnum.DOWN){
            activity.overridePendingTransition(R.anim.activity_in,R.anim.activity_out);
        }else{
            activity.overridePendingTransition(0,0);
        }
    }

}
