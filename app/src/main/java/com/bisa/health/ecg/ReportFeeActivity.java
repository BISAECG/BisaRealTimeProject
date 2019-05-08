package com.bisa.health.ecg;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.ActionBarView;
import com.bisa.health.cust.view.CustomDownProgressDialog;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.rest.ICallDownInterface;
import com.bisa.health.rest.service.DataServiceImpl;
import com.bisa.health.rest.service.IDataService;
import com.bisa.health.utils.FunUtil;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ReportFeeActivity extends BaseActivity implements ICallDownInterface {

    private static final String TAG = "ReportFeeActivity";
    private HealthServer mHealthServer;
    private SharedPersistor sharedObject;
    private HealthPath healthPath;
    private String action;
    private int type;
    private String rNumber;
    private int user_guid;
    private String url;
    private IDataService dataService;
    private PDFView pdfView;
    private File pdfFile;
    private CustomDownProgressDialog proDialog;

    private ActionBarView actionBarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feereport);
        AppManager.getAppManager().addActivity(this);

        sharedObject=new SharedPersistor(this);
        mHealthServer=sharedObject.loadObject(HealthServer.class.getName());
        healthPath=sharedObject.loadObject(HealthPath.class.getName());
        dataService=new DataServiceImpl(this,mHealthServer);

        action=getIntent().getStringExtra("action");
        type=getIntent().getIntExtra("type",0);
        user_guid=getIntent().getIntExtra("user_guid",0);
        rNumber=getIntent().getStringExtra("rNumber");
        pdfView=(PDFView) this.findViewById(R.id.pdfView);
        proDialog=new CustomDownProgressDialog.Builder(this).setPositiveButton(getResources().getString(R.string.commit_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downpdf();
            }
        }).setNegativeButton(getResources().getString(R.string.cancel_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();


        if(Integer.valueOf(type)== ReportType.HOUR24.getValue()){
            url=action+"?lang="+ FunUtil.h5Lang()+"&timestamp="+System.currentTimeMillis()+"&token="+mHealthServer.getToken();
            Log.i(TAG, "onCreate: "+url);
            pdfFile=new File(healthPath.getFeepdf(),rNumber+".pdf");
            if(pdfFile.exists()){
                pdfView.fromFile(pdfFile)
                        .load();
            }else{

                downpdf();
            }

        }

        actionBarView = findViewById(R.id.abar_title);
        actionBarView.setOnActionClickListenerNext(new ActionBarView.OnActionClickListener() {
            @Override
            public void onActionClick() {
                if(pdfFile != null) {
                    if(pdfFile.exists()) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri fileUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            fileUri = FileProvider.getUriForFile(ReportFeeActivity.this, getPackageName() + ".fileProvider", pdfFile);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            fileUri = Uri.fromFile(pdfFile);
                        }
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri);  //传输图片或者文件 采用流的方式
                        intent.setType("*/*");   //分享文件
                        startActivity(Intent.createChooser(intent, getString(R.string.xixin_ecg)));
                    }
                }
            }
        });
    }

    public void downpdf(){
        if(pdfFile!=null&&rNumber!=null){
            proDialog.switchView(true);
            proDialog.show();
            Map<String,String> param=new HashMap<String,String>();
            param.put("reportNumber",rNumber);
            dataService.download(pdfFile,param,this);
            proDialog.show();
        }else{
            showToast(getResources().getString(R.string.title_error_try));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();



    }


    @Override
    public void onDownloadFailed() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proDialog.switchView(false);
                if(pdfFile.exists()){
                    pdfFile.delete();
                }
            }
        });

    }

    @Override
    public void onDownloading(final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proDialog.setProgress(size);

            }
        });
    }

    @Override
    public void onDownloadSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proDialog.dismiss();
                if(pdfFile.exists()){
                    pdfView.fromFile(pdfFile)
                            .load();
                }

            }
        });
    }
}
