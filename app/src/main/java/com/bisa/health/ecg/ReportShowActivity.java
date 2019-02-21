package com.bisa.health.ecg;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.TextView;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.ecg.dao.ReportDaoImpl;
import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.ecg.model.ReportListDto;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.FunUtil;
import com.bisa.health.utils.WebViewUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ReportShowActivity extends BaseActivity {

    private static final String TAG = "ReportShowActivity";
    private WebView webView;
    private SharedPersistor sharedObject;
    private HealthServer mHealthServer;
    private User mUser;
    private IReportDao iappReportDao;
    private final Gson gson=new Gson();
    private String action;
    private int type;
    private int  year;
    private int  month;
    private int  day;
    private String url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        AppManager.getAppManager().addActivity(this);
        sharedObject=new SharedPersistor(this);
        mUser=sharedObject.loadObject(User.class.getName());
        mHealthServer=sharedObject.loadObject(HealthServer.class.getName());
        iappReportDao=new ReportDaoImpl(this);
        webView = (WebView) findViewById(R.id.webView);

        action=getIntent().getStringExtra("action");
        type=getIntent().getIntExtra("type",0);
        TextView titleTv= (TextView) this.getFragmentManager().findFragmentById(R.id.abar_title).getView().findViewById(R.id.tv_title);
        if(type== ReportType.HOUR24.getValue()||type== ReportType.MINUTE.getValue()){
            titleTv.setText(getResources().getString(R.string.title_head_report));
        }else{
            titleTv.setText(getResources().getString(R.string.title_head_xixin));
        }
        year=getIntent().getIntExtra("year",0);
        month=getIntent().getIntExtra("month",0);
        day=getIntent().getIntExtra("day",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
    //

    private void init(){
        webView = (WebView) findViewById(R.id.webView);
        WebViewUtil.buildSetting(webView);
        webView.addJavascriptInterface(new CallAndroid(), "CallAndroid");
        //WebView加载web资源
        url=action+"/"+year+"/"+month+"/"+day+"/"+type+"?lang="+ FunUtil.h5Lang()+"&timestamp="+System.currentTimeMillis()+"&token="+mHealthServer.getToken();
        Log.i(TAG, "init: "+url);
        webView.loadUrl(url);

    }

    public class CallAndroid extends  Object{

        @JavascriptInterface
        public void nextData(final String year,final String month,final String day,final String type,final String order,final String size,final String offset){

            final int mYear=Integer.parseInt(year);
            final int mMonth=Integer.parseInt(month);
            final int mDay=Integer.parseInt(day);
            final int mSize=Integer.parseInt(size);
            final int mOffset=Integer.parseInt(offset);
            int mType=Integer.parseInt(type);
            Log.i(TAG, "nextData: "+mYear+"|"+mMonth+"|"+mDay+"|"+mSize+"|"+mOffset+"|"+mType);
            if(mType== ReportType.MINUTE.getValue()) {
                List<AppReport> listAppReport = iappReportDao.loadReportList(mUser.getUser_guid(), mYear, mMonth, mDay, order,mSize, mOffset);
                List<ReportListDto> listReportList = new ArrayList<ReportListDto>(listAppReport.size());
                for (AppReport appReport : listAppReport) {
                    ReportListDto rld = new ReportListDto();
                    rld.setUser_guid(appReport.getUser_guid());
                    String mSubTime = DateUtil.getStringDate("yyyy-MM-dd HH:mm:ss", appReport.getStart_time());
                    rld.setStart_time(mSubTime);
                    rld.setReport_number(appReport.getReport_number());
                    rld.setReport_status(appReport.getReport_status());
                    rld.setReport_type(appReport.getReport_type());
                    listReportList.add(rld);
                }
                final String json = gson.toJson(listReportList, new TypeToken<List<ReportListDto>>() {
                }.getType());
                final int dataCount = iappReportDao.loadCountByDay(mUser.getUser_guid(), mYear, mMonth, mDay);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript("javascript:nextDataCallback('" + json + "'," + Integer.valueOf(type) + "," + dataCount + ")", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {//此处为 js 返回的结果
                                }
                            });
                        } else {
                            webView.loadUrl("javascript:nextDataCallback('" + json + "'," + Integer.valueOf(type) + "," + dataCount + ")");
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void loadOne(String _report_number,int mUser_guid,int mType,String mAction){

            if(mType==ReportType.HOUR24.getValue()){

                Log.i(TAG, "loadOne: "+_report_number+"|"+mUser_guid+"|"+mType+"|"+mAction);

                Intent mainIntent = new Intent(ReportShowActivity.this, ReportFeeActivity.class);
                mainIntent.putExtra("action",mAction);
                mainIntent.putExtra("type",mType);
                mainIntent.putExtra("user_guid",mUser_guid);
                mainIntent.putExtra("rNumber",_report_number);
                ActivityUtil.startActivity(ReportShowActivity.this,mainIntent,false, ActionEnum.NEXT);
                Log.i(TAG, "loadOne: "+_report_number);
            }else{

                Log.i(TAG, "loadOne: "+_report_number+"|"+mUser_guid+"|"+mType+"|"+mAction);

                Intent mainIntent = new Intent(ReportShowActivity.this, ReportFreeActivity.class);
                mainIntent.putExtra("action",mAction);
                mainIntent.putExtra("type",mType);
                mainIntent.putExtra("user_guid",mUser_guid);
                mainIntent.putExtra("rNumber",_report_number);
                ActivityUtil.startActivity(ReportShowActivity.this,mainIntent,false, ActionEnum.NEXT);
                Log.i(TAG, "loadOne: "+_report_number);
            }

        }
        @JavascriptInterface
        public void callTry(){
            Log.i(TAG, "callTry: ================");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });

        }

    }


}
