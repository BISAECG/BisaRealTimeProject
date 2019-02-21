package com.bisa.health;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.auth.CryptogramService;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.ArrayUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.GsonUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.MD5Help;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.tencent.android.tpush.XGPushManager;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class LoginActivity extends BaseActivity implements  View.OnClickListener, Validator.ValidationListener{

    public final String TAG = "Login";

    private boolean isValidation = false;
    private SharedPersistor sharedPersistor;

    @NotEmpty(messageResId = R.string.dialog_tip_error_area)
    @Order(1)
    private TextView tv_areacode;

    @NotEmpty(messageResId = R.string.dialog_tip_error_phone)
    @Order(2)
    private EditText tv_iphone;

    @NotEmpty(messageResId = R.string.dialog_tip_error_code)
    @Order(3)
    private EditText tv_verify;

    private TextView txt_login_pwd;

    private TextView txt_regedit;

    private TextView txt_switch_area;

    private Button btn_login;


    private Validator validator;
    private RestServiceImpl restService;


    //国家地区代码

    private HealthServer mHealthServer;

    private ImageView imageView;

    private User mUser;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     *
     *                           APP 目前是单用户登入的,如果改多账户登入 只需要改HealthServer 和 User存了的KEY值就可以了
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Log.i(TAG, "onCreate: ");
        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        mUser=sharedPersistor.loadObject(User.class.getName());

        if(mUser!=null){
            ActivityUtil.startActivity(this, MainActivity.class, true, ActionEnum.NULL);
            return;
        }

        restService = new RestServiceImpl(this,mHealthServer);
        LoginActivityPermissionsDispatcher.initPermissionWithPermissionCheck(this);
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);

        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);


        txt_login_pwd = (TextView) this.findViewById(R.id.txt_login_pwd);
        txt_login_pwd.setOnClickListener(this);

        txt_regedit = (TextView) this.findViewById(R.id.txt_regedit);
        txt_regedit.setOnClickListener(this);

        txt_switch_area = (TextView) this.findViewById(R.id.txt_switch_area);
        txt_switch_area.setOnClickListener(this);

        tv_areacode = (TextView) this.findViewById(R.id.tv_areacode);
        tv_areacode.setText(mHealthServer.getAreaCode());
        tv_verify = (EditText) this.findViewById(R.id.tv_verify);
        tv_iphone = (EditText) this.findViewById(R.id.tv_iphone);

        imageView=(ImageView) this.findViewById(R.id.img_wechat);

        String lan = Locale.getDefault().getLanguage();
        Log.i(TAG, "onCreate: "+lan);
        String country = Locale.getDefault().getCountry();
        Log.i(TAG, "onCreate: "+country);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }


    @Override
    public void onClick(View v) {

   
        if (v == btn_login) {

            Log.i(TAG, "onClick: "+imageView.getId());
            validator.validate();

            final String username = tv_iphone.getText().toString();
            final String code = tv_verify.getText().toString();


            if (!isValidation) {
                return;
            }
            /*
            获取账号的权限
             */
            LoadDiaLogUtil.getInstance().show(LoginActivity.this, false);
            final Long requestStart = SystemClock.elapsedRealtime();

            synchronized (this) {

                String timeStamp=""+DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                String digest= CryptogramService.getInstance().hmacDigest(
                        ArrayUtil.sort(new String[]{"phone","code","clientKey","timeStamp"},
                                new String[]{username,code,username,timeStamp}));
                FormBody body = new FormBody.Builder()
                        .add("phone", username)
                        .add("code", code)
                        .add("clientKey", username)
                        .add("digest",digest)
                        .add("timeStamp",timeStamp)
                        .build();

                Call call = restService.login(body);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(getResources().getString(R.string.server_error));
                                LoadDiaLogUtil.getInstance().dismiss();
                                return;
                            }
                        });

                    }

                    @Override
                    public void onResponse(final Call call, final Response response) throws IOException {
                        final String json = response.body().string();
                        Log.i(TAG, "json:"+json);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadDiaLogUtil.getInstance().dismiss();
                                ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                                if(result==null){
                                    return;
                                }
                                Log.i(TAG, "result:"+result.getData());

                                 if(result.getCode() == HttpFinal.CODE_200||result.getCode() == HttpFinal.CODE_201){
                                    Log.i(TAG, "run: "+result.getData());
                                    sharedPersistor.saveObject(result.getData());
                                    mHealthServer.setToken(result.getToken());
                                    sharedPersistor.saveObject(mHealthServer);
                                    if(!StringUtils.isEmpty(mHealthServer.getToken())){
                                        XGPushManager.bindAccount(LoginActivity.this, MD5Help.md5EnBit32(mHealthServer.getToken()));
                                    }
                                    ActivityUtil.startActivity(LoginActivity.this,MainActivity.class,true,ActionEnum.DOWN);


                                }else{
                                    showToast(result.getMessage());
                                }
                                return;
                            }
                        });
                    }

                });

            }
        } else if(v==txt_login_pwd){
            ActivityUtil.startActivity(this,LoginPwdActivity.class,true,ActionEnum.DOWN);
        }else if(v==txt_regedit){
            ActivityUtil.startActivity(this,RegeditActivity.class,ActionEnum.NEXT);
        }else if(v==txt_switch_area){
            sharedPersistor.delObject(HealthServer.class.getName());
            ActivityUtil.startActivity(this,RunActivity.class,false,ActionEnum.NULL);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /*
    权限申请            Manifest.permission.READ_PHONE_STATE,
     */
    @NeedsPermission({
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initPermission() {

    }

    @OnShowRationale({
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showRationaleForApp(final PermissionRequest request) {

        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_title)
                .setPositiveButton(R.string.commit_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.cancel_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied({
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showDeniedForApp() {
        Toast.makeText(this, "权限验证不通过,软件退出,请再次打开软件允许权限", Toast.LENGTH_LONG).show();
        finish();
    }

    @OnNeverAskAgain({
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showNeverAskForApp() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.open_permission)
                .setMessage(R.string.permission_setting)
                .setPositiveButton(R.string.opensetting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        i.setData(uri);
                        startActivity(i);
                        dialog.dismiss();
                        finish();
                    }
                }).show();

    }




    /**
     * 表单验证成功OR失败处理事件
     */
    @Override
    public void onValidationSucceeded() {
        isValidation = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isValidation = false;
        for (ValidationError error : errors) {
            String message = error.getCollatedErrorMessage(this);
            showToast(message);
            break;
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}


