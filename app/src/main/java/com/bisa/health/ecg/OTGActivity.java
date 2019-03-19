package com.bisa.health.ecg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.RunActivity;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustomDefaultDialog;
import com.bisa.health.cust.view.CustomOTGWelcomeDialog;
import com.bisa.health.cust.view.CustomProgressDialog;
import com.bisa.health.ecg.adapter.OTGAdapter;
import com.bisa.health.ecg.adapter.OTGAdapterFile;
import com.bisa.health.ecg.model.OTGECGDto;
import com.bisa.health.ecg.model.OTGECGFileDto;
import com.bisa.health.ecg.model.ReportType;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.OrderEnum;
import com.bisa.health.provider.upload.UploadContentValues;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.ICallUploadInterface;
import com.bisa.health.rest.service.DataServiceImpl;
import com.bisa.health.rest.service.IDataService;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.usb.UsbStorageDeviceManage;
import com.bisa.health.usb.fat.Fat128Sys;
import com.bisa.health.usb.fat.Fat16BootSector;
import com.bisa.health.usb.fat.FsysFat;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.ArrayUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.DiaLogUtil;
import com.bisa.health.utils.FinalBisa;
import com.bisa.health.utils.FunUtil;
import com.bisa.health.utils.HealthECGUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.USBConstants;
import com.bisa.health.utils.USBFunUtil;
import com.bisa.health.utils.Utility;
import com.bisa.health.utils.ZipUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

import static com.bisa.health.utils.FinalBisa.OTG_UPLOAD_ERROR;

public class OTGActivity extends BaseActivity implements OTGAdapter.IOnItemPosSelectListener, OTGAdapterFile.IOnItemPosChindSelectListener, View.OnClickListener {

    private static final String TAG = "OTGActivity";
    private final List<OTGECGDto> mapList = new ArrayList<OTGECGDto>();
    private SharedPersistor sharedObject;
    private double DEFAYLT_SECTOR = 512d;
    private double DEFAYLT_CLUSTER = 32d;
    private IRestService mRestService;
    private IDataService mDataService;
    private User mUser;
    private HealthPath mHealthPath;
    private HealthServer mHealthServer;
    //private TextView tv_report_desc;
    //private ScrollView scrollView;
    private ListView listViewOTG;
    private OTGAdapter otgAdapter;
    private Button btn_order;
    private OTGECGFileDto mOTGECGFileDto;
    private Button btn_report_commit;

    PowerManager pManager = null;
    PowerManager.WakeLock mWakeLock = null;
    private int mPos = -1;
    private int mChindPos = -1;
    private boolean isAuth = false;
    private final static int USB_DELAYED = 15000;
    private CustomOTGWelcomeDialog OTGConnDialogBuilder;

    private CustomProgressDialog.DialogCall mPackDialogCall;
    private CustomProgressDialog.DialogCall mUpDialogCall;
    private Runnable usbRunnable=null;
    private Object lockObj=new Object();
    private String zipFilePath=null;


