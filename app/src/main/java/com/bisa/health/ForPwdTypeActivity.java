package com.bisa.health;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.ForGetPwdDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class ForPwdTypeActivity extends BaseActivity implements View.OnClickListener {


    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private static final String TAG = "ForPwdTypeActivity";
    private TextView txt_iphone;
    private TextView txt_mail;
    private RelativeLayout rl_iphone;
    private RelativeLayout rl_mail;
    private ForGetPwdDto forGetPwdDto;
    private List<ForGetPwdDto> mList = new ArrayList<ForGetPwdDto>();


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


        forGetPwdDto= (ForGetPwdDto) getIntent().getExtras().getSerializable(ForGetPwdDto.class.getName());
        if(forGetPwdDto==null){
            showToast(getString(R.string.title_error_try));
            finish();
            return;
        }

        if(!StringUtils.isEmpty(forGetPwdDto.getPhone())){
            txt_iphone.setText(forGetPwdDto.getPhone());
            rl_iphone.setTag(VerifyTypeEnum.PHONE.name());
        }
        if(!StringUtils.isEmpty(forGetPwdDto.getEmail())){
            txt_mail.setText(forGetPwdDto.getEmail());
            rl_mail.setTag(VerifyTypeEnum.EMAIL.name());
        }

    }


    @Override
    public void onClick(View v) {


        if(v.getTag()==null){
            return;
        }
        forGetPwdDto.vLoginType(v.getTag().toString());
        Bundle body=new Bundle();
        body.putSerializable(ForGetPwdDto.class.getName(),forGetPwdDto);
        ActivityUtil.startActivity(this,ForPwdVailActivity.class,false,body, ActionEnum.NEXT);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
