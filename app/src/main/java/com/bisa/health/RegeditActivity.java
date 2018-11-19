package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.auth.CryptogramService;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.dao.IEventDao;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.ArrayUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.MD5Help;
import com.bisa.health.utils.Utility;
import com.google.gson.Gson;
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

public class RegeditActivity extends BaseActivity implements  View.OnClickListener, Validator.ValidationListener {

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

    private TextView txt_login_switch;


    private Button btn_login;
    private int navigationHeight;

    private Validator validator;
    private RestServiceImpl restService;

    private IEventDao eventDao;

    private IReportDao iappReportDao;

    private static final Gson gson = new Gson();


    //国家地区代码
    private HealthServer mHealthServer;

    private ImageView imageView;
    private TextView txt_switch_area;
    private ImageButton ibtn_back;
    private List<ServerDto> liatArea;


    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_regedit);

        Log.i(TAG, "onCreate: ");

        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        restService = new RestServiceImpl(this,mHealthServer);
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);

        liatArea=sharedPersistor.loadObject(ServerDto.class.getName());

        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);



        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigationHeight = getResources().getDimensionPixelSize(resourceId);


        txt_login_switch = (TextView) this.findViewById(R.id.txt_login_switch);
        txt_login_switch.setOnClickListener(this);

        ibtn_back=(ImageButton) this.findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);

        txt_switch_area = (TextView) this.findViewById(R.id.txt_switch_area);
        txt_switch_area.setOnClickListener(this);

        tv_areacode = (TextView) this.findViewById(R.id.tv_areacode);
        tv_areacode.setText(mHealthServer.getAreaCode());
        tv_verify = (EditText) this.findViewById(R.id.tv_verify);
        tv_iphone = (EditText) this.findViewById(R.id.tv_iphone);


        imageView = (ImageView) this.findViewById(R.id.img_wechat);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }


    @Override
    public void onClick(View v) {


        if (v == btn_login) {


            validator.validate();
            final String username = tv_iphone.getText().toString();
            final String phonecode = tv_areacode.getText().toString();
            final String code = tv_verify.getText().toString();


            if (!isValidation) {
                return;
            }
            /*
            获取账号的权限
             */
            LoadDiaLogUtil.getInstance().show(RegeditActivity.this, false);
            synchronized (this) {

                String timeStamp=""+DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                String digest= CryptogramService.getInstance().hmacDigest(
                        ArrayUtil.sort(new String[]{"username","code","area_code","time_zone","phonecode","clientKey","timeStamp"},
                                new String[]{username,code,mHealthServer.getAreaCode(),mHealthServer.getTimeZone(),phonecode,username,timeStamp}));

                FormBody body = new FormBody.Builder()
                        .add("username", username)
                        .add("code", code)
                        .add("time_zone",mHealthServer.getTimeZone())
                        .add("phonecode",phonecode)
                        .add("area_code",mHealthServer.getAreaCode())
                        .add("clientKey", username)
                        .add("digest",digest)
                        .add("timeStamp",timeStamp)
                        .build();

                Call call = restService.iphoneRegedit(body);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: ");

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

                                ResultData<User> result = Utility.jsonToObject(json,new TypeToken<ResultData<User>>(){}.getType());

                                if (result == null) {
                                    return;
                                }

                                if (result.getCode() == HttpFinal.CODE_201) {
                                    sharedPersistor.saveObject(result.getData());
                                    mHealthServer.setToken(result.getToken());
                                    sharedPersistor.saveObject(mHealthServer);
                                    if(!StringUtils.isEmpty(mHealthServer.getToken())){
                                        XGPushManager.bindAccount(RegeditActivity.this, MD5Help.md5EnBit32(mHealthServer.getToken()));
                                    }
                                    ActivityUtil.startActivity(RegeditActivity.this,MainActivity.class,true,ActionEnum.NULL);
                                }else {
                                    show_Toast(result.getMessage());
                                }
                                return;
                            }
                        });


                    }

                });


            }


        } else if (v == txt_login_switch) {
            ActivityUtil.startActivity(this,LoginActivity.class,true,ActionEnum.BACK);
        }else if(v==ibtn_back){
            ActivityUtil.finishAnim(this,ActionEnum.BACK);

        }else if(v==txt_switch_area){
            sharedPersistor.delObject(HealthServer.class.getName());
            ActivityUtil.startActivity(this,RunActivity.class,false,ActionEnum.NULL);
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
            show_Toast(message);
            break;
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }
}


