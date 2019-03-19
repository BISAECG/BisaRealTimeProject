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
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

import org.apache.commons.lang3.StringUtils;

public class UserAgeActivity extends BaseActivity implements View.OnClickListener {
    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private static final String TAG = "UserAgeActivity";
    public ImageButton backCall;
    public TextView tv_seuucess;
    public RelativeLayout rl_clear;
    public EditText edit_age;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_age_activity);
        Log.i(TAG, "onCreate: ");
        AppManager.getAppManager().addActivity(this);

        sharedPersistor = new SharedPersistor(this);
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        backCall=this.findViewById(R.id.ibtn_back);
        backCall.setOnClickListener(this);
        tv_seuucess=this.findViewById(R.id.tv_seuucess);
        tv_seuucess.setOnClickListener(this);

        rl_clear=this.findViewById(R.id.rl_clear);
        rl_clear.setOnClickListener(this);

        edit_age=this.findViewById(R.id.edit_age);
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
        String age=getIntent().getStringExtra("age");
        if(!StringUtils.isEmpty(age))
            edit_age.setText(""+age);
    }

    @Override
    public void onClick(View v) {
        if(v==backCall){
            ActivityUtil.finishAnim(this, ActionEnum.BACK);
        }else if(v==tv_seuucess){
            Intent intent = new Intent();
            intent.putExtra("age", edit_age.getText().toString().trim());
            this.setResult(UserInfoActivity.CALL_AGE_CODE, intent);
            finish();
        }else if(v==rl_clear){
            edit_age.setText("");
        }
    }

}
