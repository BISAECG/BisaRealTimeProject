package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.LoginTypeEnum;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;

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
    private List<UserBindDto> mList;
    private UserBindDto verifyBindDto;
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


        Log.i(TAG, "onCreate: "+ArrayList.class.getName());
        mList=sharedPersistor.flashLoad(ArrayList.class.getName(),true);
        userBindDto= (UserBindDto) getIntent().getExtras().getSerializable("BINDUSER");
        if(mList==null||userBindDto==null){
            show_Toast(getString(R.string.title_error_try));
            finish();
            return;
        }

        for(int i=0;i<mList.size();i++){

            if(mList.get(i).getLoginType()== LoginTypeEnum.PHONE){
                txt_iphone.setText(mList.get(i).getUsername());
                txt_iphone.setTag(i);
            }else if(mList.get(i).getLoginType()==LoginTypeEnum.EMAIL){
                txt_mail.setText(mList.get(i).getUsername());
                txt_mail.setTag(i);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v!=rl_iphone&&v!=rl_mail){
            return;
        }
        int index=Integer.parseInt(txt_iphone.getTag().toString());
        if(v==rl_mail){
            index=Integer.parseInt(txt_mail.getTag().toString());
        }
        verifyBindDto=mList.get(index);
            if(!verifyBindDto.getUsername().equals(getString(R.string.bind_isnot))){
                Bundle body=new Bundle();
                body.putSerializable("BINDUSER",userBindDto);
                body.putSerializable("VERIFYUSER",verifyBindDto);
                ActivityUtil.startActivity(this,BindAccessVailActivity.class,false,body, ActionEnum.NEXT);
            }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
