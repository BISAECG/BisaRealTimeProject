package com.bisa.health.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2017/7/29.
 */

public class LoadDownTimerUtils extends CountDownTimer {
    private TextView textView;
    private Context context;
    private static final String TAG = "LoadDownTimerUtils";
    /**
     * @param
     *
     *
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receiver
     *                          {@link #onTick(long)} callbacks.
     */
    public LoadDownTimerUtils(TextView textView, long millisInFuture, long countDownInterval, Context context) {
        super(millisInFuture, countDownInterval);
        this.textView = textView;
        this.context=context;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        textView.setText(String.format(context.getString(R.string.app_load).toString(), ""+(millisUntilFinished / 1000)));  //设置倒计时时间
    }

    @Override
    public void onFinish() {
        textView.setText("");  //设置倒计时时间
    }


}
