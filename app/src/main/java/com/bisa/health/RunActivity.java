package com.bisa.health;

import android.accounts.AccountAuthenticatorResponse;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.adapter.BisaServerAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.CustAreaCodePopView;
import com.bisa.health.model.HServer;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.UpdateVersion;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.UsersVerifiedEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.FileIOKit;
import com.bisa.health.utils.LoadDownTimerUtils;
import com.bisa.health.utils.MD5Help;
import com.bisa.health.utils.ToastUtil;
import com.google.gson.Gson;
import com.tencent.android.tpush.XGPushManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RunActivity extends Activity implements View.OnClickListener,PopupWindow.OnDismissListener,IAdapterClickInterFace{


    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private SharedPersistor sharedPersistor;
    private IRestService restService;
    private HServer hServer;
    private User mUser;
    private final static int loadTime = 15 * 1000;
    private Class<?> runClz;
    private static final String TAG = "RunActivity";

    private Locale locale = Locale.getDefault();
    private final Gson gson = new Gson();
    private String sVsersion;
    private Button btn_onerun;
    private RelativeLayout rl_loadmian;
    private HealthServer mHealthServer;
    private LoadDownTimerUtils mCountDownTimerUtils=null;
    private ImageView img_load;
    private TextView tv_load;
    private BisaServerAdapter mBisaServerAdapter;
    private List<ServerDto> mListType; // 类型列表
    private CustAreaCodePopView custAreaCodePopView;
    private String lang;
    private int version;
    private UpdateVersion mUv;
    @Override
    public synchronized void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_activity);
        String lan = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        lang = lan + "-" + country;

        img_load=(ImageView) this.findViewById(R.id.img_load);
        tv_load=(TextView) this.findViewById(R.id.tv_load);
        rl_loadmian=(RelativeLayout) this.findViewById(R.id.rl_loadmian);

        btn_onerun=(Button) this.findViewById(R.id.btn_onerun);
        btn_onerun.setOnClickListener(this);

        if(sharedPersistor==null)
            sharedPersistor=new SharedPersistor(this);
        mUser=sharedPersistor.loadObject(User.class.getName());
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        restService = new RestServiceImpl(RunActivity.this,mHealthServer);
        mUv=sharedPersistor.loadObject(UpdateVersion.class.getName());

        if(mUv!=null&&mUv.isUpdate()){
            ActivityUtil.startActivity(RunActivity.this,UpdateActivity.class,true,ActionEnum.NULL);
            return;
        }

        try {
            version=getPackageManager().getPackageInfo("com.bisa.health",0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }


        Map<String,String> syncParms=new HashMap<String,String>();
        syncParms.put("syncversion",""+version);
        Call call=restService.syncVersion(syncParms);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json=response.body().string();
                if(response.code()== HttpFinal.CODE_200){
                    String serverVersion=json;
                    if(!StringUtils.isEmpty(serverVersion)){
                        UpdateVersion uv=new UpdateVersion();
                        uv.setAppVserion(version);
                        uv.setServerVersion(Integer.parseInt(serverVersion));
                        sharedPersistor.saveObject(uv);
                    }
                }

            }
        });



        if(mHealthServer!=null){//已经登入
            rl_loadmian.setVisibility(View.GONE);
            mHandler.sendEmptyMessage(HttpFinal.SUCCESS);

        }else{
            rl_loadmian.setVisibility(View.VISIBLE);
            mCountDownTimerUtils = new LoadDownTimerUtils(tv_load, loadTime, 1000, RunActivity.this);
            mCountDownTimerUtils.start();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mCountDownTimerUtils!=null)
                        mCountDownTimerUtils.cancel();
                        rl_loadmian.setVisibility(View.GONE);
                        btn_onerun.setVisibility(View.VISIBLE);
                }
            },loadTime);

            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    RunActivity.this, R.anim.loading_animation);
            img_load.setAnimation(hyperspaceJumpAnimation);
            syncServer();

        }



    }


    /**
     *同步服务器列表
     */
    private void syncServer() {

        try {
            File bisDir=new File(this.getApplicationContext().getFilesDir().getAbsolutePath()+"/bisserver");
            if (!bisDir.exists()){
                bisDir.mkdirs();
            }
            final File serverFile = new File(this.getApplicationContext().getFilesDir().getAbsolutePath()+"/bisserver","server-"+lang + ".json");
            sVsersion="20181111";
            if (!serverFile.exists()) {
                byte[] serverByte = FileIOKit.FromFileToByte(serverFile);
                if(serverByte!=null){
                    hServer = gson.fromJson(new String(serverByte, "UTF-8"),HServer.class);
                    if(hServer!=null){
                        sVsersion = hServer.getVersion();
                    }
                }
            }
            Map<String, String> param = new HashMap<String, String>();
            param.put("version", sVsersion);
            param.put("lang", lang);
            Call call = restService.downServerList(param);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mCountDownTimerUtils!=null)
                                mCountDownTimerUtils.cancel();
                            rl_loadmian.setVisibility(View.GONE);
                            btn_onerun.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                        if(serverFile.exists()){
                            serverFile.delete();
                        }
                        if (response.code() == HttpFinal.SUCCESS) {
                            byte[] rServerByte = response.body().bytes();
                            Log.d(TAG, "onResponse: " + (new String(rServerByte, "UTF-8")));
                            if (rServerByte != null) {
                               FileIOKit.FromByteToFile(rServerByte, serverFile);
                            }

                        }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mCountDownTimerUtils!=null)
                                mCountDownTimerUtils.cancel();
                            rl_loadmian.setVisibility(View.GONE);
                            btn_onerun.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });


        } catch (Exception ex) {
            Log.d(TAG, "initUpdate: "+ ex.getMessage());
        }

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            switch (msg.what){
                case HttpFinal.ERROR:
                case HttpFinal.FAIL:
                    ActivityUtil.startActivity(RunActivity.this,LoginActivity.class,true,ActionEnum.NULL);
                     break;
                case HttpFinal.SUCCESS:
                    runLogin();
                    break;
            }



        }
    };

    private void runLogin(){

        if(!StringUtils.isEmpty(mHealthServer.getToken())){
            Log.i(TAG, "Token: "+mHealthServer.getToken());
            XGPushManager.bindAccount(this, MD5Help.md5EnBit32(mHealthServer.getToken()));
            if(mUser!=null){
                Log.i(TAG, "runLogin: "+mUser);
                    if(mUser.getVerified()== UsersVerifiedEnum.VERIFIED){
                        ActivityUtil.startActivity(RunActivity.this,MainActivity.class,true,ActionEnum.NULL);
                        return;
                    }

            }
        }
        ActivityUtil.startActivity(RunActivity.this,LoginActivity.class,true,ActionEnum.NULL);

    }
    @Override
    public void onClick(View v) {
        if(v==btn_onerun){
            final File file = new File(this.getApplicationContext().getFilesDir().getAbsolutePath()+"/bisserver","server-"+lang + ".json");
            try {
                if(file.exists()&&FileIOKit.is_file_status(file)){
                    Log.i(TAG, "onClick: >>>1>>"+(hServer==null));
                    byte[] serverByte = FileIOKit.FromFileToByte(file);
                    if(serverByte!=null){
                        hServer = gson.fromJson(new String(serverByte, "UTF-8"),HServer.class);
                    }

                }

                if(hServer==null){
                    InputStream is=getResources().getAssets().open("server-default.json");
                    byte[] serverByte = FileIOKit.FromInputStreamToByte(is);
                    hServer = gson.fromJson(new String(serverByte, "UTF-8"),HServer.class);
                }
            }catch (IOException e){
                ToastUtil.getInstance(this).show(this.getResources().getString(R.string.title_error_try));
                finish();
                return;
            }
            mListType=hServer.getList();
            mBisaServerAdapter = new BisaServerAdapter(this);
            mBisaServerAdapter.setmObjects(mListType);

            custAreaCodePopView = new CustAreaCodePopView(this);
            custAreaCodePopView.setAdatper(mBisaServerAdapter);
            custAreaCodePopView.setItemListener(this);
            custAreaCodePopView.setOnDismissListener(this);
            custAreaCodePopView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            setBackgroundAlpha(0.5f);

        }
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    @Override
    public void onItemClick(int pos) {
        if(pos>=0){
            ServerDto serverBisa = mListType.get(pos);
            HealthServer healthServer=new HealthServer();
            healthServer.setDatServer(serverBisa.getDatserver());
            healthServer.setDomain(serverBisa.getDomain());
            healthServer.setShopserver(serverBisa.getShopserver());
            healthServer.setAreaCode(serverBisa.getPhoneCode());
            healthServer.setTimeZone(serverBisa.getTime_zone());
            sharedPersistor.saveObject(healthServer);
            ActivityUtil.startActivity(RunActivity.this,LoginActivity.class,true,ActionEnum.NULL);
            return;
        }

    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = alpha;
        this.getWindow().setAttributes(lp);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
