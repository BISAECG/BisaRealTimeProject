package com.bisa.health.rest.service;

import android.content.Context;
import android.util.Log;

import com.bisa.health.model.HealthServer;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.HttpHelp;
import com.bisa.health.rest.ICallDownInterface;
import com.bisa.health.rest.ICallUploadInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/11/8.
 */

public class DataServiceImpl extends HttpHelp implements IDataService {

    private static final String TAG = "DataServiceImpl";

    public DataServiceImpl(final Context context,final HealthServer healthServer) {
        super(context,healthServer,1);
    }


    @Override
    public Object updatedata(Map<String, String> map, Map<String, File> files, ICallUploadInterface callUploadInterface) throws IOException {
        final String url="/mi/app/ecg/data/upload";
        return doFilePost(url,map,files,callUploadInterface);
    }

    @Override
    public void download(final File file, Map<String, String> param, final ICallDownInterface callDownInterface) {
        final String url="/mi/h5/report/24report";
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
                        Log.i(TAG, "onResponse: "+progress);
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
