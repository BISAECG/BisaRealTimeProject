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
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.AreaUtil;
import com.bisa.health.utils.CountDownTimerUtils;
import com.bisa.health.utils.GsonUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import org.apache.commons.lang3.StringUtils;

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

public class OtherWechatBindActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener{



    @NotEmpty(messageResId = R.string.dialog_tip_error_code)
    @Order(3)
    private EditText tv_code;

    @NotEmpty(messageResId = R.string.dialog_tip_error_phone)
    @Order(2)
    private EditText tv_iphone;

    @NotEmpty(messageResId = R.string.dialog_tip_error_area)
    @Order(1)
    private TextView tv_areacode;

    private Button imgbtn_smsSend;

    private Button btn_login;

    private boolean isValidation = false;

    private IRestService restService;

    private Validator validator;

    private HealthServer mHealthServer;

    private SharedPersistor sharedPersistor;

    private List<ServerDto> mListType; // 类型列表

    private User mUser;

    private static final String TAG = "ForPwdVailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherlogin_bind_activity);

        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        mListType=AreaUtil.getListArea(this);
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);
        mUser=sharedPersistor.loadObject(User.class.getName());
        restService=new RestServiceImpl(this,mHealthServer);

        tv_code=(EditText) this.findViewById(R.id.tv_code);

        tv_iphone=(EditText) this.findViewById(R.id.tv_iphone);

        tv_areacode=(TextView)this.findViewById(R.id.tv_areacode);
        tv_areacode.setText(mHealthServer.getAreaCode());

        imgbtn_smsSend=(Button) this.findViewById(R.id.imgbtn_smsSend);
        imgbtn_smsSend.setOnClickListener(this);

        btn_login=(Button)this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClick(View v) {

        final String mIphone = tv_iphone.getText().toString().trim();
        final String code = tv_code.getText().toString().trim();
        final String _areaCode=tv_areacode.getText().toString().trim();
        final String phonecode= AreaUtil.CountryCodeByPhoneCode(mListType,_areaCode).getCountryCode();

        if(v==imgbtn_smsSend){
            if(StringUtils.isEmpty(mIphone)||StringUtils.isEmpty(phonecode)){
                show_Toast(getResources().getString(R.string.other_bind_area_iphone));
                return;
            }
            sendCode();
        }else if(v==btn_login){

            validator.validate();

            if (!isValidation) {
                return;
            }

            LoadDiaLogUtil.getInstance().show(OtherWechatBindActivity.this, false);


            synchronized (this) {

                FormBody body = new FormBody.Builder()
                        .add("username", ""+mUser.getUsername())
                        .add("iphone", ""+mIphone)
                        .add("phonecode", ""+phonecode)
                        .add("code", code)
                        .build();

                Call call = restService.bindIphone(body);
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

                        Log.i(TAG, "onResponse: "+response.code());
                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadDiaLogUtil.getInstance().dismiss();
                                ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                                if(result==null){
                                    return;
                                }
                                if(result.getCode() == HttpFinal.CODE_200){
                                    sharedPersistor.saveObject(mUser);
                                    ActivityUtil.startActivity(OtherWechatBindActivity.this,MainActivity.class,true,ActionEnum.DOWN);
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

    private void sendCode(){
        synchronized (this) {
            final CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(imgbtn_smsSend, 60000, 1000, this);
            mCountDownTimerUtils.start();

            final String mIphone = tv_iphone.getText().toString();
            final String areaCode = tv_areacode.getText().toString().trim();

            Map<String,String> param=new HashMap<String,String>();
            param.put("phonecode",areaCode);
            param.put("iphone",mIphone);
            Call call=restService.sendCodeByIphone(param);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mCountDownTimerUtils.cancel();
                            mCountDownTimerUtils.onFinish();

                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String json = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadDiaLogUtil.getInstance().dismiss();
                            ResultData<Object> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<Object>>(){}.getType());
                            if(result==null){
                                return;
                            }

                            if(result.getCode() != HttpFinal.CODE_200){
                                mCountDownTimerUtils.cancel();
                                mCountDownTimerUtils.onFinish();
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
