package com.bisa.health.ecg;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.ble.BleDefinedConnStatus;
import com.bisa.health.ble.BleWrapper;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.CustReportDialog;
import com.bisa.health.cust.CustomDefaultDialog;
import com.bisa.health.cust.CustomFreeReportDialog;
import com.bisa.health.cust.OnDoubleClickListener;
import com.bisa.health.cust.SecondSurfaceView;
import com.bisa.health.ecg.config.ECGSetConfig;
import com.bisa.health.ecg.dao.IReportDao;
import com.bisa.health.ecg.dao.ReportDaoImpl;
import com.bisa.health.ecg.encoder.ECGBuildAnasysBack;
import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.ecg.model.ReportEnum;
import com.bisa.health.ecg.model.ReportStatus;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.AppNotifiMsg;
import com.bisa.health.model.Device;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.rest.service.DataServiceImpl;
import com.bisa.health.rest.service.IDataService;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.service.ECGService;
import com.bisa.health.service.IEcgService;
import com.bisa.health.service.IEcgServiceCallBacks;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.BleConnDiaLogUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.DiaLogUtil;
import com.bisa.health.utils.FinalBisa;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.Notificator;
import com.bisa.health.utils.ReportUtils;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static com.bisa.health.ble.BleDefinedConnStatus.BLE_CONN_SECCUESS;


public class ECGActivity extends BaseActivity implements View.OnClickListener, UiUpCallShowView, CustReportDialog.OnClickListenerInterface {


    private static final String TAG = "ECGActivity";
    /**
     * Android UI
     */

    private ImageButton markerBtn;
    private static SecondSurfaceView sfvWave = null;
    private TextView txHeartCount;

    private TextView txBpm;
    private TextView tv_title;
    private Device bleDevice;
    private TextView tv_tip_alam;
    private TextView tv_freetiming;
    private RelativeLayout rl_nav_title;
    private RelativeLayout btn_report_show;
    private RelativeLayout btn_report_generate;
    private LinearLayout ibtn_stop;
    private RelativeLayout rl_timing;

    /**
     * 服务绑定
     */
    Intent iService = null;
    private ServiceConnection serviceConnection = null;
    private IEcgService iEcgService;
    private IDataService iDataService;
    private IRestService restService;
    private CustomFreeReportDialog custFreeReport;

    /**
     * 计算心率
     */
    private int[] ecgdataArrayV2;
    private static int bpm = 0;
    /**
     * 监控手机电池电量
     */
    private static int batteryValue = 100;
    private static int signalValue = 100;
    private static int netWorkValue = FinalBisa.NEWWPRK_SATAUS_SUCCESS;
    /**
     * DB
     */
    private IReportDao iappReportDao;
    /**
     * BLE
     */
    private BleWrapper mBleWrapper;
    private IEcgServiceCallBacks mCallBack;
    private User mUser;
    private AppNotifiMsg mAppNotifiMsg;
    private HealthServer mHealthServer;
    private Notificator notificator;
    private CustReportDialog custReportDialog;
    private SharedPersistor sharedObject;
    private ECGSetConfig ecgConfig;
    private boolean isBleConnStatus = false;
    /*
	report
	 */

    //当前报告
    private ReportEnum curReport = ReportEnum.NULL;
    private final Gson gson = new Gson();
    private final int MONITOR_INTERVAL = 180 * 1000;

    private ECGBuildAnasysBack ecgBuildAnasys = ECGBuildAnasysBack.getInstance().init();
	/*
		Bar
	 */

    private ImageButton ibtn_back;
    private ImageView iv_signal;
    private ImageView iv_colud;
    private ImageView iv_battery;

