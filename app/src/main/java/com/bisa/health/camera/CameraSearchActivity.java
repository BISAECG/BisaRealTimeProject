package com.bisa.health.camera;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

public class CameraSearchActivity extends BaseActivity implements OnFunDeviceWiFiConfigListener {
    private ImageView ivQrcode;
    private ProgressBar progressBar;
    private TextView tvTimer;
    private Button btnCancel;

    private int count = 180;
    private boolean isWifiSetted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_search);

        ivQrcode = findViewById(R.id.iv_camera_search_qrcode);
        progressBar = findViewById(R.id.pgb_camera_search);
        tvTimer = findViewById(R.id.tv_camera_search_timer);
        btnCancel = findViewById(R.id.btn_camera_search_cancel);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (count > 0 && !isWifiSetted) {
                    try {
                        Thread.sleep(1000);
                        count--;
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTimer.setText(count + "s");
                        }
                    });
                }
                if(!isWifiSetted) {
                    FunSupport.getInstance().stopWiFiQuickConfig();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnCancel.setText("重试");
                            btnCancel.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                    });
                }
            }
        }).start();

        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);

    }

    @Override
    public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
        isWifiSetted = true;
        FunSupport.getInstance().mCurrDevice = funDevice;
        ActivityUtil.startActivity(CameraSearchActivity.this, CameraPwActivity.class, ActionEnum.NULL);

        finish();
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceWiFiConfigListener(this);
        super.onDestroy();
    }
}
