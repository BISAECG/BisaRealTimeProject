package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;


/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessSuccessActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BindAccessSuccessActivity";
    private TextView tv_title_tip;
    private Button btn_login;
    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private String bindAccout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_accout_success_layout);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);
        bindAccout=getIntent().getExtras().getString("BINDACCOUT","");
        tv_title_tip=(TextView)this.findViewById(R.id.tv_title_tip);
        String _strtext=getString(R.string.tip_bind_success_mail);
        String fromatStr=String.format(_strtext,""+ bindAccout);
        tv_title_tip.setText(fromatStr);
        btn_login=(Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(v==btn_login){
            Intent mainIntent = new Intent(BindAccessSuccessActivity.this,MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityUtil.startActivity(BindAccessSuccessActivity.this,mainIntent,true, ActionEnum.NULL);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
