package com.bisa.health.cust;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bisa.health.R;

import static java.lang.Integer.parseInt;


/**
 * Created by Administrator on 2017/11/29.
 */

public class CustReportDialog extends Dialog {

    private Context context;
    private View view;

    private int centerX;
    private int centerY;
    private int depthZ = 700;
    private int duration = 150;
    private Rotate3dAnimation openAnimation;
    private Rotate3dAnimation closeAnimation;
    private boolean isOpen = false;


    private FrameLayout dialog_view_main;

    private RelativeLayout rl_main;
    private RelativeLayout rl_main_daily;
    private RelativeLayout rl_main_all;

    /*
    报告类型
     */
    private RelativeLayout rl_daily;
    private RelativeLayout rl_all;

    private Button btnDaily;
    private Button btnAll;



    private OnClickListenerInterface mListener;
    public static interface OnClickListenerInterface {

        /**
         * 确认,
         */
        void doConfirm(int rType) throws RemoteException;

        /**
         * 取消
         */
//        public void doCancel();
    }

    public CustReportDialog(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    public void show() {

        if(rl_main!=null){
            rl_main.setVisibility(View.VISIBLE);
            rl_main_daily.setVisibility(View.GONE);
            rl_main_all.setVisibility(View.GONE);
        }
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //去掉系统的黑色矩形边框
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_report_main, null);
        setContentView(view);
        //父容器
        dialog_view_main=(FrameLayout) view.findViewById(R.id.dialog_view_main);

        //报告类型
        rl_main= (RelativeLayout) view.findViewById(R.id.rl_main);
        //免费报告
        rl_main_daily= (RelativeLayout) view.findViewById(R.id.rl_main_daily);

        //OTG报告
        rl_main_all= (RelativeLayout) view.findViewById(R.id.rl_main_all);

        rl_daily= (RelativeLayout) view.findViewById(R.id.rl_daily);
        rl_daily.setOnClickListener(new OnWidgetClickListener());

        rl_all= (RelativeLayout) view.findViewById(R.id.rl_all);
        rl_all.setOnClickListener(new OnWidgetClickListener());

        btnDaily= (Button) view.findViewById(R.id.btn_commit_report_daily);
        btnDaily.setOnClickListener(new OnWidgetClickListener());

        btnAll= (Button) view.findViewById(R.id.btn_commit_report_all);
        btnAll.setOnClickListener(new OnWidgetClickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();

//        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
//        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
//        lp.height = (int) (d.heightPixels * 0.6); // 高度设置为屏幕的0.6

        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }


    public void setClicklistener(OnClickListenerInterface clickListenerInterface) {
        this.mListener = clickListenerInterface;
    }

    private class OnWidgetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int rType= parseInt(v.getTag().toString());

            int id = v.getId();

            switch (id) {
                case R.id.rl_daily:
                    startAnimation(rl_main,rl_main_daily);
                    break;
                case R.id.rl_all:
                    startAnimation(rl_main,rl_main_all);
                    break;
                case R.id.btn_commit_report_daily:
                case R.id.btn_commit_report_all:
                    try {
                        mListener.doConfirm(rType);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void startAnimation(final View inView,final View outView) {

        //接口回调传递参数
//                    mListener.doConfirm();
//                    centerX = mContainer.getWidth() / 2;
//                    centerY = mContainer.getHeight() / 2;
        centerX = rl_main.getWidth() / 2;
        centerY = rl_main.getHeight() / 2;
      //  if (openAnimation != null) {
            Log.i("----","out View2:"+outView.getId());
            initOpenAnim(inView,outView);
            initCloseAnim(inView,outView);
       // }

        //用作判断当前点击事件发生时动画是否正在执行
        if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
            return;
        }
        if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
            return;
        }

        //判断动画执行
        if (isOpen) {
            dialog_view_main.startAnimation(openAnimation);

        } else {
            dialog_view_main.startAnimation(closeAnimation);

        }
        isOpen = !isOpen;
    }

    /**
     * 卡牌文本介绍打开效果：注意旋转角度
     */
    private void initOpenAnim(final View inView,final View outView) {
        //从0到90度，顺时针旋转视图，此时reverse参数为true，达到90度时动画结束时视图变得不可见，
        openAnimation = new Rotate3dAnimation(0, 90, centerX, centerY, depthZ, true);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                inView.setVisibility(View.GONE);
                outView.setVisibility(View.VISIBLE);
                //从270到360度，顺时针旋转视图，此时reverse参数为false，达到360度动画结束时视图变得可见
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(270, 360, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                dialog_view_main.startAnimation(rotateAnimation);
            }
        });
    }

    /**
     * 卡牌文本介绍关闭效果：旋转角度与打开时逆行即可
     */
    private void initCloseAnim(final View inView,final View outView) {
        closeAnimation = new Rotate3dAnimation(360, 270, centerX, centerY, depthZ, true);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                rl_main_daily.setVisibility(View.VISIBLE);
//                rl_main.setVisibility(View.GONE);
                outView.setVisibility(View.VISIBLE);
                inView.setVisibility(View.GONE);
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation(90, 0, centerX, centerY, depthZ, false);
                rotateAnimation.setDuration(duration);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                dialog_view_main.startAnimation(rotateAnimation);
            }
        });
    }

    // 改变视角距离, 贴近屏幕
    private void setCameraDistance() {
        int distance = 16000;
        float scale = this.context.getResources().getDisplayMetrics().density * distance;
        dialog_view_main.setCameraDistance(scale);
        rl_main.setCameraDistance(scale);
        rl_daily.setCameraDistance(scale);
        rl_all.setCameraDistance(scale);

    }

}
