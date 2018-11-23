package com.bisa.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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


public class UserSexActivity extends BaseActivity  implements  OnClickListener{


    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private User mUser;
    private HealthPath mHealthPath;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    public ImageButton backCall;
    public TextView tv_seuucess;
    public EditText edit_name;
    public RelativeLayout rl_male;
    public RelativeLayout rl_female;
    public ImageView iv_male;
    public ImageView iv_famale;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usersex_activity);
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

        edit_name=this.findViewById(R.id.edit_name);

        rl_male=this.findViewById(R.id.rl_male);
        rl_male.setOnClickListener(this);

        rl_female=this.findViewById(R.id.rl_female);
        rl_female.setOnClickListener(this);

        iv_famale=this.findViewById(R.id.iv_famale);
        iv_male=this.findViewById(R.id.iv_male);
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
        int  sex=getIntent().getIntExtra("sex",-1);
        if(sex==0){

            iv_male.setVisibility(View.VISIBLE);
            iv_famale.setVisibility(View.GONE);
        }else if(sex==-1){
            iv_male.setVisibility(View.GONE);
            iv_famale.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onClick(View v) {
        if(v==backCall){
            ActivityUtil.finishAnim(this,ActionEnum.BACK);
        }else if(v==tv_seuucess){
            int sex=0;
            if(iv_famale.getVisibility()==View.VISIBLE){
                sex=1;
            }
            Intent intent = new Intent();
            intent.putExtra("sex", sex);
            this.setResult(UserInfoActivity.CALL_SEX_CODE, intent);
            finish();
        } else if(v==rl_male){
            iv_male.setVisibility(View.VISIBLE);
            iv_famale.setVisibility(View.GONE);
        }else if(v==rl_female){
            iv_male.setVisibility(View.GONE);
            iv_famale.setVisibility(View.VISIBLE);
        }
    }
}
