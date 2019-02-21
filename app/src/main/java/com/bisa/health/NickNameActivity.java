package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

public class NickNameActivity extends BaseActivity implements View.OnClickListener {
    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private User mUser;
    private HealthPath mHealthPath;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    private static final String TAG = "NickNameActivity";
    public ImageButton backCall;
    public TextView tv_seuucess;
    public RelativeLayout rl_clear;
    public EditText edit_name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nickname_activity);
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
        String nickname=getIntent().getStringExtra("nickname");
        edit_name.setText(nickname);
    }

    @Override
    public void onClick(View v) {
        if(v==backCall){
            ActivityUtil.finishAnim(this, ActionEnum.BACK);
        }else if(v==tv_seuucess){
            Intent intent = new Intent();
            intent.putExtra("nickname", edit_name.getText().toString().trim());
            this.setResult(UserInfoActivity.CALL_NAME_CODE, intent);
            finish();
        }else if(v==rl_clear){
            edit_name.setText("");
        }
    }

}
