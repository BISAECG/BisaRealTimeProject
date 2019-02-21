package com.bisa.health;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class LoginPwdActivity extends BaseActivity implements View.OnClickListener,CheckBox.OnCheckedChangeListener,Validator.ValidationListener{

    private TextView txt_regedit;
    private TextView txt_login_switch;
    private TextView txt_getpwd;

    private CheckBox img_pwd_show;

    private boolean isValidation = false;


    @NotEmpty(messageResId = R.string.dialog_tip_error_username)
    @Order(2)
    private EditText tv_iphone;


    @NotEmpty(messageResId = R.string.dialog_tip_error_notpassword)
    @Order(3)
    private EditText tv_pwd;

    private Button btn_login;
    private Validator validator;

    private TextView txt_switch_area;

    //国家地区代码
    private HealthServer mHealthServer;
    private RestServiceImpl restService;
    private SharedPersistor sharedPersistor;
    private User mUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpwd_activity);
        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        mUser=sharedPersistor.loadObject(User.class.getName());
        restService = new RestServiceImpl(this,mHealthServer);

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);

        img_pwd_show=(CheckBox) this.findViewById(R.id.img_pwd_show);
        img_pwd_show.setOnCheckedChangeListener(this);

        tv_iphone=(EditText) this.findViewById(R.id.tv_iphone);
        tv_iphone.setOnClickListener(this);

        tv_pwd=(EditText)this.findViewById(R.id.tv_pwd);
        tv_pwd.setOnClickListener(this);

        btn_login=(Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        txt_switch_area = (TextView) this.findViewById(R.id.txt_switch_area);
        txt_switch_area.setOnClickListener(this);

        txt_regedit=(TextView) this.findViewById(R.id.txt_regedit);
        txt_regedit.setOnClickListener(this);

        txt_login_switch=(TextView)this.findViewById(R.id.txt_login_switch);
        txt_login_switch.setOnClickListener(this);

        txt_getpwd=(TextView)this.findViewById(R.id.txt_getpwd);
        txt_getpwd.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == btn_login) {
            validator.validate();


            final String username = tv_iphone.getText().toString();
            final String password = tv_pwd.getText().toString();


            if (!isValidation) {
                return;
            }

                        /*
            获取账号的权限
             */
            LoadDiaLogUtil.getInstance().show(LoginPwdActivity.this, false);
            final Long requestStart = SystemClock.elapsedRealtime();
            synchronized (this) {
                String timeStamp=""+DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                String digest= CryptogramService.getInstance().hmacDigest(
                        ArrayUtil.sort(new String[]{"account","password","clientKey","timeStamp"},
                                new String[]{username, MD5Help.md5EnBit32(password),username,timeStamp}));

                FormBody body = new FormBody.Builder()
                        .add("account", username)
                        .add("password", MD5Help.md5EnBit32(password))
                        .add("clientKey", username)
                        .add("digest",digest)
                        .add("timeStamp",timeStamp)
                        .build();
                Call call = restService.loginPwd(body);
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
                    public void onResponse(Call call,final Response response) throws IOException {
                        final String json = response.body().string();
                        Log.i(TAG, "onResponse: "+json);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadDiaLogUtil.getInstance().dismiss();
                                ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                                if(result==null){
                                    return;
                                }


                                if(result.getCode() == HttpFinal.CODE_200||result.getCode() == HttpFinal.CODE_201){
                                    sharedPersistor.saveObject(result.getData());
                                    mHealthServer.setToken(result.getToken());
                                    sharedPersistor.saveObject(mHealthServer);
                                    if(!StringUtils.isEmpty(mHealthServer.getToken())){
                                        XGPushManager.bindAccount(LoginPwdActivity.this, MD5Help.md5EnBit32(mHealthServer.getToken()));
                                    }

                                     ActivityUtil.startActivity(LoginPwdActivity.this,MainActivity.class,true,ActionEnum.DOWN);


                                }else{
                                    showToast(result.getMessage());
                                }
                                return;

                            }
                        });


                    }

                });


            }

        }else if(v==txt_regedit){
            ActivityUtil.startActivity(this,RegeditActivity.class, ActionEnum.NEXT);
        }else if(v==txt_login_switch){
            ActivityUtil.startActivity(this,LoginActivity.class,true,ActionEnum.DOWN);
        }else if(v==txt_getpwd){
            ActivityUtil.startActivity(this,ForPwdUserActivity.class,ActionEnum.NEXT);
        }else if(v==txt_switch_area){
            sharedPersistor.delObject(HealthServer.class.getName());
            ActivityUtil.startActivity(this,RunActivity.class,false,ActionEnum.NULL);
        }
    }

    private static final String TAG = "LoginPwdActivity";
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            tv_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            Log.i(TAG, "onCheckedChanged: 1");
        }else{
            tv_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT );//设置密码不可见
            Log.i(TAG, "onCheckedChanged: 2");
        }
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


