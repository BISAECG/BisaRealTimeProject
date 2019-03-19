package com.bisa.health.rest.service;

import android.content.Context;

import com.bisa.health.model.HealthServer;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.HttpHelp;
import com.bisa.health.rest.ICallDownInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/29.
 */

public class RestServiceImpl extends HttpHelp implements  IRestService{


    public RestServiceImpl(final Context context,final HealthServer healthServer) {
        super(context,healthServer,1);
    }


    @Override
    public Call sendCodeByIphone(Map<String,String>  param) {
        String url="/mi/call/common/code/iphone";
        Call call=get(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call updateUsername(FormBody param) {
        String url="/mi/app/user/update/username";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call sendCodeByUser(Map<String,String> param) {
        String url="/mi/call/common/code";
        Call call=get(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }



    @Override
    public Call login(FormBody param) {
        String url="/mi/login/iphone";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }



    @Override
    public Call loginPwd(FormBody param) {
        String url="/mi/login/pwd";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }


    @Override
    public Call wxLogin(FormBody param) {

        String url="/mi/login/other";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }



    @Override
    public Call updateInfo(MultipartBody param) {
        String url="/mi/app/user/upinfo";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call getUserInfo() {
        String url="/mi/app/user/info";
        Call call=get(url,null,null,HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call downHeadIco(String uri) {
        Call call=get(uri,null,null,HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call addContact(FormBody param) {

        String url="/mi/app/ecg/contacts/add";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;

    }

    @Override
    public Call appDeleteContact(FormBody param) {

        String url="/mi/app/ecg/contacts/del";
        Call call=post(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;

    }

    @Override
    public Call sendSOS(Map<String,String> param) {
        String url="/mi/app/ecg/sms/fast";
        Call call=get(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call generateReport(FormBody param) {
        String url="/mi/app/ecg/report/create";
        Call call =post(url,param,null,HttpFinal.CONN_HTTPS);
        return call;
    }

    private static final String TAG = "RestServiceImpl";
    @Override
    public Call downServerList(Map<String, String> param) {
        String url=SERVER_URL+"/mi/call/server/down";
        Call call=get(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call iphoneRegedit(FormBody param) {
        String url="/mi/login/phone/reg";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call findAccount(Map<String, String> param) {
        String url="/mi/call/common/fotget/user";
        Call call=get(url,param,null,HttpFinal.CONN_HTTPS);
        return  call;
    }


    @Override
    public Call findPassword(FormBody param) {
        String url="/mi/call/common/fotget/pwd";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call getContacts() {
        String url="/mi/app/ecg/contacts/list";
        Call call=get(url,null,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call getAccount() {
        String url="/mi/app/bind/account/list";
        Call call=get(url,null,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call modifyPwd(FormBody param) {
        String url="/mi/app/user/pwd/modify";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call otherbind(FormBody param) {
        String url="/mi/login/otherbind";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call bindAccount(FormBody param) {
        String url="/mi/app/bind/account";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call bindOther(FormBody param) {
        String url="/mi/app/bind/other";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call varifyBindToken(FormBody param) {
        String url="/mi/app/bind/verify/token";
        Call call=post(url,param,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call initDat() {
        String url="/mi/app/comm/notifimsg";
        Call call=get(url,null,null, HttpFinal.CONN_HTTPS);
        return  call;
    }

    @Override
    public Call syncVersion(Map<String, String> param) {
        String url=SERVER_URL+"/mi/call/app/syncversion";
        Call call=get(url,param,null, HttpFinal.DEFALUT_CONN_HTTP);
        return call;
    }

    @Override
    public void downapp(final File file, Map<String, String> param, final ICallDownInterface callDownInterface) {
        String url=SERVER_URL+"/mi/call/app/down";
        Call call=get(url,param,null, HttpFinal.CONN_HTTPS);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callDownInterface.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                long mTotal = response.body().contentLength();
                try {
                    is = response.body().byteStream();
                    long total = 10485760;
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        callDownInterface.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    callDownInterface.onDownloadSuccess();
                } catch (Exception e) {
                    callDownInterface.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }

            }
        });
    }


}
