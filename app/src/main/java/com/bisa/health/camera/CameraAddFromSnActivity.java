package com.bisa.health.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevStatus;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevType;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.zbar.lib.CaptureActivity;


public class CameraAddFromSnActivity extends Activity {
    private EditText etSn;
    private Button btnAdd;

    private ImageView ivQrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_add_from_sn);

        etSn = findViewById(R.id.et_camera_add_from_sn_sn);
        btnAdd = findViewById(R.id.btn_camera_add_from_sn_add);
        ivQrCode = findViewById(R.id.iv_camera_add_from_sn_qrcode);


        Uri uri = getIntent().getData();
        if(uri != null) {
            String devSn = uri.getQueryParameter("deviceSn");
            if(devSn != null) {
                etSn.setText(devSn);
            }
        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(etSn.getText().toString())) {
                    FunDevice funDevice = new FunDevice();
                    //funDevice.initWith(new SDBDeviceInfo());
                    funDevice.devSn = etSn.getText().toString();
                    funDevice.devName = getString(R.string.xixin_camera);
                    funDevice.devIp = "0.0.0.0";
                    funDevice.devType = FunDevType.EE_DEV_NORMAL_MONITOR;
                    funDevice.devStatus = FunDevStatus.STATUS_UNKNOWN;
                    funDevice.isRemote = true;
                    funDevice.devMac = etSn.getText().toString();
                    funDevice.loginName = "admin";
                    funDevice.loginPsw = "";

                    FunSupport.getInstance().mCurrDevice = funDevice;
                    Intent intent = new Intent(CameraAddFromSnActivity.this, CameraNameActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        ivQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Android6.0 以上动态申请权限 当手机系统大于 23 时，才有必要去判断权限是否获取
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查该权限是否已经获取
                    int i = ContextCompat.checkSelfPermission(CameraAddFromSnActivity.this, Manifest.permission.CAMERA);
                    // 权限是否已经授权 GRANTED---授权  DINIED---拒绝
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        //没有权限，向用户请求权限
                        ActivityCompat.requestPermissions(CameraAddFromSnActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
                        return;
                    }
                }
                Intent intent = new Intent(CameraAddFromSnActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                etSn.setText(data.getStringExtra("SN"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 2) {
            if(permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(CameraAddFromSnActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
