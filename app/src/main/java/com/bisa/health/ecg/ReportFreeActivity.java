package com.bisa.health.ecg;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.ecg.dao.ReportDaoImpl;
import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.utils.FunUtil;
import com.bisa.health.utils.HttpUtil;
import com.bisa.health.utils.WebViewUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportFreeActivity extends BaseActivity {

    private static final String TAG = "ReportFreeActivity";
    private WebView webView;
    private HealthServer healthServer;
    private SharedPersistor sharedObject;
    private String mCurServerURL;
    private User mUser;
    private IReportDao iappReportDao;
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private String action;
    private int type;
    private String rNumber;
    private int user_guid;
    private String url;
    private String postDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        AppManager.getAppManager().addActivity(this);

        sharedObject=new SharedPersistor(this);
        mUser=sharedObject.loadObject(User.class.getName());
        healthServer=sharedObject.loadObject(HealthServer.class.getName());
        iappReportDao=new ReportDaoImpl(this);
        webView = (WebView) findViewById(R.id.webView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        action=getIntent().getStringExtra("action");
        type=getIntent().getIntExtra("type",0);
        user_guid=getIntent().getIntExtra("user_guid",0);
        rNumber=getIntent().getStringExtra("rNumber");
        if(!StringUtils.isEmpty(rNumber)&&type!=0){

            if(Integer.valueOf(type)== ReportType.MINUTE.getValue()){
                AppReport appReport=iappReportDao.loadByGuidAndNumber(mUser.getUser_guid(),rNumber);
                url=action+"?lang="+ FunUtil.h5Lang();
                init(url,appReport);

            }


        }

    }

    private void init(String url,AppReport mReport){

        WebViewUtil.buildSetting(webView);
        webView.addJavascriptInterface(new CallAndroid(), "CallAndroid");

        File ecdFile=new File(mReport.getEcgdat());
        if(!ecdFile.exists()){
            showToast(getResources().getString(R.string.freereport_file_exists));
            finish();
            return ;
        }
        byte[] ecdBytes=null;
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(ecdFile);
            ecdBytes=new byte[fis.available()];
            fis.read(ecdBytes);
        } catch (IOException e) {
            return ;
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    return ;
                }
            }
        }

        byte[] data= Base64.encode(ecdBytes,Base64.NO_WRAP);
        Map<String,String> param=new HashMap<String,String>();
        param.put("ecgdat",new String(data));
        param.put("sex",""+mUser.getSex().getValue());
        param.put("headUrl",""+mUser.getUri_pic());
        param.put("ctime",""+mReport.getStart_time());

        postDate = HttpUtil.packParamsToWebView(param);

        if(postDate==null) return ;
        //WebView加载web资源
        Log.i(TAG, "init: "+url);
        webView.postUrl(url, postDate.getBytes());
    }

    public class CallAndroid extends  Object{
        @JavascriptInterface
        public void callTry(){
            Log.i(TAG, "callTry: ================");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(postDate==null) return ;
                    webView.postUrl(url, postDate.getBytes());
                }
            });

        }


    }


}
