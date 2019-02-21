package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessOkActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BindAccessOkActivity";


    private EditText tv_code;

    private EditText tv_iphone;

    private TextView tv_areacode;

    private TextView tv_tip_1;

    private EditText tv_mail;
    private LinearLayout ll_vcode;
    private LinearLayout fl_iphone;
    private LinearLayout ll_mail;

    private Button imgbtn_smsSend;
    private Button btn_login;
    private String token;
    private UserBindDto userBindDto;
    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_accout_ok_layout);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);

        userBindDto=(UserBindDto)getIntent().getExtras().getSerializable(UserBindDto.class.getName());
        token=(String)getIntent().getExtras().getString("token",null);
        if(userBindDto==null||StringUtils.isEmpty(token)){
            showToast(getString(R.string.title_error_try));
            finish();
            return;
        }

        fl_iphone=(LinearLayout) this.findViewById(R.id.fl_iphone);
        ll_mail=(LinearLayout) this.findViewById(R.id.ll_mail);
        tv_tip_1=(TextView) this.findViewById(R.id.tv_tip_1);
        tv_code=(EditText) this.findViewById(R.id.tv_code);
        tv_iphone=(EditText) this.findViewById(R.id.tv_iphone);
        tv_areacode=(TextView) this.findViewById(R.id.tv_areacode);
        tv_areacode.setText(mHealthServer.getAreaCode());

        tv_mail=(EditText) this.findViewById(R.id.tv_mail);
        ll_vcode=(LinearLayout) this.findViewById(R.id.ll_vcode);
        imgbtn_smsSend=(Button) this.findViewById(R.id.imgbtn_smsSend);
        imgbtn_smsSend.setOnClickListener(this);

        btn_login=(Button)this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        if(userBindDto.getBindType().equals(VerifyTypeEnum.PHONE.name())){
            ll_vcode.setVisibility(View.VISIBLE);
            tv_iphone.setHint(getString(R.string.title_input_iphone));
            tv_tip_1.setText(getString(R.string.title_bind_new_iphone));
            fl_iphone.setVisibility(View.VISIBLE);
            ll_mail.setVisibility(View.GONE);
        }else if(userBindDto.getBindType().equals(VerifyTypeEnum.EMAIL.name())){
            ll_vcode.setVisibility(View.GONE);
           tv_mail.setHint(getString(R.string.title_input_mail));
            tv_tip_1.setText(getString(R.string.title_bind_new_mail));
            fl_iphone.setVisibility(View.GONE);
            ll_mail.setVisibility(View.VISIBLE);
        }


    }



    @Override
    public void onClick(View v) {


        final String code = tv_code.getText().toString();
        final String iphone = tv_iphone.getText().toString();
        String phonecode = tv_areacode.getText().toString();
        final String mail = tv_mail.getText().toString();


        if(v==imgbtn_smsSend){
            sendCode(iphone,phonecode);
        }else if(v==btn_login){

            if(userBindDto.getBindType().equals(VerifyTypeEnum.PHONE.name())){
                if(StringUtils.isEmpty(phonecode)){
                    showToast(getString(R.string.dialog_tip_error_area));
                }else if(StringUtils.isEmpty(iphone)){
                    showToast(getString(R.string.dialog_tip_error_phone));
                }else if(StringUtils.isEmpty(code)){
                    showToast(getString(R.string.dialog_tip_error_code));
                }else{
                    Log.d(TAG, "onClick: >>>"+userBindDto.getBindType());
                    synchronized (this) {
                        showDialog(false);
                        FormBody body = new FormBody.Builder()
                                .add("account", ""+iphone)
                                .add("code", code)
                                .add("phonecode", phonecode)
                                .add("versalt", token)
                                .add("loginType", VerifyTypeEnum.PHONE.name())
                                .build();

                        Call call = mRestService.bindAccount(body);
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
                                        ResultData<Object> result = Utility.jsonToObject(json,new TypeToken<ResultData<Object>>(){}.getType());

                                        if (result == null) {
                                            return;
                                        }

                                        if(result.getCode()== HttpFinal.CODE_200){
                                            showToast(result.getMessage(), Toast.LENGTH_LONG);
                                            Intent mainIntent = new Intent(BindAccessOkActivity.this,MainActivity.class);
                                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            ActivityUtil.startActivity(BindAccessOkActivity.this,mainIntent,true,ActionEnum.NULL);
                                        }else if(result.getCode()== HttpFinal.CODE_223){
                                            Bundle data=new Bundle();
                                            data.putString("msg",result.getMessage());
                                            ActivityUtil.startActivity(BindAccessOkActivity.this, KillLoginOutActivity.class,true,data,ActionEnum.NULL);
                                        }else{
                                            showToast(result.getMessage(), Toast.LENGTH_LONG);
                                        }
                                        return;

                                    }
                                });


                            }

                        });
                    }

                }

            }else if(userBindDto.getBindType().equals(VerifyTypeEnum.EMAIL.name())){
                if(StringUtils.isEmpty(mail)){
                    showToast(getString(R.string.dialog_tip_error_mail));
                }else{

                    synchronized (this) {
                        showDialog(false);
                        FormBody body = new FormBody.Builder()
                                .add("account", ""+mail)
                                .add("versalt", token)
                                .add("loginType", VerifyTypeEnum.EMAIL.name())
                                .build();

                        Call call = mRestService.bindAccount(body);
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
                                        ResultData<Object> result = Utility.jsonToObject(json,new TypeToken<ResultData<Object>>(){}.getType());

                                        if (result == null) {
                                            return;
                                        }

                                        if(result.getCode()== HttpFinal.CODE_200){
                                            Bundle body=new Bundle();
                                            body.putString("BINDACCOUT",mail);
                                            ActivityUtil.startActivity(BindAccessOkActivity.this,BindAccessSuccessActivity.class,false, body, ActionEnum.NEXT);
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


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private void sendCode(String iphone,String area_code){
        synchronized (this) {
            showDialog(false);
            Log.i(TAG, "sendCode: "+area_code);

            Map<String,String> param=new HashMap<String,String>();
            param.put("phonecode",area_code);
            param.put("iphone",iphone);
            Call call=mRestService.sendCodeByIphone(param);

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
                            final CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(imgbtn_smsSend, 60000, 1000, BindAccessOkActivity.this);
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
