package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;

import com.bisa.health.ble.BleWrapper;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.fragment.MyDeviceFragment;
import com.bisa.health.cust.fragment.UserCenterFragment;
import com.bisa.health.model.AppNotifiMsg;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.GsonUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener{


    private RadioButton btn_my_dev;
    private RadioButton btn_my_centre;
    private final static String TAG = "MainActivity";

    private BleWrapper mBleWrapper;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private IRestService mRestService;
    private User mUser;


    private UserCenterFragment userCenterFragment;
    private MyDeviceFragment myDeviceFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        sharedPersistor = new SharedPersistor(this);

        mUser=sharedPersistor.loadObject(User.class.getName());
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService = new RestServiceImpl(this,mHealthServer);
        setDefaultFragment();

        btn_my_dev = (RadioButton) this.findViewById(R.id.btn_my_dev);
        btn_my_dev.setOnClickListener(this);

        btn_my_centre=(RadioButton)this.findViewById(R.id.btn_my_center);
        btn_my_centre.setOnClickListener(this);
        initBle();
        initDat();
    }


    public void initBle(){
        mBleWrapper=new BleWrapper(this);
        mBleWrapper.initialize();
        if(!mBleWrapper.isBtEnabled()){
            mBleWrapper.getAdapter().enable();
        }
    }
    private void initDat(){


        Call call=mRestService.initDat();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();

                ResultData<AppNotifiMsg> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<AppNotifiMsg>>(){}.getType());
                if (result == null||result.getData()==null) {
                    return;
                }

                if(result.getData().getIsContact()>0){
                    sharedPersistor.saveObject(AppNotifiMsg.class.getName()+"-"+mUser.getUser_guid(),result.getData());
                }

            }
        });

    }

    private void setDefaultFragment()
    {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        myDeviceFragment = new MyDeviceFragment();
        transaction.replace(R.id.fl_main, myDeviceFragment);
        transaction.commit();
    }

    @Override

    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"onRestart");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }




    /**
     * 2次退出设定
     */
    private int exit_count = 2;
    private long exit_offset_temp=0;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            long exit_offset=System.currentTimeMillis();
            if(exit_offset-exit_offset_temp>5000){
                exit_count=2;
            }
            if (exit_count == 2) {
               showToast(getString(R.string.exit_click));
            }

            exit_count--;
            exit_offset_temp=exit_offset;
            if (exit_count <= 0) {
                exit_count = 2;
                exitApp();
            }

            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    底部点击转换事件
     */
    @Override
    public void onClick(View v) {

        if(v==btn_my_centre||v==btn_my_dev){

            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();

            if(v == btn_my_centre){
                if(userCenterFragment==null){
                    userCenterFragment=new UserCenterFragment();
                }
                transaction.replace(R.id.fl_main, userCenterFragment);

            }else if(v== btn_my_dev){
                if(myDeviceFragment==null){
                    myDeviceFragment=new MyDeviceFragment();
                }
                transaction.replace(R.id.fl_main, myDeviceFragment);
            }

            transaction.commit();
        }


    }



}
