package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;


public class KillLoginOutActivity extends BaseActivity {

    private Button killOut;
    private TextView tv_title;
    private static final String TAG = "KillLoginOutActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_login_out);
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
                KillLoginOutActivity.this.restartApp();
            }
        });
    }
}
