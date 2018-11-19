package com.bisa.health.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bisa.health.R;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ReportDiaLogUtil {

    private static ReportDiaLogUtil instance=null;
    private static Dialog dialog;;
    private ReportDiaLogUtil(){}

    public static ReportDiaLogUtil getInstance() {

        if (instance == null) {
            synchronized (ReportDiaLogUtil.class) {
                if (instance == null) {//2
                    instance = new ReportDiaLogUtil();
                }
            }
        }
        return instance;
    }

    public void show(Activity activity,boolean isCancel){

        if(dialog!=null&&dialog.isShowing()){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.dialog_report_main, null);// 得到加载view
        final FrameLayout layout = (FrameLayout) v.findViewById(R.id.dialog_view_main);// 加载布局
        RelativeLayout rl_daily= (RelativeLayout) v.findViewById(R.id.rl_daily);
        rl_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.INVISIBLE);
            }
        });

        dialog = new Dialog(activity, R.style.loading_dialog);// 创建自定义样式dialog
        dialog.setCancelable(isCancel);// 不可以用“返回键”取消

        dialog.setContentView(layout, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT));// 设置布局

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
