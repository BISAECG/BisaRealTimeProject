package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.CountDownTimerUtils;
import com.bisa.health.utils.Utility;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessVailActivity extends BaseActivity implements View.OnClickListener,Validator.ValidationListener{

    private static final String TAG = "BindAccessActivity";


    @NotEmpty(messageResId = R.string.dialog_tip_error_code)
    @Order(1)
    private EditText tv_code;

    private TextView tv_tip_1;

    private TextView tv_tip_2;

    private Button imgbtn_smsSend;

    private Button btn_login;
    private UserBindDto userBindDto;
    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private Validator validator;
    private boolean isValidation = false;
    //验证类型
    private int vType=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_accout_vail_layout);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);
        userBindDto=(UserBindDto)getIntent().getExtras().getSerializable(UserBindDto.class.getName());
        if(userBindDto==null){
            showToast(getString(R.string.title_error_try));
            finish();
            return;

        }

        tv_tip_1=(TextView) this.findViewById(R.id.tv_tip_1);
        tv_tip_2=(TextView) this.findViewById(R.id.tv_tip_2);
        tv_code=(EditText) this.findViewById(R.id.tv_code);

        imgbtn_smsSend=(Button) this.findViewById(R.id.imgbtn_smsSend);
        imgbtn_smsSend.setOnClickListener(this);

        btn_login=(Button)this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        if(userBindDto.getVerifyType().equals(VerifyTypeEnum.PHONE.name())){
            String mailTip=getResources().getString(R.string.title_getpwd_iphone);
            String fromatStr=String.format(mailTip,userBindDto.getPhone());
            tv_tip_2.setText(fromatStr);
        }else{
            String mailTip=getResources().getString(R.string.title_getpwd_mail);
            String fromatStr=String.format(mailTip,userBindDto.getEmail());
            tv_tip_2.setText(fromatStr);
        }

        sendCode();
    }


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
    public void onClick(View v) {
        if(v==imgbtn_smsSend){
            sendCode();
        }else if(v==btn_login){

            validator.validate();

            final String code = tv_code.getText().toString();

            if (!isValidation) {
                return;
            }
           showDialog(false);
            synchronized (this) {

                FormBody body = new FormBody.Builder()
                        .add("loginType",userBindDto.getVerifyType()+"")
                        .add("code", code)
                        .build();

                Call call = mRestService.varifyBindToken(body);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogDismiss();
                                showToast(getResources().getString(R.string.server_error));

                                return;
                            }
                        });

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();
                        Log.i(TAG, "onResponse: "+json);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogDismiss();
                                ResultData<String> result = Utility.jsonToObject(json,new TypeToken<ResultData<String>>(){}.getType());

                                if (result == null) {
                                    return;
                                }

                                if(result.getCode()== HttpFinal.CODE_200){
                                    Bundle body=new Bundle();
                                    body.putString("token",result.getToken());
                                    body.putSerializable(UserBindDto.class.getName(),userBindDto);
                                    ActivityUtil.startActivity(BindAccessVailActivity.this,BindAccessOkActivity.class,false, body, ActionEnum.NEXT);
                                }else{
                                    showToast(result.getMessage());

                                }
                                return;

                            }
                        });


                    }

                });
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void sendCode(){
        synchronized (this) {

            showDialog(false);

            Log.d(TAG, "sendCode: "+userBindDto);

            Map<String,String> param=new HashMap<String,String>();
            param.put("loginType",userBindDto.getVerifyType()+"");
            param.put("username",userBindDto.getUsername());

            Call call=mRestService.sendCodeByUser(param);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           dialogDismiss();
                            showToast(getString(R.string.network_error));

                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String json = response.body().string();
                    Log.i(TAG, "onResponse: "+json);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogDismiss();
                            final CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(imgbtn_smsSend, 60000, 1000, BindAccessVailActivity.this);
                            mCountDownTimerUtils.start();

                            ResultData<Object> result = Utility.jsonToObject(json,new TypeToken<ResultData<Object>>(){}.getType());
                            if (result == null) {
                                return;
                            }
                        }
                    });
                }
            });
        }
    }

}
