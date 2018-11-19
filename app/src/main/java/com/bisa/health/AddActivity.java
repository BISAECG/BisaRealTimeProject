package com.bisa.health;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bisa.health.adapter.DeviceListAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.ble.BleWrapper;
import com.bisa.health.ble.BleWrapperServiceCallbacks;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.CustSpinerPopWindow;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.dao.IDeviceDao;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.BleNamePreSuffix;
import com.bisa.health.model.Device;
import com.bisa.health.model.enumerate.DeviceTypeEnum;
import com.bisa.health.model.User;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.LoadDiaLogUtil;

public class AddActivity extends BaseActivity
		implements IAdapterClickInterFace {

	private final static String TAG = "AddActivity";

	private DeviceListAdapter findDevAdapter;

	private TextView tv_productnum_value;

	private CustSpinerPopWindow custFindSpiner;

	private Button btnPairDev;

	private Button btn_pair_commit;

	private Handler mHandler = new Handler();


	private int findDeviceIndex=-1;

	private IDeviceDao deviceDao;

	private SharedPersistor sharedPersistor;
	private User mUser;


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
		sharedPersistor=new SharedPersistor(this);
		mUser=sharedPersistor.loadObject(User.class.getName());
		deviceDao=new DeviceDao(this);

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
			show_Toast(getResources().getString(R.string.title_sys_not_support));
			finish();
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
					LoadDiaLogUtil.getInstance().show(AddActivity.this, true);
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

				if (findDeviceIndex!=-1 && !"".equals(tv_productnum_value.getText())) {


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
					deviceDao.updateOrSave(bleDevice);
					ActivityUtil.finishAnim(AddActivity.this, ActionEnum.BACK);


				}
			}

		}

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
		findDeviceIndex=pos;
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


				if(findDevAdapter.getCount()>0){

					if (findDevAdapter.getCount() == 1) {

						findDeviceIndex=0;
						Object object=findDevAdapter.getItem(0);
						if(object!=null){
							String deviceName= ((BluetoothDevice)object).getName();
							tv_productnum_value.setText(deviceName);
						}else{
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
