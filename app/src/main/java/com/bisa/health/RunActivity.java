package com.bisa.health;

import android.accounts.AccountAuthenticatorResponse;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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
import com.bisa.health.cust.view.CustAreaCodePopView;
import com.bisa.health.model.HServer;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.UpdateVersion;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.FileIOKit;
import com.bisa.health.utils.LoadDownTimerUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RunActivity extends Activity implements View.OnClickListener, PopupWindow.OnDismissListener, IAdapterClickInterFace {


    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private SharedPersistor sharedPersistor;
    private IRestService restService;
    private HServer hServer;

    private final static int loadTime = 15 * 1000;
    private Class<?> runClz;
    private static final String TAG = "RunActivity";

    private Locale locale = Locale.getDefault();
    private final Gson gson = new Gson();
    private Button btn_onerun;
    private RelativeLayout rl_loadmian;
    private HealthServer mHealthServer;
    private LoadDownTimerUtils mCountDownTimerUtils = null;
    private ImageView img_load;
    private TextView tv_load;
    private BisaServerAdapter mBisaServerAdapter;
    private List<ServerDto> mListType; // 类型列表
    private CustAreaCodePopView custAreaCodePopView;
    private String lang;
    private int version;
    private UpdateVersion mUv;
    private Handler mHandler = new Handler();
    @Override
    public synchronized void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.run_activity);
        String lan = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        lang = lan + "-" + country;

        img_load = (ImageView) this.findViewById(R.id.img_load);
        tv_load = (TextView) this.findViewById(R.id.tv_load);
        rl_loadmian = (RelativeLayout) this.findViewById(R.id.rl_loadmian);

        btn_onerun = (Button) this.findViewById(R.id.btn_onerun);
        btn_onerun.setOnClickListener(this);

        if (sharedPersistor == null)
            sharedPersistor = new SharedPersistor(this);

        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());

        restService = new RestServiceImpl(RunActivity.this, mHealthServer);

        mUv = sharedPersistor.loadObject(UpdateVersion.class.getName());

        if (mUv != null && mUv.isUpdate()) {
            ActivityUtil.startActivity(RunActivity.this, UpdateActivity.class, true, ActionEnum.NULL);
            return;
        }

        try {
            version = getPackageManager().getPackageInfo("com.bisa.health", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }


        Map<String, String> syncParms = new HashMap<String, String>();
        syncParms.put("syncversion", "" + version);
        Call call = restService.syncVersion(syncParms);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (response.code() == HttpFinal.CODE_200) {
                    try {
                        String serverVersion = json;
                        if (!StringUtils.isEmpty(serverVersion)) {
                            UpdateVersion uv = new UpdateVersion();
                            uv.setAppVserion(version);
                            uv.setServerVersion(Integer.parseInt(serverVersion));
                            sharedPersistor.saveObject(uv);
                        }
                    }catch (Exception e){
                        Log.i(TAG, "onResponse: "+e);
                    }

                }

            }
        });

        if (mHealthServer != null) {
            ActivityUtil.startActivity(RunActivity.this, LoginActivity.class, true, ActionEnum.NULL);
            return;
        }

        /**
         * 同步服务器列表
         */


        rl_loadmian.setVisibility(View.VISIBLE);
        mCountDownTimerUtils = new LoadDownTimerUtils(tv_load, loadTime, 1000, RunActivity.this);
        mCountDownTimerUtils.start();
        syncServer();

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                RunActivity.this, R.anim.loading_animation);
        img_load.setAnimation(hyperspaceJumpAnimation);

    }


    private void syncServer() {

        try {
            hServer=sharedPersistor.loadObject(HServer.class.getName());

            if(hServer==null){
                InputStream is = getResources().getAssets().open("server-default.json");
                byte[] serverByte = FileIOKit.FromInputStreamToByte(is);
                hServer = gson.fromJson(new String(serverByte, "UTF-8"), HServer.class);
                sharedPersistor.saveObject(hServer);
            }

            Map<String, String> param = new HashMap<String, String>();
            param.put("version", hServer.getVersion());
            param.put("language", lang);
            Call call = restService.downServerList(param);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mCountDownTimerUtils != null)
                                mCountDownTimerUtils.cancel();
                            rl_loadmian.setVisibility(View.GONE);
                            btn_onerun.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final byte[] rServerByte = response.body().bytes();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mCountDownTimerUtils != null)
                                mCountDownTimerUtils.cancel();
                            rl_loadmian.setVisibility(View.GONE);
                            btn_onerun.setVisibility(View.VISIBLE);

                            if (response.code() == HttpFinal.SUCCESS) {

                                try {
                                    hServer = gson.fromJson(new String(rServerByte, "UTF-8"), HServer.class);
                                    if(hServer!=null){
                                        sharedPersistor.saveObject(hServer);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                    });



                }
            });


        } catch (Exception ex) {
            Log.i(TAG, "initUpdate: " + ex.getMessage());
        }

    }


    @Override
    public void onClick(View v) {
        if (v == btn_onerun) {
            hServer=sharedPersistor.loadObject(HServer.class.getName());
            mListType = hServer.getList();
            mBisaServerAdapter = new BisaServerAdapter(this);
            mBisaServerAdapter.setmObjects(mListType);
            mBisaServerAdapter.notifyDataSetChanged();

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
        if (pos >= 0) {
            ServerDto serverBisa = mListType.get(pos);
            HealthServer healthServer = new HealthServer();
            healthServer.setDatServer(serverBisa.getDatserver());
            healthServer.setDomain(serverBisa.getDomain());
            healthServer.setShopserver(serverBisa.getShopserver());
            healthServer.setAreaCode(serverBisa.getPhoneCode());
            healthServer.setTimeZone(serverBisa.getTime_zone());
            sharedPersistor.saveObject(healthServer);
            ActivityUtil.startActivity(RunActivity.this, LoginActivity.class, true, ActionEnum.NULL);
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
