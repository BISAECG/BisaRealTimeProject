package com.bisa.health.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunDevicePassword;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.ModifyPassword;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.lib.FunSDK;

public class CameraPwActivity extends BaseActivity {
    private TextView tvSN;
    private EditText etNewPw, etConfirmPw;
    private Button btnConfirm;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_pw);

        tvSN = findViewById(R.id.tv_camera_pw_sn);
        etNewPw = findViewById(R.id.et_camera_pw_new_pw);
        etConfirmPw = findViewById(R.id.et_camera_pw_confirm_pw);
        btnConfirm = findViewById(R.id.btn_camera_pw_confirm);


        mFunDevice = FunSupport.getInstance().mCurrDevice;

        tvSN.setText("SN: " + mFunDevice.getDevSn());

        deviceClearNativePws();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(false);
                // 如果设备未登录,先登录设备
                if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
                    loginDevice();
                } else {
                    tryToChangePassw();
                }

            }
        });

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    tryToChangePassw();
                }
            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId()) {
                    dialogDismiss();
                    showToast("login fail:" + FunError.getErrorStr(errCode));
                }
            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if (mFunDevice.getId() == funDevice.getId() && ModifyPassword.CONFIG_NAME.equals(configName) ) {
                    // 修改密码成功,保存新密码,下次登录使用
                    //if ( null != mFunDevice && null != etNewPw ) {
                    //FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), etNewPw.getText().toString());
                    //}
                    // 库函数方式本地保存密码
                    //if (FunSupport.getInstance().getSaveNativePassword()) {
                    //FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", etNewPw.getText().toString());
                    // 如果设置了使用本地保存密码，则将密码保存到本地文件
                    //}
                    // 隐藏等待框
                    dialogDismiss();
                    ActivityUtil.startActivity(CameraPwActivity.this, CameraNameActivity.class, ActionEnum.NULL);
                    finish();
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if(mFunDevice.getId() == funDevice.getId() && ModifyPassword.CONFIG_NAME.equals(configName)) {
                    dialogDismiss();
                    showToast(FunError.getErrorStr(errCode));
                }
            }

            @Override
            public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

            }

            @Override
            public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

            }

            @Override
            public void onDeviceFileListGetFailed(FunDevice funDevice) {

            }
        };

        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);

    }

    private void tryToChangePassw() {

        String newPassw = etNewPw.getText().toString();
        String newPasswConfirm = etConfirmPw.getText().toString();

        if (!newPassw.equals(newPasswConfirm)) {
            dialogDismiss();
            showToast(getResources().getString(R.string.camera_pw_confirm_error));
            return;
        }

        // 修改密码,设置ModifyPassword参数
        // 注意,如果是直接调用FunSDK.DevSetConfigByJson()接口,需要对密码做MD5加密,参考ModifyPassword.java的处理
        ModifyPassword modifyPasswd = new ModifyPassword();
        modifyPasswd.PassWord = "";
        modifyPasswd.NewPassWord = newPasswConfirm;

        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, modifyPasswd);
    }

    private void loginDevice() {
        FunSupport.getInstance().requestDeviceLogin(mFunDevice.getDevSn(), "admin", "");
    }

    private void deviceClearNativePws() {
        //FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), "");
        // 库函数方式本地保存密码
        //if (FunSupport.getInstance().getSaveNativePassword()) {
            FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", "");
            // 如果设置了使用本地保存密码，则将密码保存到本地文件
        //}
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
