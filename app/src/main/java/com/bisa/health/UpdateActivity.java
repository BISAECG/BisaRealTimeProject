package com.bisa.health;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.UpdateVersion;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.HttpHelp;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class UpdateActivity extends BaseActivity implements View.OnClickListener {
	private HealthPath mHealthPath;
	private SharedPersistor sharedPersistor;
	private String apkname = "BisHealth.apk";
	private IRestService restService;
	private DownloadManager downloadManager;
	private DownloadManager.Request request;
	private HealthServer mHealthServer;
	private ProgressBar progressBar;
	private Timer timer;
	private TimerTask task;
	private Button btn_update;
	private RelativeLayout rl_sw1;
	private RelativeLayout rl_sw2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		UpdateActivityPermissionsDispatcher.doNewVersionUpdateWithPermissionCheck(this);
		progressBar=(ProgressBar)this.findViewById(R.id.progressBar);
		rl_sw1=this.findViewById(R.id.rl_sw1);
		rl_sw1.setVisibility(View.GONE);
		rl_sw2=this.findViewById(R.id.rl_sw2);
		rl_sw2.setVisibility(View.VISIBLE);
		btn_update=this.findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);

		if(sharedPersistor==null)
			sharedPersistor=new SharedPersistor(this);
		mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
		mHealthPath=sharedPersistor.loadObject(HealthPath.class.getName());
		restService = new RestServiceImpl(this,mHealthServer);
		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		doNewVersionUpdate();
	}


	@NeedsPermission({
			Manifest.permission.REQUEST_INSTALL_PACKAGES})
	public void doNewVersionUpdate() {
		File apkFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),apkname);
		if (apkFile != null && apkFile.exists()) {
			apkFile.delete();
		}

	}

	@Override
	protected void onResume() {
		// finish();
		super.onResume();
	}

	@OnShowRationale({
			Manifest.permission.REQUEST_INSTALL_PACKAGES})
	public void showRationaleForApp(final PermissionRequest request) {

		new AlertDialog.Builder(this)
				.setMessage(R.string.permission_title)
				.setPositiveButton(R.string.commit_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						request.proceed();
					}
				})
				.setNegativeButton(R.string.cancel_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						request.cancel();
					}
				})
				.show();
	}

	@OnPermissionDenied({
			Manifest.permission.REQUEST_INSTALL_PACKAGES})
	public void showDeniedForApp() {
		Toast.makeText(this, "权限验证不通过,软件退出,请再次打开软件允许权限", Toast.LENGTH_LONG).show();
		finish();
	}

	private static final String TAG = "UpdateActivity";

	private void downFile() {

		DownloadManager.Request request=new DownloadManager.Request(Uri.parse(HttpHelp.DOWN_APP_URL));
		request.setTitle(getResources().getString(R.string.update_title));
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		//设置存储路径 --这个是应用专用的,软件卸载后，下载的文件将随着卸载全部被删除
		request.setDestinationInExternalFilesDir(UpdateActivity.this,Environment.DIRECTORY_DOWNLOADS, apkname);
		//设置 文件类型
		request.setMimeType("application/vnd.android.package-archive");



		final long downid=downloadManager.enqueue(request);

		progressBar.setMax(100);

		final  DownloadManager.Query query = new DownloadManager.Query();
		Log.i(TAG, "downFile: "+(request==null)+"|"+(downloadManager==null)+"|"+downid);
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				Cursor cursor = downloadManager.query(query.setFilterById(downid));
				if (cursor != null && cursor.moveToFirst()) {
					if (cursor.getInt(
							cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        progressBar.setProgress(100);
                        task.cancel();
						String queryUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        Message msg =Message.obtain();
                        msg.what=HttpFinal.DOWN_SUCCESS_200;
                        Bundle bundle = new Bundle();
                        bundle.putString("query",queryUri);
                        msg.setData(bundle);

                        handler.sendMessageAtTime(msg,SystemClock.uptimeMillis()+1000);
					}


					int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
					int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
					int pro =  (bytes_downloaded * 100) / bytes_total;
					Message msg =Message.obtain();
                    msg.what=HttpFinal.DOWN_ING_203;
					Bundle bundle = new Bundle();
					bundle.putInt("pro",pro);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
				cursor.close();
			}
		};
		timer.schedule(task, 0,1000);
		task.run();

	}
	private Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {

		    switch (msg.what) {
                case HttpFinal.DOWN_SUCCESS_200:
                    Bundle installData = msg.getData();
                    String downid  = installData.getString("query");
                    installApk(downid);
                    break;
                case HttpFinal.DOWN_ING_203:
                    Bundle bundle = msg.getData();
                    int pro = bundle.getInt("pro");
                    String name  = bundle.getString("name");
                    progressBar.setProgress(pro);
                    break;
            }
			super.handleMessage(msg);


		}
	};



	public Uri getDownloadPath(String queryStr) {
		Uri path = null;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "getDownloadPath: 6.0<");
			//6.0 以下
			path = Uri.parse(queryStr);
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			//6.0 — 7.0
            Log.i(TAG, "getDownloadPath: 6.0~7.0");
			path = Uri.fromFile(queryDownloadedApk(queryStr));
		} else {
            Log.i(TAG, "getDownloadPath: 7.0");
			//7.0 以上
			try {
				File filePath=new File(new URI(queryStr));
				Log.i(TAG, "getDownloadPath: "+(filePath==null));
				path = FileProvider.getUriForFile(UpdateActivity.this, "com.bisa.health.fileProvider",filePath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

		}
		return path;
	}
    private File queryDownloadedApk(String queryStr) {
		File downloadFilePath  = new File(Uri.parse(queryStr).getPath());
        return downloadFilePath;
    }

    public void installApk(String queryStr) {
        //在上面已经获取到apk存储路径
        Uri uri = getDownloadPath(queryStr);
        //跳转安装
        Intent intentInstall = new Intent();
        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentInstall.setAction(Intent.ACTION_VIEW);
        intentInstall.setDataAndType(uri, "application/vnd.android.package-archive");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intentInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
		}
        ActivityUtil.startActivity( UpdateActivity.this,intentInstall,true,ActionEnum.NULL);
		sharedPersistor.delObject(UpdateVersion.class.getName());
    }


	@Override
	public void onClick(View v) {
		if(v==btn_update){
			rl_sw2.setVisibility(View.GONE);
			rl_sw1.setVisibility(View.VISIBLE);
			downFile();
		}
	}
}