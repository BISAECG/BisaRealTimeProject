package com.bisa.health.ecg;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.ecg.dao.ReportDaoImpl;
import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.ecg.model.CalendarDto;
import com.bisa.health.ecg.model.DataoriginDto;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.FunUtil;
import com.bisa.health.utils.WebViewUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ReportCalendarActivity extends BaseActivity {

    private static final String TAG = "ReportCalendarActivity";
    private WebView webView;
    private SharedPersistor sharedObject;
    private User mUser;
    private HealthServer mHealthServer;
    private IReportDao iappReportDao;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_show);
        AppManager.getAppManager().addActivity(this);
        iappReportDao=new ReportDaoImpl(this);
        sharedObject=new SharedPersistor(this);
        mUser=sharedObject.loadObject(User.class.getName());
        mHealthServer=sharedObject.loadObject(HealthServer.class.getName());
        webView = (WebView) findViewById(R.id.webView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        init();
    }

    private void init(){

        WebViewUtil.buildSetting(webView);
        webView.addJavascriptInterface(new CallAndroid(), "CallAndroid");
        //WebView加载web资源

        url=mHealthServer.getDomain()+"/mi/h5/report/calendar?lang="+ FunUtil.h5Lang()+"&timestamp="+System.currentTimeMillis()+"&token="+mHealthServer.getToken();
        //String url="http://192.168.1.3:8088/health-app/mi/h5/report/freetest";
        Log.i(TAG, "init: "+url);
        webView.loadUrl(url);
        WebViewUtil.build(webView);
    }



    public class CallAndroid extends  Object{
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void callJson(final int ynow,final  int _mnow) {

            final int mnow=_mnow+1;
            int user_guid=mUser.getUser_guid();
            List<AppReport> listReport= iappReportDao.loadByCalendarData(ynow,mnow,user_guid, ReportType.MINUTE.getValue());
            List<CalendarDto> listCal=new ArrayList<CalendarDto>();
            for(AppReport report:listReport){
                CalendarDto calendarDto=new CalendarDto();
                calendarDto.setDay(report.getDay());
                calendarDto.setReport_count(report.getReport_count());
                calendarDto.setReport_number(report.getReport_number());
                calendarDto.setReport_status(report.getReport_status());
                calendarDto.setStart_time(report.getStart_time());
                calendarDto.setUser_guid(report.getUser_guid());
                calendarDto.setReport_type(report.getReport_type());
                listCal.add(calendarDto);
            }

            DataoriginDto dataoriginDto = new DataoriginDto();
            dataoriginDto.setCalendarDtoList(listCal);
            dataoriginDto.setAlarmLogList(null);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            final String callJsonStr = gson.toJson(dataoriginDto,new TypeToken<DataoriginDto>(){}.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: "+callJsonStr);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                        webView.evaluateJavascript("javascript:loadReport('" + callJsonStr + "'," + ynow + "," + mnow +")", new ValueCallback<String>() {
                             @Override
                              public void onReceiveValue(String value) {//此处为 js 返回的结果
                              }
                         });
                    }else{
                        webView.loadUrl("javascript:loadReport('" + callJsonStr + "'," + ynow + "," + mnow + ")");
                    }

                    }
                });

        }
        @JavascriptInterface
        public void callurl(final int type,final int month,final int ynow,final String day,final String action){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int mDay=Integer.valueOf(day.trim());
                    Log.i(TAG, "run: "+type);
                    Intent mainIntent = new Intent(ReportCalendarActivity.this, ReportShowActivity.class);
                    mainIntent.putExtra("action",action);
                    mainIntent.putExtra("type",type);
                    mainIntent.putExtra("year",ynow);
                    mainIntent.putExtra("month",month+1);
                    mainIntent.putExtra("day",mDay);
                    ActivityUtil.startActivity(ReportCalendarActivity.this,mainIntent,false, ActionEnum.NEXT);

                }
            });
        }
        @JavascriptInterface
        public void callTry(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });

        }

    }

}
