package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.dto.UserPwdDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/25.
 */

public class ForPwdUserActivity extends BaseActivity implements  Validator.ValidationListener,View.OnClickListener{


    private static final String TAG = "ForPwdUserActivity";

    @NotEmpty(messageResId = R.string.dialog_tip_error_username)
    @Order(2)
    private EditText tv_iphone;

    private boolean isValidation = false;
    private Validator validator;
    private Button btn_commit;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private RestServiceImpl restService;

    //国家地区代码
    private static final Gson gson = new Gson();
    private Locale locale = Locale.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getpwd_s1_activity);

        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);


        tv_iphone = (EditText) this.findViewById(R.id.tv_iphone);
        btn_commit=(Button) this.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
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
            show_Toast(message);
            break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btn_commit){

            validator.validate();
            final String username = tv_iphone.getText().toString();

            if (!isValidation ) {
                return;
            }
            LoadDiaLogUtil.getInstance().show(ForPwdUserActivity.this, false);
            restService = new RestServiceImpl(this,mHealthServer);



            synchronized (this) {

                Map<String,String> param=new HashMap<String,String>();
                param.put("username",username);
                Call call = restService.findAccount(param);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show_Toast(getResources().getString(R.string.server_error));
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
                                ResultData<List<UserBindDto>> result = Utility.jsonToObject(json,new TypeToken< ResultData<List<UserBindDto>>>(){}.getType());

                                if (result == null) {
                                    return;
                                }

                                if(result.getCode()==HttpFinal.CODE_200){
                                    Bundle body=new Bundle();
                                    UserPwdDto userPwdDto=new UserPwdDto(-1,result.getData());
                                    Log.i(TAG,userPwdDto.toString());
                                    body.putSerializable(UserPwdDto.class.getName(),userPwdDto);
                                    ActivityUtil.startActivity(ForPwdUserActivity.this,ForPwdTypeActivity.class,false, body,ActionEnum.NEXT);
                                }else{
                                    show_Toast(result.getMessage());
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
