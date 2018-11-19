package com.bisa.health.cust;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.bisa.health.R;
import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.utils.HttpUtil;
import com.bisa.health.utils.WebViewUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.bisa.health.exception.CrashHandler.TAG;

public class CustomFreeReportDialog extends Dialog  {

    private Context mContext;

    public CustomFreeReportDialog(Context context) {
        super(context);
        mContext=context;

    }

    public CustomFreeReportDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;

    }

    public static class Builder {
        private Context context;
        private AppReport mReport;
        private WebView webView;
        private User mUser;
        private HealthServer mHealthServer;
        private String postDate;
        private String url;
        public Builder(Context context, AppReport mReport, User mUser, HealthServer mHealthServer) {
            this.context=context;
            this.mReport=mReport;
            this.mUser=mUser;
            this.mHealthServer=mHealthServer;
        }




        public CustomFreeReportDialog create() {


            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomFreeReportDialog dialog = new CustomFreeReportDialog(context,R.style.Dialog);
            View view = inflater.inflate(R.layout.dialog_free_report, null);
            webView= (WebView) view.findViewById(R.id.webview);
            webView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
            WebViewUtil.buildSetting(webView);
            webView.addJavascriptInterface(new CallAndroid(), "CallAndroid");
            String lan = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            String lang = lan + "_" + country;
            url=mHealthServer.getDomain()+"/mi/call/h5/free?lang="+lang;
            Log.i(TAG, "init:"+url);

            File ecdFile=new File(mReport.getEcgdat());
            if(!ecdFile.exists()){
                return null;
            }
            byte[] ecdBytes=null;
            FileInputStream fis=null;
            try {
                fis = new FileInputStream(ecdFile);
                ecdBytes=new byte[fis.available()];
                fis.read(ecdBytes);
            } catch (IOException e) {
                return null;
            }finally {
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }

            byte[] data= Base64.encode(ecdBytes,Base64.NO_WRAP);
            Map<String,String> param=new HashMap<String,String>();
            param.put("ecgdat",new String(data));
            param.put("sex",""+mUser.getSex().getValue());
            param.put("height",""+mUser.getHeight());
            param.put("weight",""+mUser.getWeight());
            param.put("sport_type",""+mUser.getSport_type().getValue());
            param.put("shape_type",""+mUser.getShape_type().getValue());
            param.put("headUrl",""+mUser.getUri_pic());
            param.put("ctime",""+mReport.getStart_time());

             postDate = HttpUtil.packParamsToWebView(param);

            if(postDate==null) return null;
            Log.i(TAG, "init: "+postDate);

            webView.postUrl(url, postDate.getBytes());
            WebViewUtil.build(webView);
            dialog.setCancelable(true);
            dialog.setContentView(view, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return dialog;
        }

        public  class CallAndroid extends  Object{
            @JavascriptInterface
            public void callTry(){

                if(postDate==null) return ;
                Activity activity=(Activity)context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.postUrl(url, postDate.getBytes());
                    }
                });


            }
        }
    }


}