    private RelativeLayout rl_unread;
    private TextView tv_unread;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecg_activity);
        AppManager.getAppManager().addActivity(this);
        sharedObject = new SharedPersistor(this);
        mUser = sharedObject.loadObject(User.class.getName());
        mHealthServer = sharedObject.loadObject(HealthServer.class.getName());
        mAppNotifiMsg=sharedObject.loadObject(AppNotifiMsg.class.getName());
        ibtn_back = (ImageButton) this.findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        iv_signal = (ImageView) this.findViewById(R.id.iv_signal);
        iv_colud = (ImageView) this.findViewById(R.id.iv_colud);
        iv_battery = (ImageView) this.findViewById(R.id.iv_battery);

        rl_unread= (RelativeLayout) this.findViewById(R.id.rl_unread);

        tv_freetiming = (TextView) this.findViewById(R.id.tv_freetiming);
        ibtn_stop = (LinearLayout) this.findViewById(R.id.ibtn_stop);
        ibtn_stop.setOnClickListener(this);
        rl_timing = (RelativeLayout) this.findViewById(R.id.rl_timing);
        tv_unread=(TextView) this.findViewById(R.id.tv_unread);
        if(mAppNotifiMsg!=null&&mAppNotifiMsg.getUnReadCount()>0){
            rl_unread.setVisibility(View.VISIBLE);
            if(mAppNotifiMsg.getUnReadCount()>99){
                tv_unread.setText("+99");
            }else{
                tv_unread.setText(""+mAppNotifiMsg.getUnReadCount());
            }

        }

        notificator = new Notificator(this);
        custReportDialog = new CustReportDialog(this);
        custReportDialog.setClicklistener(this);


        if (mBleWrapper == null)
            mBleWrapper = new BleWrapper(this);

        if (!mBleWrapper.checkBleHardwareAvailable()) {
            show_Toast(getString(R.string.title_sys_not_support));
            finish();
        }
        if(!mBleWrapper.isBtEnabled()){
            mBleWrapper.bleEnabled();
        }

        if (restService == null) {
            restService = new RestServiceImpl(this, mHealthServer);
        }

        if (iDataService == null) {
            iDataService = new DataServiceImpl(this, mHealthServer);
        }

        if (iappReportDao == null) {
            iappReportDao = new ReportDaoImpl(this);
        }

        /**
         * 初始化控件
         */
        markerBtn = (ImageButton) findViewById(R.id.btn_event_safeclock);
        markerBtn.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick(View v) {
                Log.i(TAG, "onDoubleClick: >>>");
                v.setEnabled(false);
                delayOnMarker();
                if (iEcgService != null) {

                    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                            ECGActivity.this, R.anim.loading_animation);

                    markerBtn.setAnimation(hyperspaceJumpAnimation);
                    try {
                        iEcgService.uiNotifiMarker(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
        btn_report_show = (RelativeLayout) findViewById(R.id.btn_report_show);
        btn_report_show.setOnClickListener(new BtnEventhandler());

        btn_report_generate = (RelativeLayout) findViewById(R.id.btn_report_generate);
        btn_report_generate.setOnClickListener(new BtnEventhandler());
        sfvWave = (SecondSurfaceView) this.findViewById(R.id.ECGsurface);
        sfvWave.setNullCallBack(this);
        //双击事件来实现横屏
        sfvWave.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        }));

        txHeartCount = (TextView) this.findViewById(R.id.txt_ecg_bpmcounttext);
        txBpm = (TextView) this.findViewById(R.id.txt_ecg_bpmval);
        tv_tip_alam = (TextView) this.findViewById(R.id.tv_tip_alam);
        rl_nav_title = (RelativeLayout) this.findViewById(R.id.rl_nav_title);
        /**
         * 注册后台服务
         */

        Bundle data = getIntent().getExtras();
        bleDevice = data.getParcelable(Device.class.getName());
        if (bleDevice == null) {
            show_Toast(getString(R.string.title_error));
            ActivityUtil.finishAnim(this, ActionEnum.BACK);
        }

        Log.i(TAG, "onCreate:");

        /**
         * 初始化回调服务
         */
        if (mCallBack == null) {
            mCallBack = new IEcgServiceCallBacks.Stub() {

                @Override
                public void uiNewRssiAvailable(int Rssi) throws RemoteException {

                    try {
                        int iRssi = Math.abs(Rssi);
                        float power = (float) ((iRssi - 77) / (10 * 2.0));
                        signalValue = (int) (Math.pow(10, power) / 3);
                        setSignalIco(signalValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void uiCallNotifiConnEvent(int mStatus) throws RemoteException {

                    mHandler.sendEmptyMessage(mStatus);
                }

                @Override
                public void uiCallNotifiAlam(int mStatus, String msg) throws RemoteException {

                    Log.i(TAG, "uiCallNotifiAlam: " + msg);
                    try {
                        String preMsg = StringUtils.isEmpty(msg) ? getResources().getString(R.string.title_error_try) : msg;
                        final String tip_msg = preMsg + "[" + DateUtil.getStringDate("yyyy-MM-dd kk:mm:ss", (new Date())) + "]";

                        switch (mStatus) {
                            case FinalBisa.NOTIFI_AlAM_SUCCESS:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_tip_alam.setText(tip_msg);
                                    }
                                });
                                break;
                            case FinalBisa.NOTIFI_AlAM_FINAL:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_tip_alam.setText(tip_msg);
                                    }
                                });
                                break;
                        }
                    } catch (Exception e) {
                        return;
                    }

                }

                @Override
                public void uiUpdateDeviceBattert(int battert) throws RemoteException {
                    batteryValue = battert;
                    setBetteryIco(batteryValue);
                }

                @Override
                public void uiUpdateNetWork(int status) throws RemoteException {
                    netWorkValue = status;
                    setNetworkIco(netWorkValue);
                }

                @Override
                public void ecgDrawing(int[] val) throws RemoteException {

                    if (sfvWave != null)
                        sfvWave.push(val);

                }
            };

        }
        //回调服务结束


        if (serviceConnection == null) {// 绑定服务

            serviceConnection = new ServiceConnection() {

                public void onServiceDisconnected(ComponentName name) {
                    iEcgService = null;
                }

                public void onServiceConnected(ComponentName name, IBinder service) {

                    if (iEcgService == null) {
                        iEcgService = IEcgService.Stub.asInterface(service);
                    }

                    try {
                        iEcgService.uiRegisterCallBacks(mCallBack);
                        iEcgService.uiDeviceConnected(bleDevice.getMacadderss(), bleDevice.getDevnum());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            };

        }


        iService = new Intent(this, ECGService.class);
        bindService(iService, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "onCreate: ");
    }

    private void delayOnMarker() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if (markerBtn != null && !markerBtn.isEnabled()) {
                    markerBtn.setEnabled(true);
                    markerBtn.clearAnimation();
                }
            }
        };
        mHandler.postDelayed(timeout, FinalBisa.ALAM_CHECK_INTERVAL);
    }

    private Runnable reportRunnable = new Runnable() {
        @Override
        public void run() {
            stopFreeReport(false);
        }
    };


    private synchronized void stopFreeReport(boolean isStop) {

        if(isStop){
            final CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(this)
                    .setIco(getResources().getDrawable(R.drawable.warning_ico))
                    .setMessage(getResources().getString(R.string.freereport_stop))
                    .setTitle(getResources().getString(R.string.freereport_title))
                    .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isShowFreeReport();


                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }else{
            isShowFreeReport();
        }






    }

    public void isShowFreeReport(){
        if (iEcgService != null) {
            try {
                rl_timing.setVisibility(View.GONE);
                curReport = ReportEnum.NULL;
                String freeReportPath = iEcgService.endMonitor();

                final File ecdFile = new File(freeReportPath);
                if (!ecdFile.exists()) return;

                final String reportNumber = ecdFile.getName().split("\\.")[0];
                final Date startTime = ReportUtils.getReportFileTimeToDate(ecdFile.getName());

                Log.i(TAG, "run: " + freeReportPath+">>>>>"+startTime);

                AppReport report = new AppReport();
                Calendar curCalendar = DateUtil.getCalendar(startTime);
                report.setReport_status(ReportStatus.READ.getValue());
                report.setReport_type(ReportType.MINUTE.getValue());
                report.setReport_number(reportNumber);
                report.setYear(curCalendar.get(Calendar.YEAR));
                report.setMonth(curCalendar.get(Calendar.MONTH) + 1);
                report.setDay(curCalendar.get(Calendar.DAY_OF_MONTH));
                report.setStart_time(curCalendar.getTime());
                report.setUser_guid(mUser.getUser_guid());
                report.setEcgdat(ecdFile.getAbsolutePath());
                AppReport mReport=iappReportDao.add(report);
                if(mReport!=null){

                    custFreeReport = new CustomFreeReportDialog.Builder(ECGActivity.this,mReport,mUser,mHealthServer).create();
                    DiaLogUtil.DialogShow(custFreeReport,ECGActivity.this, 10);
                }else{
                    show_Toast(getResources().getString(R.string.title_error_try));
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void doConfirm(int rType) throws RemoteException {


        switch (rType) {

            case 1:
                if (isBleConnStatus == false) {
                    show_Toast(getResources().getString(R.string.report_not_ble));
                    custReportDialog.dismiss();
                    break;
                }


                if (iEcgService != null && curReport == ReportEnum.NULL) {
                    rl_timing.setVisibility(View.VISIBLE);
                    long curTime = DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                    iEcgService.startMonitor(curTime);
                    long monitorEndTime = curTime + MONITOR_INTERVAL;
                    ;
                    curReport = ReportEnum.FREE_REPORT;
                    curReport.setTimeOffset(monitorEndTime);
                    mHandler.postDelayed(reportRunnable, MONITOR_INTERVAL);
                    show_Toast(getResources().getString(R.string.report_205));
                } else {//有报告
                    show_Toast(getResources().getString(R.string.report_400));

                }

                if (custReportDialog != null)
                    custReportDialog.dismiss();
                break;
            case 24:
                if (custReportDialog != null)
                    custReportDialog.dismiss();
                ActivityUtil.startActivity(ECGActivity.this, OTGActivity.class, true, ActionEnum.NULL);
                break;
            default:
                break;

        }

    }


    @Override
    public void onClick(View v) {
        if (v == ibtn_back) {

            LoadDiaLogUtil.getInstance().show(ECGActivity.this, false);
            if (iEcgService != null) {
                try {
                    iEcgService.uiDeviceClose();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Runnable delayRun = new Runnable() {
                @Override
                public void run() {
                    LoadDiaLogUtil.getInstance().dismiss();
                    ActivityUtil.finishAnim(ECGActivity.this, ActionEnum.BACK);
                }
            };
            mHandler.postDelayed(delayRun, 1000);

        } else if (v == ibtn_stop) {
            mHandler.removeCallbacks(reportRunnable);
            if (curReport != ReportEnum.NULL) {
                stopFreeReport(true);
            }

        }

    }
    //end主界面回调动作


    /**
     * 按钮的统一处理事件
     */
    final class BtnEventhandler implements OnClickListener {

        @Override
        public void onClick(View v) {

            if (v == markerBtn) {
                v.setEnabled(false);
                delayOnMarker();
                if (iEcgService != null) {

                    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                            ECGActivity.this, R.anim.loading_animation);

                    markerBtn.setAnimation(hyperspaceJumpAnimation);
                    try {
                        iEcgService.uiNotifiMarker(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else if (v == btn_report_generate) {
                custReportDialog.show();
            } else if (v == btn_report_show) {
                ActivityUtil.startActivity(ECGActivity.this, ReportCalendarActivity.class, false, ActionEnum.NULL);

            }

        }

    }

    /**
     * 电量
     */
    protected void setBetteryIco(final int Power) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (Power) {
                    case 0:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_1));
                        break;
                    case 1:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_2));
                        break;
                    case 2:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_3));
                        break;
                    case 3:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_4));
                        break;
                    case 4:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_5));
                        break;
                    case 5:
                        iv_battery.setImageDrawable(getResources().getDrawable(R.drawable.battery_5));
                        break;

                }
            }
        });

    }

    private void setSignalIco(final int Power) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (Power) {
                    case 0:
                        iv_signal.setImageDrawable(getResources().getDrawable(R.drawable.signal_4));
                        break;
                    case 1:
                        iv_signal.setImageDrawable(getResources().getDrawable(R.drawable.signal_3));
                        break;
                    case 2:
                        iv_signal.setImageDrawable(getResources().getDrawable(R.drawable.signal_2));
                        break;
                    case 3:
                        iv_signal.setImageDrawable(getResources().getDrawable(R.drawable.signal_1));
                        break;
                    case 100:
                        iv_signal.setImageDrawable(getResources().getDrawable(R.drawable.signal));
                        break;

                }
            }
        });

    }

    private void setNetworkIco(final int status) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case FinalBisa.NEWWPRK_SATAUS_SUCCESS:
                        iv_colud.setImageDrawable(getResources().getDrawable(R.drawable.up_success));
                        break;
                    case FinalBisa.NEWWPRK_SATAUS_FAIL:
                        iv_colud.setImageDrawable(getResources().getDrawable(R.drawable.up_fail));
                        break;
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        notificator.CancelRunningNotif();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");


    }

    @Override
    protected void onStop() {
        super.onStop();
        notificator.RunningNotifiction(getResources().getString(R.string.app_name_run), ECGActivity.class);
        Log.i(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificator.CancelRunningNotif();
        Log.i(TAG, ">>>>>>>>>:onDestroy");
        iappReportDao = null;
        exit();

    }

    private void exit() {

        if(!isExit&&iEcgService!=null){
            Log.i(TAG, "exit: 关闭");
            try {
                iEcgService.uiDeviceClose();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "exit: >>>>>>1");

        if (serviceConnection != null) {
            this.unbindService(serviceConnection);
            serviceConnection = null;
        }
        Log.i(TAG, "exit: >>>>>>3");
        if (sfvWave != null) {
            sfvWave.mIsDrawing(false);
            sfvWave = null;
        }
        mCallBack = null;
    }


    private int exit_count = 2;
    private long exit_offset_temp = 0;
    private boolean isExit=false;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            long exit_offset = System.currentTimeMillis();
            if (exit_offset - exit_offset_temp > 5000) {
                exit_count = 2;
            }

            if (exit_count == 2) {
                if (curReport != ReportEnum.NULL) {
                    show_Toast(getString(R.string.report_exists));
                } else {
                    show_Toast(getString(R.string.exit_click));
                }

            }

            exit_count--;
            exit_offset_temp = exit_offset;
            if (exit_count <= 0) {
                isExit=true;
                exit_count = 2;
                LoadDiaLogUtil.getInstance().show(ECGActivity.this, false);

                if (iEcgService != null) {
                    try {
                        iEcgService.uiDeviceClose();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                Runnable delayRun = new Runnable() {
                    @Override
                    public void run() {
                        LoadDiaLogUtil.getInstance().dismiss();
                        ActivityUtil.finishAnim(ECGActivity.this, ActionEnum.BACK);
                    }
                };
                mHandler.postDelayed(delayRun, 1000);

            }

            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private int svWidth = 0;
    private int svHeight = 0;
    private LinearLayout rl_ecg_view;
    private boolean isLandscape = false;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        rl_ecg_view = (LinearLayout) this.findViewById(R.id.rl_ecg_view);

        if (svWidth == 0 && svHeight == 0) {
            svWidth = sfvWave.getWidth();
            svHeight = sfvWave.getHeight();
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int actionBarHeight = (this.findViewById(R.id.abar_title)).getHeight();

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            isLandscape = true;
            int mHeight = dm.heightPixels - actionBarHeight;
            int mWidth = dm.widthPixels;
            ViewGroup.LayoutParams layoutParams = sfvWave.getLayoutParams();
            layoutParams.height = mHeight;
            layoutParams.width = mWidth;


            ViewGroup.LayoutParams layoutParamsll = rl_ecg_view.getLayoutParams();
            layoutParamsll.height = mHeight;
            layoutParamsll.width = mWidth;
            rl_ecg_view.setLayoutParams(layoutParamsll);


            sfvWave.initView(mWidth, mHeight, layoutParams);
            rl_nav_title.setVisibility(View.GONE);
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            tv_title.setText(getString(R.string.title_main_mydev));
            isLandscape = false;
            ViewGroup.LayoutParams layoutParamsll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 5f);
            rl_ecg_view.setLayoutParams(layoutParamsll);

            ViewGroup.LayoutParams layoutParams = sfvWave.getLayoutParams();
            layoutParams.width = svWidth;
            layoutParams.height = svHeight;

            sfvWave.initView(svWidth, svHeight, layoutParams);
            rl_nav_title.setVisibility(View.VISIBLE);

            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }

        super.onConfigurationChanged(newConfig);
    }

    /**
     * 更新心率
     */
    @Override
    public void uiBpmCallBack(int val2, int val1, int val3) {

        ecgdataArrayV2 = ecgBuildAnasys.build(val2, val1, val3);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ecgdataArrayV2 != null && bpm != ecgdataArrayV2[93]) {
                    txBpm.setText("" + ecgdataArrayV2[93]);
                    if (isLandscape) {
                        tv_title.setText("" + ecgdataArrayV2[93] + " BPM");
                    }

                    txHeartCount.setText("" + ecgdataArrayV2[90]);
                    bpm = ecgdataArrayV2[93];
                }
                if (curReport != ReportEnum.NULL) {
                    long curTime = DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                    tv_freetiming.setText(DateUtil.formatTime(curReport.getTimeOffset() - curTime));
                }

            }
        });
    }


    private final OnClickListener onClickDialogListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.btn_connble) {

                BleConnDiaLogUtil.getInstance().dismiss();
                try {
                    if (iEcgService != null) {
                        iEcgService.uiDeviceConnected(bleDevice.getMacadderss(), bleDevice.getDevnum());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


        }
    };


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BleDefinedConnStatus.BLE_CONN_ING:
                    isBleConnStatus = false;
                    Log.i(TAG, "handleMessage: BLE_CONN_ING");
                    LoadDiaLogUtil.getInstance().show(ECGActivity.this, true);
                    break;
                case BLE_CONN_SECCUESS:
                    isBleConnStatus = true;
                    Log.i(TAG, "handleMessage: BLE_CONN_SECCUESS");
                    LoadDiaLogUtil.getInstance().dismiss();
                    break;
                case BleDefinedConnStatus.BLE_CONN_FAILED:
                    isBleConnStatus = false;
                    Log.i(TAG, "handleMessage: BLE_CONN_FAILED");
                    LoadDiaLogUtil.getInstance().dismiss();
                    BleConnDiaLogUtil.getInstance().setOnClickBLEListener(onClickDialogListener).show(ECGActivity.this, true);
                    break;

            }
        }

    };


}
