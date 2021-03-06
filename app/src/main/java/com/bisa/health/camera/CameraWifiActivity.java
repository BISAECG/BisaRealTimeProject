package com.bisa.health.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;

import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.utils.DeviceWifiManager;
import com.bisa.health.camera.lib.funsdk.support.utils.MyUtils;
import com.bisa.health.camera.lib.funsdk.support.utils.StringUtils;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.IpUtil;


public class CameraWifiActivity extends BaseActivity {
    private EditText etWifiSsid, etWifiPw;
    private Button btnConfirm;
    private Button btnProblem;

    private View view;
    private ImageButton iBtnClose;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_wifi);

        etWifiSsid = findViewById(R.id.et_camera_wifi_ssid);
        etWifiPw = findViewById(R.id.et_camera_wifi_pw);
        btnConfirm = findViewById(R.id.btn_camera_wifi_confirm);
        btnProblem = findViewById(R.id.btn_camera_wifi_problem);

        String currSSID = getConnectWifiSSID();
        etWifiSsid.setText(currSSID);

        CameraSdkInit.init(this);

        view = getLayoutInflater().inflate(R.layout.dialog_camera_wifi_problem, null);
        iBtnClose = view.findViewById(R.id.ibtn_camera_wifi_problem_close);
        dialog = new AlertDialog.Builder(this)
                .setView(view).create();
        iBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSetting();
            }
        });

        btnProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


    }


    private String getConnectWifiSSID() {
        try {
            WifiManager wifimanager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifimanager.getConnectionInfo().getSSID().replace("\"", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    // 开始快速配置
    private void startQuickSetting() {

        try {
            WifiManager wifiManage = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManage.getConnectionInfo();
            DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

            if ( null == wifiInfo ) {
                showToast(getResources().getString(R.string.wifi_not_connected));
                return;
            }

            String ssid = wifiInfo.getSSID().replace("\"", "");
            if ( StringUtils.isStringNULL(ssid) ) {
                showToast(getResources().getString(R.string.wifi_not_connected));
                return;
            }

            ScanResult scanResult = DeviceWifiManager.getInstance(this).getCurScanResult(ssid);
            if ( null == scanResult ) {
                showToast(getResources().getString(R.string.wifi_not_connected));
                return;
            }


            int pwdType = MyUtils.getEncrypPasswordType(scanResult.capabilities);
            String wifiPwd = etWifiPw.getText().toString().trim();

            if ( pwdType != 0 && StringUtils.isStringNULL(wifiPwd) ) {
                // 需要密码
                showToast(getResources().getString(R.string.wifi_not_connected));
                return;
            }


            StringBuffer data = new StringBuffer();
            data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);

            String submask;
            if (wifiDhcp.netmask == 0) {
                submask = "255.255.255.0";
            } else {
                submask = IpUtil.formatIpAddress(wifiDhcp.netmask);
            }

            String mac = wifiInfo.getMacAddress();
            StringBuffer info = new StringBuffer();
            info.append("gateway:").append(IpUtil.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
                    .append(IpUtil.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
                    .append(" dns1:").append(IpUtil.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
                    .append(IpUtil.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
                    .append(" ");


            FunSupport.getInstance().startWiFiQuickConfig(ssid,
                    data.toString(), info.toString(),
                    IpUtil.formatIpAddress(wifiDhcp.gateway),
                    pwdType, 0, mac, -1);

            //FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
            String[] wifiConfig = {ssid, data.toString(), info.toString(), IpUtil.formatIpAddress(wifiDhcp.gateway), String.valueOf(pwdType), mac, wifiPwd, scanResult.BSSID};

            Intent intent = new Intent(this, CameraSearchActivity.class);
            intent.putExtra("wifiConfig", wifiConfig);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("error occur!");
        }
    }

}
