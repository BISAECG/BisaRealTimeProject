package com.bisa.health.pay;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.utils.FunUtil;
import com.bisa.health.utils.WebViewUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Administrator on 2018/4/25.
 */

public class BisaServiceActivity extends BaseActivity implements View.OnClickListener{



    private static final String TAG = "BisaServiceActivity";
    private WebView webView;
    private HealthServer mHealthServer;
    private SharedPersistor sharedObject;
    private String mCurServerURL;
    private User mUser;
    private String url;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        AppManager.getAppManager().addActivity(this);

        sharedObject=new SharedPersistor(this);
        mUser=sharedObject.loadObject(User.class.getName());
        mHealthServer=sharedObject.loadObject(HealthServer.class.getName());
        webView = (WebView) findViewById(R.id.webView);
        url=mHealthServer.getDomain()+"/mi/h5/service/show?lang="+ FunUtil.h5Lang()+"&timestamp="+System.currentTimeMillis()+"&token="+mHealthServer.getToken();
        Log.i(TAG, "onCreate: "+url);
        init(url);
    }



    private void init(String url){

        WebViewUtil.buildSetting(webView);
        webView.addJavascriptInterface(new CallAndroid(), "CallAndroid");
        webView.loadUrl(url);
    }

    public class CallAndroid extends  Object{
        @JavascriptInterface
        public void callTry(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });

        }

        @JavascriptInterface
        public void flush(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BisaServiceActivity.this.finish();
                }
            });

        }
    }

    @Override
    public void onClick(View v) {



    }



}
