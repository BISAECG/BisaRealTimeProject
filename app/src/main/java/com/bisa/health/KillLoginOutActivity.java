package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;

import org.apache.commons.lang3.StringUtils;


public class KillLoginOutActivity extends BaseActivity {

    private Button killOut;
    private TextView tv_title;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private static final String TAG = "KillLoginOutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_login_out);
        sharedPersistor = new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        tv_title= (TextView) this.findViewById(R.id.tv_title);
        if(getIntent().getExtras()!=null){
            String msg=getIntent().getExtras().getString("msg",null);
            Log.i(TAG, "onCreate: "+msg);
            
            if(!StringUtils.isEmpty(msg)){
                tv_title.setText(msg);
            }
        }


        killOut=(Button)this.findViewById(R.id.positiveButton);
        killOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHealthServer.setToken(null);
                Log.i(TAG, "onClick: exit");
        ;
                mHealthServer.setToken(null);
                Log.i(TAG, "onClick: exit");
                sharedPersistor.saveObject(mHealthServer);
                sharedPersistor.delObject(User.class.getName());
                KillLoginOutActivity.this.restartApp();
            }
        });
    }
}
