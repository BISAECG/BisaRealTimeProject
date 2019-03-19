package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.GsonUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


public class UserNameActivity extends BaseActivity  implements  OnClickListener{

    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private User mUser;
    private HealthPath mHealthPath;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    private static final String TAG = "UserNameActivity";
    public ImageButton backCall;
    public TextView tv_seuucess;
    public RelativeLayout rl_clear;
    public EditText edit_name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_activity);
        Log.i(TAG, "onCreate: ");
        AppManager.getAppManager().addActivity(this);

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthPath = sharedPersistor.loadObject(HealthPath.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService = new RestServiceImpl(this, mHealthServer);
        backCall=this.findViewById(R.id.ibtn_back);
        backCall.setOnClickListener(this);
        tv_seuucess=this.findViewById(R.id.tv_seuucess);
        tv_seuucess.setOnClickListener(this);

        rl_clear=this.findViewById(R.id.rl_clear);
        rl_clear.setOnClickListener(this);

        edit_name=this.findViewById(R.id.edit_name);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String nickname=getIntent().getStringExtra("username");
        edit_name.setText(nickname);
    }

    @Override
    public void onClick(View v) {
        if(v==backCall){
           ActivityUtil.finishAnim(this,ActionEnum.BACK);
        }else if(v==tv_seuucess){

            String username= edit_name.getText().toString().trim();

            if(!StringUtils.isEmpty(username)){

                showDialog(false);
                FormBody body = new FormBody.Builder()
                        .add("username", username)
                        .build();

                Call call=mRestService.updateUsername(body);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogDismiss();
                                showToast(getResources().getString(R.string.server_error));

                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialogDismiss();
                                ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                                if(result==null){
                                    return;
                                }
                                if(result.getCode() == HttpFinal.CODE_200){
                                    sharedPersistor.saveObject(result.getData());

                                    Intent intent = new Intent();
                                    intent.putExtra("username", edit_name.getText().toString().trim());
                                    Log.i(TAG, "onClick: "+edit_name.getText().toString().trim());
                                    UserNameActivity.this.setResult(UserInfoActivity.CALL_USERNAME_CODE, intent);
                                    finish();
                                }else{
                                    showToast(result.getMessage());
                                }


                            }
                        });
                    }
                });
            }

        }else if(v==rl_clear){
            edit_name.setText("");
        }
    }
}