    //Test SD

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final String ACTION_USB_PERMISSION = "com.bis.ecg.USB_PERMISSION";
    private OrderEnum CUR_ORDER = OrderEnum.DESC;
    private UsbStorageDeviceManage usbStorageDeviceManage = null;
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    setMsg("接收到自定义广播");
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                        if (usbDevice != null) {  //Do something
                            setMsg("用户已授权，可以进行读取操作");
                            isAuth = true;
                            readDevice(mUsbDevice, mUsbManager);
                        } else {
                            isAuth = false;
                            setMsg("未获取到设备信息");
                        }
                    } else {
                        setMsg("用户未授权，读取失败");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        setMsg("接收到存储设备插入广播，尝试读取 isAuth:" + isAuth+"|");
                        //测试开关
                        LoadDiaLogUtil.getInstance().show(OTGActivity.this, true);
                        OTGInit();
                       // ListTestInit();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到存储设备拔出广播
                    UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_remove != null) {
                        if (usbStorageDeviceManage != null) {
                            usbStorageDeviceManage.close();
                        }
                        mOTGECGFileDto = null;
                        mapList.clear();
                        otgAdapter.notifyDataSetChanged();
                        setMsg("接收到存储设备插入拔出");
                    }
                    break;
            }
        }
    };

    /**
     * 初始化上传对话框
     */
     CustomProgressDialog uploadDialog=null;

    /**
     * 初始化打包对话框
     */
     CustomProgressDialog packageDialog = null;

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限

            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("InvalidWakeLockTag")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "=================onCreate================");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otg_activity);
        sharedObject = new SharedPersistor(this);
        mUser = sharedObject.loadObject(User.class.getName());
        mHealthPath = sharedObject.loadObject(HealthPath.class.getName());
        mHealthServer = sharedObject.loadObject(HealthServer.class.getName());
        if(mUser==null||mHealthServer==null){
            ActivityUtil.startActivity(this, RunActivity.class,true, ActionEnum.NULL);
        }

        mRestService = new RestServiceImpl(this, mHealthServer);
        mDataService = new DataServiceImpl(this,mHealthServer);


        registerReceiver();
        usbStorageDeviceManage = new UsbStorageDeviceManage(this);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        //scrollView = (ScrollView) this.findViewById(R.id.main_sv);
        //tv_report_desc = (TextView) this.findViewById(R.id.main_tv_msg);
        otgAdapter = new OTGAdapter(this, this, this);
        btn_report_commit = (Button) this.findViewById(R.id.btn_report_commit);
        btn_report_commit.setOnClickListener(this);
        btn_order = (Button) this.findViewById(R.id.btn_order);
        btn_order.setOnClickListener(this);

        listViewOTG = (ListView) this.findViewById(R.id.listOtgView);
        listViewOTG.setAdapter(otgAdapter);

        pManager = ((PowerManager) getSystemService(POWER_SERVICE));

        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                "CPUKeepRunning");
        mWakeLock.acquire();

        uploadDialog= new CustomProgressDialog.Builder(this)
                .setICO(R.drawable.otg_ico)
                .setTitle(R.string.otg_conn_update)
                .setBody(R.string.otg_conn_update_tip)
                .setErrorBody(R.string.otg_conn_update_error_tip)
                .setPositiveButton(getResources().getString(R.string.commit_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (zipFilePath != null) {
                    startUploadWork();
                }
            }
        }).setNegativeButton(getResources().getString(R.string.cancel_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setDialogCall(new CustomProgressDialog.CallDialogBuild() {
                    @Override
                    public void builder(CustomProgressDialog.DialogCall dialogCall) {
                        mUpDialogCall=dialogCall;
                    }
        }).create();


        packageDialog = new CustomProgressDialog.Builder(this)
                .setICO(R.drawable.otg_ico)
                .setTitle(R.string.otg_conn_package)
                .setBody(R.string.otg_conn_package_tip)
                .setErrorBody(R.string.otg_conn_package_error_tip)
                .setPositiveButton(getResources().getString(R.string.commit_submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (mOTGECGFileDto != null) {
                    startPackageWork();
                }
            }
        }).setNegativeButton(getResources().getString(R.string.cancel_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setDialogCall(new CustomProgressDialog.CallDialogBuild() {
                    @Override
                    public void builder(CustomProgressDialog.DialogCall dialogCall) {
                        mPackDialogCall=dialogCall;
                    }
        }).create();


        OTGConnDialogBuilder = new CustomOTGWelcomeDialog.Builder(this)
                .setPositiveButton(getResources().getString(R.string.otg_conn_yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        //verifyStoragePermissions(this);//测试用的
        //DiaLogUtil.DialogShow(OTGConnDialogBuilder, this, 10);
        OTGInit();
    }

    protected void ListTestInit() {

        for (int i = 10; i < 20; i++) {
            OTGECGDto otgecgDto = new OTGECGDto();
            otgecgDto.setTitle("2018-05-" + i);
            otgecgDto.setShow(false);
            otgecgDto.setYmd(Integer.parseInt("201805" + i));
            for (int x = 20; x < 30; x++) {
                Fat128Sys fat128Sys = new Fat128Sys();
                fat128Sys.setLength(512000);
                fat128Sys.setStartSectors(1024);
                fat128Sys.setFilename("TEST_201705" + i + "0825" + x + ".ECD");
                OTGECGFileDto otgecgFileDto = new OTGECGFileDto(fat128Sys, false, "GMT +8");
                otgecgDto.getList().add(otgecgFileDto);
            }


            mapList.add(otgecgDto);
        }

        otgAdapter.setMapList(mapList);
        otgAdapter.notifyDataSetChanged();


    }

    private void registerReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter(ACTION_USB_PERMISSION);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            mWakeLock.release();
        }
        if (mUsbReceiver != null) {//有注册就有注销
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
    }

    private UsbDevice mUsbDevice;
    private UsbManager mUsbManager;

    private synchronized void OTGInit() {

        try {



            HashMap<String, UsbDevice> deviceMap = mUsbManager.getDeviceList();
            for (UsbDevice usbDevice : deviceMap.values()) {
                setMsg("UsbDevice:" + "VID:" + usbDevice.getVendorId() + "|PID:" + usbDevice.getProductId() + "\n");
                if (USBConstants.VID == usbDevice.getVendorId() && USBConstants.PID == usbDevice.getProductId()) {
                    mUsbDevice = usbDevice;
                    //获取存储设备
                    if (mUsbManager.hasPermission(usbDevice)) {//有就直接读取设备是否有权限
                        setMsg("检测到有权限，直接读取");

                        readDevice(usbDevice, mUsbManager);
                        LoadDiaLogUtil.getInstance().dismiss();
                    } else {//没有就去发起意图申请
                        setMsg("检测到设备，但是没有权限，进行申请");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        mUsbManager.requestPermission(usbDevice, pendingIntent); //该代码执行后，系统弹出一个对话框，
                        showToast(getResources().getString(R.string.tip_otg_conn));
                    }
                    return;
                } else {
                    mUsbDevice = null;
                }
            }

        } catch (Exception e) {

            showToast(e.getMessage());

        }


    }

    private UsbDeviceConnection mConnection;
    private UsbInterface mUsbInterface;
    private int mRootSectorIndex = -1;

    private void readDevice(UsbDevice usbDevice, UsbManager usbManager) {


        mapList.clear();
        if (usbStorageDeviceManage.init(usbDevice, usbManager) == null) {
            setMsg("USB初始化失败");
            showToast(getResources().getString(R.string.otg_conn_device_fail));

            return;
        }
        FsysFat mFsysFat = null;
        Fat16BootSector fat16BootSector=null;
        ByteBuffer buffer = ByteBuffer.allocate(512);
        int rootSectorIndex=-1;
        try {
            usbStorageDeviceManage.getBlockDevice().init();
            usbStorageDeviceManage.getBlockDevice().read(0, buffer);
            fat16BootSector = Fat16BootSector.read(buffer);
            setMsg("fat16BootSector:" + fat16BootSector.toString());
            rootSectorIndex = fat16BootSector.getRootIndex() / fat16BootSector.getBytesPerSector();
            mRootSectorIndex = rootSectorIndex;
            ByteBuffer rootBuffer = ByteBuffer.allocate(fat16BootSector.getBytesPerSector() * 32);

            setMsg(fat16BootSector.getRootIndex() + "\n");
            usbStorageDeviceManage.getBlockDevice().init();
            usbStorageDeviceManage.getBlockDevice().read(rootSectorIndex, rootBuffer);
            List<FsysFat> fsysFatList = FsysFat.read(rootBuffer);
            if (fsysFatList.size() > 0) {
                for (FsysFat fsysFat : fsysFatList) {

                    if (fsysFat.getFs_name().trim().equals("USDATA")) {
                        mFsysFat = fsysFat;
                    }


                }
            } else {
                showToast(getResources().getString(R.string.otg_conn_device_fail));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (mFsysFat == null&&rootSectorIndex==-1) {//数据读取错误
            //TIP
            showToast(getResources().getString(R.string.tip_otg_read_error));
            return;
        }

        int uDataSectorsIndex = USBFunUtil.cluster_to_sector(mFsysFat.getFs_first_index_low(), rootSectorIndex);
        setMsg(uDataSectorsIndex + "\n");
        ByteBuffer userDataBuffer = ByteBuffer.allocate(fat16BootSector.getBytesPerSector() * 64);
        List<Fat128Sys> listFat128 = null;
        try {
            usbStorageDeviceManage.getBlockDevice().init();
            usbStorageDeviceManage.getBlockDevice().read(uDataSectorsIndex, userDataBuffer);
            listFat128 = Fat128Sys.read(userDataBuffer);
            if (listFat128.size() > 0) {

                try {
                    ArrayUtil.SortFat128List(listFat128, OrderEnum.DESC);
                } catch (Exception ef) {
                    showToast(getString(R.string.tip_otg_filename_error));
                }

                OTGECGDto otgecgDto = null;

                String tYMD = null;
                for (int i = 0; i < listFat128.size(); i++) {

                    if (i == (listFat128.size() - 1)) {
                        mapList.add(otgecgDto);
                    }

                    String[] ecgNameArray = FunUtil.ecgNameSpilt(listFat128.get(i).getFilename());
                    if (ecgNameArray == null) {
                        continue;
                    }
                    String fileYMD = FunUtil.BuildYMD(ecgNameArray[1]);
                    int orderIndex = FunUtil.BuildYMDIndex(ecgNameArray[1]);
                    if (!fileYMD.equals(tYMD)) {

                        if (tYMD != null) {
                            mapList.add(otgecgDto);
                        }

                        otgecgDto = new OTGECGDto();
                        otgecgDto.setTitle(fileYMD);
                        otgecgDto.setYmd(orderIndex);
                        tYMD = fileYMD;

                    }
                    otgecgDto.getList().add(new OTGECGFileDto(listFat128.get(i), false, "GMT+08"));
                }
                otgAdapter.setMapList(mapList);
                otgAdapter.notifyDataSetChanged();
                LoadDiaLogUtil.getInstance().dismiss();

            } else {
                showToast(getString(R.string. otg_device_notfile));
            }


        } catch (IOException e) {
            e.printStackTrace();
            setMsg(e.getMessage() + "\n");
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void setMsg(final String msg) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tv_report_desc.append(msg + "\n");
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });

    }

    @Override
    public void onItemPosListener(int pos) {

        if (pos < 0) {
            return;
        }

        if (mPos != -1 && pos != mPos && mapList.get(pos) != null) {
            otgAdapter.setUpdateView(mPos);
        }

        mPos = pos;

    }


    @Override
    public void onItemPosChindListener(int pos) {

        Log.i(TAG, "onItemPosChindListener: " + pos + "|" + mPos);
        if (pos != -1 && mPos != -1) {
            for (int i = 0; i < mapList.size(); i++) {
                if (i == mPos) {
                    List<OTGECGFileDto> listDto = mapList.get(i).getList();
                    for (int j = 0; j < listDto.size(); j++) {
                        if (j == pos) {
                            mOTGECGFileDto = listDto.get(j);
                            Log.i(TAG, "onItemPosChindListener: " + (mOTGECGFileDto == null));
                            return;
                        }

                    }
                }
            }
        }
        mOTGECGFileDto = null;
    }


    @Override
    public void onClick(View v) {

        if (v == btn_order&&mapList.size()>0) {
            if (CUR_ORDER == OrderEnum.DESC) {
                btn_order.setText(getString(R.string.ecd_file_order_desc));
                CUR_ORDER = OrderEnum.ASC;
            } else {
                btn_order.setText(getString(R.string.ecd_file_order_asc));
                CUR_ORDER = OrderEnum.DESC;
            }
            ArrayUtil.SortOTGECGDtoList(mapList, CUR_ORDER);
            otgAdapter.setMapList(mapList);
            otgAdapter.notifyDataSetChanged();
        } else if (v == btn_report_commit) {
            if (mOTGECGFileDto != null) {
                Log.i(TAG, "onClick: "+mOTGECGFileDto);

                final CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(this)
                        .setIco(getResources().getDrawable(R.drawable.report_ico))
                        .setTitle(getResources().getString(R.string.titl_report_create))
                        .setMessage(getResources().getString(R.string.tip_otg_report))
                        .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int which) {

                                dialog.dismiss();

                                LoadDiaLogUtil.getInstance().show(OTGActivity.this, false);

                                String reportNumber= HealthECGUtil.buildReportNumberByFile(mOTGECGFileDto.getFat128Sys().getFilename());

                                FormBody body = new FormBody.Builder()
                                        .add("report_type", "" + ReportType.HOUR24.name())
                                        .add("report_number", reportNumber)
                                        .build();

                                Call call = mRestService.generateReport(body);

                                call.enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LoadDiaLogUtil.getInstance().dismiss();
                                                showToast(getString(R.string.server_error));
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String json = response.body().string();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                LoadDiaLogUtil.getInstance().dismiss();
                                                dialog.dismiss();
                                                ResultData<Object> result = Utility.jsonToObject(json, new TypeToken<ResultData<Object>>() {
                                                }.getType());

                                                if (result == null) {
                                                    return;
                                                }
                                                if(result.getCode()==HttpFinal.CODE_200||result.getCode()==HttpFinal.CODE_230){
                                                    startPackageWork();
                                                }else{
                                                    showToast(result.getMessage(), Toast.LENGTH_LONG);
                                                }

                                            }
                                        });


                                    }
                                });

                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();

            } else {
                showToast(getString(R.string.tip_otg_not_file));
            }
        }

    }

    private class ReadUSBFileWork extends AsyncTask<Fat128Sys, Integer, String> {


        @Override
        protected String doInBackground(Fat128Sys... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPackDialogCall.switchView(true);
                    DiaLogUtil.DialogShow(packageDialog, OTGActivity.this, -1);
                }
            });
            String ecdFilename= HealthECGUtil.buildReportNumberByFileName( params[0].getFilename());
            String zipFilename= HealthECGUtil.buildReportNumberByFileZipName( params[0].getFilename());
            File ecdFile = new File(mHealthPath.getOtgzipdat(),ecdFilename);
            if (ecdFile.exists()) {
                ecdFile.delete();
            }
            File mZipFile=new File(mHealthPath.getOtgzipdat(),zipFilename);
            Log.i(TAG, "doInBackground: "+ecdFile.getAbsolutePath());
            if(mZipFile.exists()){
                zipFilePath=mZipFile.getAbsolutePath();
                return  zipFilePath;
            }


            int LengthSector = (int) Math.ceil((params[0].getLength() / DEFAYLT_SECTOR));


            int mStartFatSector = USBFunUtil.cluster_to_sector(params[0].getStartSectors(), mRootSectorIndex);
            //int mStartFatSector=1024;
            int mEndFatSector = LengthSector;
            int _EndFatSector = mStartFatSector + mEndFatSector;
            FileOutputStream fos = null;

            try {

                int readLength = (int) Math.ceil(mEndFatSector / DEFAYLT_CLUSTER);

                fos = new FileOutputStream(ecdFile);
                for (int i = 0, j = 1; j <= readLength; i++, j++) {
                    usbStorageDeviceManage.getBlockDevice().init();
                    int startReadSector = (int) (mStartFatSector + (i * DEFAYLT_CLUSTER));
                    int endReadSector = (int) (mStartFatSector + (j * DEFAYLT_CLUSTER));

                    if (endReadSector > _EndFatSector) {
                        int bufferLength = (int) (_EndFatSector - (endReadSector - DEFAYLT_CLUSTER));
                        ByteBuffer userDataBuffer = ByteBuffer.allocate((int) (bufferLength * DEFAYLT_SECTOR));
                        Log.i(TAG, "doInBackground: ++++++++>>>> " + j + "|" + startReadSector + "|" + userDataBuffer.array().length);
                        setMsg("doInBackground: ++++++++>>>> " + j + "|" + startReadSector + "|" + userDataBuffer.array().length + "\n");
                        usbStorageDeviceManage.getBlockDevice().read(startReadSector, userDataBuffer);
                        fos.write(userDataBuffer.array());
                        // Log.i(TAG, "doInBackground: "+startReadSector+"|"+_EndFatSector);
                    } else {
                        ByteBuffer userDataBuffer = ByteBuffer.allocate((int) (512 * 32));
                        usbStorageDeviceManage.getBlockDevice().read(startReadSector, userDataBuffer);
                        Log.i(TAG, "doInBackground: -------->>>>" + j + "|" + startReadSector + "|" + (512 * 32));
                        setMsg("doInBackground: -------->>>>" + j + "|" + startReadSector + "|" + (512 * 32) + "\n");
                        //Log.i(TAG, "doInBackground: "+startReadSector+"|"+endReadSector);
                        fos.write(userDataBuffer.array());
                    }
                    Log.i(TAG, "doInBackground: " + startReadSector + "|" + endReadSector + "|" + _EndFatSector);
                    publishProgress((int) ((i / (float) readLength) * 100));
                }


                fos.flush();
                fos.close();
                fos=null;

                zipFilePath = ZipUtil.zip(ecdFile.getAbsolutePath());

                if (zipFilePath == null) {
                    throw new IOException();
                }

                File file = new File(zipFilePath);
                if (!file.exists()) {
                    throw new IOException();
                }

                if (ecdFile.exists()) {
                    ecdFile.delete();
                }



            } catch (Exception e) {

                zipFilePath=null;
                if (ecdFile.exists()) {
                    ecdFile.delete();
                }
                mHandler.sendEmptyMessage(FinalBisa.OTG_READ_ERROR);
                return null;
            } finally {
                if (fos != null) {

                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }


            return zipFilePath;
        }

        @Override
        protected void onPreExecute() {
            mPackDialogCall.setProgress(0);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //启动文件上传对话框
            if(StringUtils.isEmpty(s)){

                mHandler.sendEmptyMessage(FinalBisa.OTG_READ_ERROR);
            }else{
                DiaLogUtil.DialogDismiss(packageDialog, OTGActivity.this);
               startUploadWork();
            }




        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mPackDialogCall.setProgress(values[0]);
        }
    }
    private  int mSizeCount=0;
    private class UploadFileWork extends AsyncTask<String, Integer, File> {
        @Override
        protected File doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUpDialogCall.switchView(true);
                    DiaLogUtil.DialogShow(uploadDialog, OTGActivity.this, -1);
                }
            });


            try{

                String zipPathFile=params[0];
                Log.i(TAG, "doInBackground: "+zipPathFile);
                File zipFile=new File(zipPathFile);
                String reportNumber= zipFile.getName().split("\\.")[0];
                if(StringUtils.isEmpty(reportNumber)){
                    throw  new RuntimeException("File build error");
                }
                Map<String,String> param=new HashMap<String,String>();
                param.put("reportNumber",reportNumber);

                Map<String,File> mapFile=new HashMap<String,File>();
                mapFile.put(zipFile.getName(),zipFile);
                final long readLength=zipFile.length();
                mSizeCount=0;
                mDataService.updatedata(param, mapFile, new ICallUploadInterface() {
                    @Override
                    public void callUploadSize(int size) {
                        mSizeCount+=size;
                        //setMsg("UploadFileWork Run"+mSizeCount+"\n");
                        publishProgress((int) ((mSizeCount / (float) readLength) * 100));
                    }
                });

                return zipFile;

            }catch (Exception e){
                showToast(e.getMessage());
                mHandler.sendEmptyMessage(OTG_UPLOAD_ERROR);
                return null;
            }

        }

        @Override
        protected void onPreExecute() {
            mUpDialogCall.setProgress(0);
        }

        @Override
        protected void onPostExecute(File file) {

            if(file==null){
                mHandler.sendEmptyMessage(FinalBisa.OTG_UPLOAD_ERROR);
            }else{

                long curTime= DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
                UploadContentValues uploadContentValues=new UploadContentValues();
                uploadContentValues.putUserGuid(mUser.getUser_guid());
                String strTz = java.util.TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
                uploadContentValues.putUptime(DateUtil.getUTimeStr(curTime,strTz));
                uploadContentValues.putFilename(mOTGECGFileDto.getFat128Sys().getFilename());
                getContentResolver().insert(uploadContentValues.uri(),uploadContentValues.values());

                if(file.exists()){
                    file.delete();
                }
                DiaLogUtil.DialogDismiss(uploadDialog, OTGActivity.this);

                final CustomDefaultDialog.Builder defaultDialog; defaultDialog=new CustomDefaultDialog.Builder(OTGActivity.this)
                .setMessage(getString(R.string.otg_upload_seccess))
                .setIco(getResources().getDrawable(R.drawable.report_ico))
                .setTitle(getResources().getString(R.string.titl_report_create))
                .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                defaultDialog.create().show();

            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mUpDialogCall.setProgress(values[0]);
        }
    }


    public  void startPackageWork() {
        synchronized (lockObj) {
            if (mOTGECGFileDto != null) {
                Fat128Sys mFat128 = mOTGECGFileDto.getFat128Sys();
                ReadUSBFileWork readUsbWork = new ReadUSBFileWork();
                readUsbWork.execute(mFat128);
            }
        }

    }

    public  void startUploadWork() {
        synchronized (lockObj) {
            if (zipFilePath != null) {
                UploadFileWork uploadFileWork = new UploadFileWork();
                uploadFileWork.execute(zipFilePath);
            }
        }

    }

    public Handler mHandler = new Handler()// 声明消息处理器
    {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FinalBisa.OTG_READ_ERROR:
                    mPackDialogCall.switchView(false);
                    break;
                case FinalBisa.OTG_UPLOAD_ERROR:
                    mUpDialogCall.switchView(false);
                    break;
                default:

                    break;
            }
        }

    };


}
