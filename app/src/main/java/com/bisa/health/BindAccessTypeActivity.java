package com.bisa.health;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessTypeActivity extends BaseActivity implements View.OnClickListener {


    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private static final String TAG = "BindAccessActivity";
    private TextView txt_iphone;
    private TextView txt_mail;
    private RelativeLayout rl_iphone;
    private RelativeLayout rl_mail;
    private UserBindDto userBindDto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_accout_type_layout);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());

        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);

        rl_iphone=(RelativeLayout) this.findViewById(R.id.rl_iphone);
        rl_iphone.setOnClickListener(this);

        rl_mail=(RelativeLayout)this.findViewById(R.id.rl_mail);
        rl_mail.setOnClickListener(this);

        txt_iphone=(TextView) this.findViewById(R.id.txt_iphone);
        txt_mail=(TextView)this.findViewById(R.id.txt_mail);

        userBindDto= (UserBindDto) getIntent().getExtras().getSerializable(UserBindDto.class.getName());
        if(userBindDto==null){
            showToast(getString(R.string.title_error_try));
            finish();
            return;
        }

        if(StringUtils.isEmpty(userBindDto.getPhone())){
            txt_iphone.setText(getString(R.string.bind_isnot));
            rl_iphone.setTag(null);
        }else{
            txt_iphone.setText(userBindDto.getPhone());
            rl_iphone.setTag(VerifyTypeEnum.PHONE.name());
        }


        if(StringUtils.isEmpty(userBindDto.getEmail())){
            txt_mail.setText(getString(R.string.bind_isnot));
            rl_mail.setTag(null);
        }else{
            txt_mail.setText(userBindDto.getEmail());
            rl_mail.setTag(VerifyTypeEnum.EMAIL.name());
        }


    }


    @Override
    public void onClick(View v) {
        if(v!=rl_iphone&&v!=rl_mail){
            return;
        }
        if(v.getTag()!=null){
            userBindDto.setVerifyType(v.getTag().toString());
            Bundle body=new Bundle();
            body.putSerializable(UserBindDto.class.getName(),userBindDto);
            ActivityUtil.startActivity(this,BindAccessVailActivity.class,false,body, ActionEnum.NEXT);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
