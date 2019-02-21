package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisa.health.model.dto.ForGetPwdDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.utils.ActivityUtil;

/**
 * Created by Administrator on 2018/4/25.
 */

public class ForPwdOkActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_title_tip;
    private TextView tv_title_val;

    private Button btn_login;

    private ForGetPwdDto forGetPwdDto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getpwd_s4_activity);

        forGetPwdDto=(ForGetPwdDto)getIntent().getExtras().getSerializable(ForGetPwdDto.class.getName());


        tv_title_tip=(TextView) this.findViewById(R.id.tv_title_tip);
        tv_title_val=(TextView) this.findViewById(R.id.tv_title_val);
        btn_login=(Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        if(forGetPwdDto.vLoginType().equals(VerifyTypeEnum.PHONE.name())){
            tv_title_tip.setText(getString(R.string.tip_forget_iphone_ok));
            tv_title_val.setText(forGetPwdDto.getPhone());
        }else if(forGetPwdDto.vLoginType().equals(VerifyTypeEnum.EMAIL.name())){
            tv_title_tip.setText(getString(R.string.tip_forget_mail_ok));
            tv_title_val.setText(forGetPwdDto.getEmail());
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btn_login){
            ActivityUtil.startActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP,this,LoginPwdActivity.class,true, ActionEnum.DOWN);
        }
    }
}
