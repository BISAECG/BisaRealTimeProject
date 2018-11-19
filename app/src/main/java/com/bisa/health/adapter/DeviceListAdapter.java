package com.bisa.health.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

	
	private ArrayList<BluetoothDevice> mDevices;
	private LayoutInflater mInflater;
	
	public DeviceListAdapter(Activity par) {
		super();
		mDevices  = new ArrayList<BluetoothDevice>();
		mInflater = par.getLayoutInflater();
	}
	
	public void addDevice(BluetoothDevice device) {
		if(mDevices.contains(device) == false) {
			mDevices.add(device);
		}
	}
	
	public BluetoothDevice getDevice(int index) {
		if(mDevices.size()>0)
			return mDevices.get(index);
		else
			return  null;
	}
	
	
	public void clearList() {
		mDevices.clear();
	}
	
	@Override
	public int getCount() {
		return mDevices.size();
	}

	@Override
	public Object getItem(int position) {

		return getDevice(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get already available view or create new if necessary
		FieldReferences fields;
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
        	fields = new FieldReferences();
        	fields.mTextView = (TextView) convertView.findViewById(R.id.tv_spiner_txt);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }			
		
        // set proper values into the view
        BluetoothDevice device = mDevices.get(position);
        String name = device.getName();
        if(name == null || name.length() <= 0) name = "Unknown Device";
        fields.mTextView.setText(name);
		return convertView;
	}
	
	private class FieldReferences {
		TextView mTextView;
	}
}
