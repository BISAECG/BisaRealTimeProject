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
import com.bisa.health.model.dto.ForGetPwdDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.CountDownTimerUtils;
import com.bisa.health.utils.LoadDiaLogUtil;
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

public class ForPwdVailActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener{


    @NotEmpty(messageResId = R.string.dialog_tip_error_code)
    @Order(1)
    private EditText tv_code;

    private TextView tv_tip;

    private Button imgbtn_smsSend;

    private Button btn_login;
    private ForGetPwdDto forGetPwdDto;
    private IRestService restService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;

    private Validator validator;
    private boolean isValidation = false;
    private static final String TAG = "ForPwdVailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getpwd_s3_activity);

        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        forGetPwdDto= (ForGetPwdDto) getIntent().getExtras().getSerializable(ForGetPwdDto.class.getName());
        if(forGetPwdDto==null){
            showToast(getString(R.string.title_error_try));
            finish();
            return;
        }

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);

        restService=new RestServiceImpl(this,mHealthServer);
        tv_tip=(TextView) this.findViewById(R.id.tv_tip);

        tv_code=(EditText) this.findViewById(R.id.tv_code);

        imgbtn_smsSend=(Button) this.findViewById(R.id.imgbtn_smsSend);
        imgbtn_smsSend.setOnClickListener(this);

        btn_login=(Button)this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        if(forGetPwdDto.vLoginType().equals(VerifyTypeEnum.PHONE.name())){
            String mailTip=getResources().getString(R.string.title_getpwd_iphone);
            String fromatStr=String.format(mailTip,""+forGetPwdDto.getPhone());
            tv_tip.setText(fromatStr);
        }else{
            String mailTip=getResources().getString(R.string.title_getpwd_mail);
            String fromatStr=String.format(mailTip,""+forGetPwdDto.getEmail());
            tv_tip.setText(fromatStr);
        }

        sendCode();
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
            LoadDiaLogUtil.getInstance().show(ForPwdVailActivity.this, false);


            synchronized (this) {

                FormBody body = new FormBody.Builder()
                        .add("username", ""+forGetPwdDto.getUsername())
                        .add("code", code)
                        .add("loginType", forGetPwdDto.vLoginType())
                        .build();

                Call call = restService.findPassword(body);
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
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();
                        Log.i(TAG, "onResponse: "+json);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadDiaLogUtil.getInstance().dismiss();
                                ResultData<Object> result = Utility.jsonToObject(json,new TypeToken<ResultData<Object>>(){}.getType());

                                if (result == null) {
                                    return;
                                }

                                if(result.getCode()==HttpFinal.CODE_200){
                                    Bundle body=new Bundle();
                                    body.putSerializable(ForGetPwdDto.class.getName(),forGetPwdDto);
                                    ActivityUtil.startActivity(ForPwdVailActivity.this,ForPwdOkActivity.class,false, body, ActionEnum.NULL);
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

    private void sendCode(){
        synchronized (this) {


            showDialog(false);

            Map<String,String> param=new HashMap<String,String>();
            param.put("loginType",forGetPwdDto.vLoginType());
            param.put("username",forGetPwdDto.getUsername());
            Call call=restService.sendCodeByUser(param);

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
                            final CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(imgbtn_smsSend, 60000, 1000, ForPwdVailActivity.this);
                            mCountDownTimerUtils.start();

                            ResultData<Object> appServer = Utility.jsonToObject(json,new TypeToken<ResultData<Object>>(){}.getType());
                            if (appServer == null) {
                                return;
                            }
                        }
                    });
                }
            });
        }
    }
}
