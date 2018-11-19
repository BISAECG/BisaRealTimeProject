package com.bisa.health.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.model.Device;

import java.util.ArrayList;

import static com.bisa.health.model.BleNamePreSuffix.BLE_CAMERA;
import static com.bisa.health.model.BleNamePreSuffix.BLE_ECG;


public class DeviceAdapter extends BaseAdapter {

	private  static final String TAG = "DeviceAdapter";
	private ArrayList<Device> mBleDevice;
	private LayoutInflater mInflater;
	private boolean isDel;
	private boolean isAllSelect;
	private Activity parent;
	private IOnItemSelectListener mIOnItemSelectListener;

	public static interface IOnItemSelectListener{
		public void onItemClick(int selectSize,int maxSize);
	};


	public DeviceAdapter(Activity parent,IOnItemSelectListener mIOnItemSelectListener) {
		super();
		mBleDevice = new ArrayList<Device>();
		mInflater = parent.getLayoutInflater();
		this.parent = parent;
		this.mIOnItemSelectListener=mIOnItemSelectListener;
	}

	public boolean isDel() {
		return isDel;
	}

	public boolean isAllSelect() {
		return isAllSelect;
	}

	public void setAllSelect(boolean isAllSelect) {
		this.isAllSelect=isAllSelect;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

	public void addBleDevice(Device ch) {
		if (mBleDevice.contains(ch) == false) {
			mBleDevice.add(ch);
		}
	}

	public void addBleDevice(int index, Device ch) {
		mBleDevice.set(index, ch);
	}

	public Device getBleDevice(int index) {
		return mBleDevice.get(index);
	}

	public ArrayList<Device> getmBleDevice() {
		return mBleDevice;
	}

	public void clearList() {
		mBleDevice.clear();
	}

	@Override
	public int getCount() {
		return mBleDevice.size();
	}

	@Override
	public Object getItem(int position) {
		return getBleDevice(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FieldReferences fields;
		PairDeviceAdapter.ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_list_dev, null);
			fields = new FieldReferences();
			fields.icopath = (ImageView) convertView.findViewById(R.id.img_dev_logo);
			fields.devname = (TextView) convertView.findViewById(R.id.txt_dev_name);
			fields.devnum = (TextView) convertView.findViewById(R.id.txt_dev_address);
			fields.isClass = (ImageView) convertView.findViewById(R.id.img_run);
			fields.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_all);
			convertView.setTag(fields);
		} else {
			fields = (FieldReferences) convertView.getTag();
		}
		final Device bleDevice = getBleDevice(position);
		if (bleDevice != null) {


			int icoFlag=bleDevice.getIcoflag();
			fields.icopath.setImageBitmap(toBitMap(parent.getContext(),icoFlag));
			fields.devname.setText(bleDevice.getDevname());
			fields.devnum.setText(bleDevice.getDevnum());

			if (isDel) {
				fields.checkbox.setVisibility(View.VISIBLE);
				fields.checkbox.setChecked(false);
				fields.isClass.setVisibility(View.GONE);
			} else {
				fields.checkbox.setVisibility(View.GONE);
				fields.isClass.setVisibility(View.VISIBLE);
			}

			Log.i(TAG, "getView: "+bleDevice.getCheckbox());
			if(this.isAllSelect){
				mBleDevice.get(position).setCheckbox(1);
				fields.checkbox.setChecked(true);
			}else{
				mBleDevice.get(position).setCheckbox(0);
				fields.checkbox.setChecked(false);
			}

			// 注意这里设置的不是onCheckedChangListener，还是值得思考一下的
			fields.checkbox.setOnClickListener(new CompoundButton.OnClickListener() {

				@Override
				public void onClick(View v) {
					CheckBox ckBox=(CheckBox) v;
					if(ckBox.isChecked()){
						mBleDevice.get(position).setCheckbox(1);
					}else{
						mBleDevice.get(position).setCheckbox(0);
					}

					int size=0;
					for(Device mDevice : mBleDevice){
						if(mDevice.getCheckbox()==1){
							size++;
						}
					}
					mIOnItemSelectListener.onItemClick(size,mBleDevice.size());
				}
			});

		}

		return convertView;

	}

	private class FieldReferences {
		ImageView icopath;
		TextView devname;
		TextView devnum;
		ImageView isClass;
		CheckBox checkbox;
	}

	// 手写的从int到enum的转换函数
	public static Bitmap toBitMap(Context context, int index) {

		Drawable d;
		BitmapDrawable bd;
		Bitmap bm;
		switch (index) {
			case 0:
				d = context.getResources().getDrawable(BLE_ECG.getResource());
				bd = (BitmapDrawable) d;
				bm = bd.getBitmap();
				return bm;
			case 1:

				d = context.getResources().getDrawable(BLE_CAMERA.getResource());
				bd = (BitmapDrawable) d;
				bm = bd.getBitmap();
				return bm;

			default:
				return null;
		}
	}

}
