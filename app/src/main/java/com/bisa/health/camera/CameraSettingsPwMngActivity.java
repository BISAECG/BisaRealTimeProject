package com.bisa.health.camera;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunDevicePassword;
import com.bisa.health.camera.lib.funsdk.support.FunError;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.ModifyPassword;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.FunSDK;

public class CameraSettingsPwMngActivity extends BaseActivity {
    private EditText etOldPw;
    private EditText etNewPw;
    private EditText etConfirmPw;
    private ImageView ivEyeOldPw;
    private ImageView ivEyeNewPw;
    private ImageView ivEyeConfirmPw;
    private Button btnConfirmMod;

    private FunDevice mFunDevice;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings_pw_mng);

        etOldPw = findViewById(R.id.et_camera_settings_pwMng_oldPw);
        etNewPw = findViewById(R.id.et_camera_settings_pwMng_newPw);
        etConfirmPw = findViewById(R.id.et_camera_settings_pwMng_confirmPw);
        ivEyeOldPw = findViewById(R.id.iv_camera_settings_pwMng_oldPw);
        ivEyeNewPw = findViewById(R.id.iv_camera_settings_pwMng_newPw);
        ivEyeConfirmPw = findViewById(R.id.iv_camera_settings_pwMng_confirmPw);
        btnConfirmMod = findViewById(R.id.btn_camera_settings_pwMng_confirmMod);

        mFunDevice = FunSupport.getInstance().mCurrDevice;
        if(mFunDevice == null) {
            Toast.makeText(this, "Device does not exist!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ivEyeOldPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivEyeOldPw.isSelected()) {
                    ivEyeOldPw.setSelected(true);
                    etOldPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etOldPw.setSelection(etOldPw.getText().length());
                }
                else {
                    ivEyeOldPw.setSelected(false);
                    etOldPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etOldPw.setSelection(etOldPw.getText().length());
                }

            }
        });
        ivEyeNewPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivEyeNewPw.isSelected()) {
                    ivEyeNewPw.setSelected(true);
                    etNewPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etNewPw.setSelection(etNewPw.getText().length());
                }
                else {
                    ivEyeNewPw.setSelected(false);
                    etNewPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etNewPw.setSelection(etNewPw.getText().length());
                }

            }
        });
        ivEyeConfirmPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ivEyeConfirmPw.isSelected()) {
                    ivEyeConfirmPw.setSelected(true);
                    etConfirmPw.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etConfirmPw.setSelection(etConfirmPw.getText().length());
                }
                else {
                    ivEyeConfirmPw.setSelected(false);
                    etConfirmPw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirmPw.setSelection(etConfirmPw.getText().length());
                }

            }
        });

        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {
                if (mFunDevice.getId() == funDevice.getId() && ModifyPassword.CONFIG_NAME.equals(configName)) {
                    // 修改密码成功,保存新密码,下次登录使用
                    //if (null != etNewPw) {
                        //FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(), etNewPw.getText().toString());
                    //}
                    // 库函数方式本地保存密码
                    //if (FunSupport.getInstance().getSaveNativePassword()) {
                        //FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", etNewPw.getText().toString());
                        // 如果设置了使用本地保存密码，则将密码保存到本地文件
                    //}
                    etOldPw.setText("");
                    etNewPw.setText("");
                    etConfirmPw.setText("");
                    showToast(getString(R.string.camera_settings_pwMng_modSuccess), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {
                if (ModifyPassword.CONFIG_NAME.equals(configName) && mFunDevice.getId() == funDevice.getId()) {
                    showToast(FunError.getErrorStr(errCode), Toast.LENGTH_LONG);
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

        btnConfirmMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToChangePassw();
            }
        });


    }

    private void tryToChangePassw() {

        String oldPassw = etOldPw.getText().toString();
        String newPassw = etNewPw.getText().toString();
        String newPasswConfirm = etConfirmPw.getText().toString();

        if (!newPassw.equals(newPasswConfirm)) {
            showToast(getResources().getString(R.string.camera_pw_confirm_error));
            return;
        }

        // 修改密码,设置ModifyPassword参数
        // 注意,如果是直接调用FunSDK.DevSetConfigByJson()接口,需要对密码做MD5加密,参考ModifyPassword.java的处理
        ModifyPassword modifyPasswd = new ModifyPassword();
        modifyPasswd.PassWord = oldPassw;
        modifyPasswd.NewPassWord = newPasswConfirm;

        FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, modifyPasswd);
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onDestroy();
    }
}
