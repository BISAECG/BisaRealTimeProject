package com.bisa.health.camera;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.sdk.UIFactory;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

public class CameraSearchActivity extends BaseActivity implements OnFunDeviceWiFiConfigListener {
    private ImageView ivQrcode;
    private ProgressBar progressBar;
    private TextView tvTimer;
    private Button btnSetWifiWithQrcode;
    private Button btnCancel;
    private ImageView ivCircle;

    private CountDownTimer countDownTimer;

    private String[] wifiConfig;

    private LinearLayout lLayoutTips;
    private ImageView ivWifiQrcode;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_search);

        ivQrcode = findViewById(R.id.iv_camera_search_qrcode);
        progressBar = findViewById(R.id.pgb_camera_search);
        tvTimer = findViewById(R.id.tv_camera_search_timer);
        btnSetWifiWithQrcode = findViewById(R.id.btn_camera_search_setWifiWithQrcode);
        btnCancel = findViewById(R.id.btn_camera_search_cancel);
        ivCircle = findViewById(R.id.iv_camera_search_circle);

        FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);

        countDownTimer = new CountDownTimer(181000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = millisUntilFinished / 1000 + "s";
                tvTimer.setText(time);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0s");
                progressBar.setVisibility(View.GONE);
                ivCircle.setVisibility(View.VISIBLE);
                btnSetWifiWithQrcode.setVisibility(View.VISIBLE);
                btnCancel.setSelected(true);
                btnCancel.setText(R.string.camera_search_retry);
                FunSupport.getInstance().stopWiFiQuickConfig();
            }
        };

        wifiConfig = getIntent().getStringArrayExtra("wifiConfig");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btnCancel.isSelected()) {
                    btnCancel.setSelected(true);
                    progressBar.setVisibility(View.GONE);
                    ivCircle.setVisibility(View.VISIBLE);
                    btnCancel.setText(R.string.camera_search_retry);
                    countDownTimer.cancel();
                    FunSupport.getInstance().stopWiFiQuickConfig();
                }
                else {
                    btnCancel.setSelected(false);
                    btnSetWifiWithQrcode.setVisibility(View.GONE);
                    ivCircle.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    btnCancel.setText(R.string.camera_search_cancel);
                    countDownTimer.start();
                    if(wifiConfig != null) {
                        FunSupport.getInstance().startWiFiQuickConfig(wifiConfig[0],
                                wifiConfig[1], wifiConfig[2], wifiConfig[3],
                                Integer.valueOf(wifiConfig[4]), 0, wifiConfig[5], -1);
                    }
                }
            }
        });


        countDownTimer.start();


        ivQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow();
            }
        });

        btnSetWifiWithQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow();
            }
        });


        String qrCodeStr = "S:" + wifiConfig[0] + "\nP:" + wifiConfig[6] + "\nE:1" + "\nM:" + wifiConfig[7].replace(":", "") + "\nI:255\n";


        View view = getLayoutInflater().inflate(R.layout.dialog_camera_search, null);
        lLayoutTips = view.findViewById(R.id.llayout_camera_search_dialog_tips);
        Button btnIknow = view.findViewById(R.id.btn_camera_search_dialog_iKnow);
        ivWifiQrcode = view.findViewById(R.id.iv_camera_search_dialog_wifiQrcode);
        // 生成二维码
        Bitmap qrCodeBmp = UIFactory.createCode(qrCodeStr, 1000, 0xff202020);
        ivWifiQrcode.setImageBitmap(qrCodeBmp);

        btnIknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lLayoutTips.setVisibility(View.GONE);
                ivWifiQrcode.setVisibility(View.VISIBLE);
            }
        });
        dialog = new AlertDialog.Builder(this).setView(view).create();

    }

    private void dialogShow() {
        lLayoutTips.setVisibility(View.VISIBLE);
        ivWifiQrcode.setVisibility(View.GONE);
        dialog.show();
    }

    @Override
    public void onDeviceWiFiConfigSetted(FunDevice funDevice) {
        FunSupport.getInstance().mCurrDevice = funDevice;
        ActivityUtil.startActivity(CameraSearchActivity.this, CameraPwActivity.class, ActionEnum.NULL);
        countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        FunSupport.getInstance().stopWiFiQuickConfig();
        FunSupport.getInstance().removeOnFunDeviceWiFiConfigListener(this);
        super.onDestroy();
    }
}
