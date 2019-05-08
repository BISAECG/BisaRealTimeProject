package com.bisa.health;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisa.health.adapter.DeviceListAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.ble.BleWrapper;
import com.bisa.health.ble.BleWrapperServiceCallbacks;
import com.bisa.health.cache.CacheManage;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustSpinerPopWindow;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.dao.IDeviceDao;
import com.bisa.health.model.BleNamePreSuffix;
import com.bisa.health.model.Device;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.DeviceTypeEnum;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.LoadDiaLogUtil;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.position.OnBaseCallback;
import zhy.com.highlight.shape.RectLightShape;
import zhy.com.highlight.view.HightLightView;

public class AddActivity extends BaseActivity
        implements IAdapterClickInterFace {

    private final static String TAG = "AddActivity";

    private DeviceListAdapter findDevAdapter;

    private TextView tv_productnum_value;

    private CustSpinerPopWindow custFindSpiner;

    private Button btnPairDev;

    private Button btn_pair_commit;

    private Handler mHandler = new Handler();


    private int findDeviceIndex = -1;

    private IDeviceDao deviceDao;

    private SharedPersistor sharedPersistor;
    private User mUser;
    public HighLight mHightLight1 = null;
    public HighLight mHightLight2 = null;
    public HighLight mHightLight3 = null;
    /**
     * 蓝牙操作
     */
    private boolean mScanning;
    private BleWrapper mBleWrapper;
    private static final long SCANNING_TIMEOUT = 8 * 1000; /* 5 seconds */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_activity);
        AppManager.getAppManager().addActivity(this);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        deviceDao = new DeviceDao(this);

        tv_productnum_value = (TextView) this.findViewById(R.id.tv_productnum_value);
        btnPairDev = (Button) this.findViewById(R.id.btn_dev_pair);
        btnPairDev.setOnClickListener(new ButtonEventHandler());

        btn_pair_commit = (Button) this.findViewById(R.id.btn_pair_commit);
        btn_pair_commit.setOnClickListener(new ButtonEventHandler());

        findDevAdapter = new DeviceListAdapter(this);
        custFindSpiner = new CustSpinerPopWindow(this);
        custFindSpiner.setMonItemPairDevListener(this);
        custFindSpiner.setDeviceListAdapter(findDevAdapter);


        mBleWrapper = new BleWrapper(this, new BleWrapperServiceCallbacks.Null() {

            @Override
            public void callDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
                handleFoundDevice(device, rssi, record);
            }

        });
        if (!mBleWrapper.initialize()) {
            showToast(getResources().getString(R.string.title_sys_not_support));
            finish();
        }

    }

    public void remove(HighLight mHightLight) {
        mHightLight.remove();
    }

    public void initHightLight() {
        mHightLight1 = new HighLight(this)//
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .autoRemove(false)
                .addHighLight(R.id.btn_dev_pair, R.layout.info_ecg1, new OnBaseCallback(20) {

                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.leftMargin = rectF.left;
                        marginInfo.bottomMargin = bottomMargin + rectF.height() + offset;
                    }
                }, new RectLightShape())
                .addHighLight(R.id.btn_dev_pair, R.layout.info_ecg_next, new OnBaseCallback(100) {
                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.rightMargin = 0;
                        marginInfo.topMargin = 1;
                    }
                }, new RectLightShape());


        mHightLight2 = new HighLight(this)//
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .autoRemove(false)
                .addHighLight(R.id.tv_productnum_value, R.layout.info_ecg2, new OnBaseCallback(20) {

                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.leftMargin = rectF.left;
                        marginInfo.topMargin = rectF.top + rectF.height()+offset;
                    }
                }, new RectLightShape())
                .addHighLight(R.id.tv_productnum_value, R.layout.info_ecg_next, new OnBaseCallback(100) {
                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.rightMargin = 0;
                        marginInfo.topMargin = 1;
                    }
                }, new RectLightShape());

        mHightLight3 = new HighLight(this)//
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .autoRemove(false)
                .addHighLight(R.id.btn_pair_commit, R.layout.info_ecg3, new OnBaseCallback(20) {

                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.leftMargin = rectF.left;
                        marginInfo.topMargin = rectF.top - rectF.height()-offset;
                    }
                }, new RectLightShape())
                .addHighLight(R.id.btn_pair_commit, R.layout.info_ecg_iknow, new OnBaseCallback(100) {
                    @Override
                    public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                        marginInfo.rightMargin = 0;
                        marginInfo.topMargin = 1;
                    }
                }, new RectLightShape());
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //界面初始化后显示高亮布局


        SharedPreferences setting =getSharedPreferences(CacheManage.SHARE_APP_TAG,  Context.MODE_PRIVATE);
        Boolean user_first = setting.getBoolean(this.getClass().getName(),true);
        if(user_first){//第一次
            setting.edit().putBoolean(this.getClass().getName(), false).commit();
            initHightLight();
            mHightLight1.show();
        }

    }

    public void clickKnown(View view)
    {

        HightLightView mHightLight= (HightLightView) view.getParent().getParent();
       TextView v= mHightLight.findViewById(R.id.tv_tiptitle);
        if(v.getTag().equals("next1")){
            remove(mHightLight1);
            mHightLight2.show();
            Log.i(TAG, "clickKnown1: ");
        }else if(v.getTag().equals("next2")){
            remove(mHightLight2);
            mHightLight3.show();
            Log.i(TAG, "clickKnown2: ");
        }else if(v.getTag().equals("end")){
            remove(mHightLight3);
            Log.i(TAG, "clickKnown3: ");
        }

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mBleWrapper != null) {
            mScanning = false;
            mBleWrapper.stopScanning();
        }
    }


    /**
     * Button 处理时间
     */
    class ButtonEventHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // 匹配按钮
            if (v == btnPairDev) {

                findDevAdapter.clearList();
                findDevAdapter.notifyDataSetChanged();
                tv_productnum_value.setText("");
                if (!mScanning) {
                    LoadDiaLogUtil.getInstance().show(AddActivity.this, false);
                    btnPairDev.setEnabled(false);
                    btnPairDev.setBackground(getResources().getDrawable(R.drawable.pair_disabled));
                    mScanning = true;
                    addScanningTimeout();
                    mBleWrapper.startScanning();
                }
            }
            /**
             * 确认添加按钮
             */
            if (v == btn_pair_commit) {

                if (findDeviceIndex != -1 && !"".equals(tv_productnum_value.getText())) {


                    final Device bleDevice = new Device();
                    BluetoothDevice _bleDevice = findDevAdapter.getDevice(findDeviceIndex);
                    BleNamePreSuffix bleEnum = BleNamePreSuffix.valueOf(DeviceTypeEnum.ECG.vlaueOf());
                    bleDevice.setDevname(getResources().getString(bleEnum.getDevName()));
                    bleDevice.setMacadderss(_bleDevice.getAddress());
                    bleDevice.setDevnum(_bleDevice.getName());
                    bleDevice.setConnstatus(bleEnum.getConnStatus());
                    bleDevice.setClzName(bleEnum.getClzName());
                    bleDevice.setIcoflag(DeviceTypeEnum.ECG.vlaueOf());
                    bleDevice.setUser_guid(mUser.getUser_guid());
                    deviceDao.add(bleDevice);
                    ActivityUtil.startActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP,AddActivity.this,MainActivity.class,false,ActionEnum.NULL);


                }
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressWarnings("unused")
    private synchronized void handleFoundDevice(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int anIndex = BleNamePreSuffix.valueOf(DeviceTypeEnum.ECG.vlaueOf(), device.getName());
                if (anIndex != -1) {
                    findDevAdapter.addDevice(device);
                }
            }
        });
    }


    /**
     * 获取获得的蓝牙设备
     */
    @Override
    public void onItemClick(int pos) {
        findDeviceIndex = pos;
        BluetoothDevice blueDev = findDevAdapter.getDevice(pos);
        if (blueDev != null)
            tv_productnum_value.setText(blueDev.getName());
    }

    /*
     * make sure that potential scanning will take no longer than
     * <SCANNING_TIMEOUT> seconds from now on
     */
    private synchronized void addScanningTimeout() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if (mBleWrapper == null)
                    return;
                mScanning = false;
                mBleWrapper.stopScanning();
                btnPairDev.setEnabled(true);
                btnPairDev.setBackground(getResources().getDrawable(R.drawable.pair_enabled));


                if (findDevAdapter.getCount() > 0) {

                    if (findDevAdapter.getCount() == 1) {

                        findDeviceIndex = 0;
                        Object object = findDevAdapter.getItem(0);
                        if (object != null) {
                            String deviceName = ((BluetoothDevice) object).getName();
                            tv_productnum_value.setText(deviceName);
                        } else {
                            tv_productnum_value.setText("");
                        }
                    } else {
                        custFindSpiner.showAsDropDown(tv_productnum_value);
                    }
                    findDevAdapter.notifyDataSetChanged();

                }
                LoadDiaLogUtil.getInstance().dismiss();

            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }

}
