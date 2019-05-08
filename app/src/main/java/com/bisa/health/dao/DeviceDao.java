package com.bisa.health.dao;

import android.content.Context;
import android.net.Uri;

import com.bisa.health.model.Device;
import com.bisa.health.provider.device.DeviceContentValues;
import com.bisa.health.provider.device.DeviceCursor;
import com.bisa.health.provider.device.DeviceSelection;

import java.util.ArrayList;
import java.util.List;

public class DeviceDao implements IDeviceDao {

	private Context context;

	public DeviceDao(Context context) {
		this.context = context;
	}

	@Override
	public Device add(Device device) {
		DeviceContentValues deviceContentValues=device.toDeviceContentValues();
		Uri uri=deviceContentValues.insert(context);
		if(uri!=null){
			return device;
		}
		return null;

	}

	@Override
	public int delete(String deviceName) {
		DeviceSelection where=new DeviceSelection();
		where.devname(deviceName);
		DeviceContentValues deviceContentValues=new DeviceContentValues();
		return context.getContentResolver().delete(deviceContentValues.uri(),where.sel(),where.args());
	}

	public int deleteByUserSn(int guid, String sn) {
		DeviceSelection where=new DeviceSelection();
		where.userGuid(guid);
		where.and();
		where.devnum(sn);
		DeviceContentValues deviceContentValues=new DeviceContentValues();
		return context.getContentResolver().delete(deviceContentValues.uri(),where.sel(),where.args());
	}
	public Device upOrSaveByUser(Device device) {
		try{
			DeviceSelection where=new DeviceSelection();
			where.userGuid(device.getUser_guid());
			where.and();
			where.devnum(device.getDevnum());

			DeviceCursor deviceCursor= where.query(context);
			int count=deviceCursor.getCount();
			deviceCursor.close();
			if(count>0){
				return upDeviceByUser(device);
			}else{
				return add(device);
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public Device upDeviceByUser(Device device) {
		DeviceSelection where=new DeviceSelection();
		where.userGuid(device.getUser_guid());
		where.and();
		where.devnum(device.getDevnum());

		DeviceContentValues deviceContentValues=device.toDeviceContentValues();
		int  dbCount=deviceContentValues.update(context,where);
		if(dbCount<=0){
			return null;
		}
		return device;
	}

	@Override
	public Device upDevice(Device device) {
		DeviceSelection where=new DeviceSelection();
		where.userGuid(device.getUser_guid());

		DeviceContentValues deviceContentValues=device.toDeviceContentValues();
		int  dbCount=deviceContentValues.update(context,where);
		if(dbCount<=0){
			return null;
		}
		return device;
	}

	@Override
	public Device loadDeviceByClzName(String className) {
		DeviceSelection where=new DeviceSelection();
		where.clzname(className).limit(1);
		DeviceCursor deviceCursor=where.query(context);
		if(deviceCursor.getCount()>0){
			deviceCursor.moveToFirst();
			Device device=new Device().toDevice(deviceCursor);
			deviceCursor.close();
			return device;
		}
		return null;
	}

	@Override
	public Device loadDeviceByDevName(String devName) {
		DeviceSelection where=new DeviceSelection();
		where.devname(devName).limit(1);
		DeviceCursor deviceCursor=where.query(context);
		if(deviceCursor.getCount()>0){
			deviceCursor.moveToFirst();
			Device device=new Device().toDevice(deviceCursor);
			deviceCursor.close();
			return device;
		}
		return null;
	}

	@Override
	public Device loadDeviceByMac(String mac) {
		DeviceSelection where=new DeviceSelection();
		where.macadderss(mac).limit(1);
		DeviceCursor deviceCursor=where.query(context);
		if(deviceCursor.getCount()>0){
			deviceCursor.moveToFirst();
			Device device=new Device().toDevice(deviceCursor);
			deviceCursor.close();
			return device;
		}
		return null;
	}

	@Override
	public List<Device> list() {
		List<Device> listDevice=new ArrayList<Device>();
		DeviceSelection where=new DeviceSelection();
		DeviceCursor deviceCursor=where.query(context);
		while(deviceCursor.moveToNext()){
			listDevice.add(new Device().toDevice(deviceCursor));
		}
		deviceCursor.close();
		return listDevice;
	}

	@Override
	public List<Device> list(int user_guid) {
		List<Device> listDevice=new ArrayList<Device>();
		DeviceSelection where=new DeviceSelection();
		where.userGuid(user_guid);
		DeviceCursor deviceCursor=where.query(context);
		while(deviceCursor.moveToNext()){
			listDevice.add(new Device().toDevice(deviceCursor));
		}
		deviceCursor.close();
		return listDevice;
	}

	private static final String TAG = "DeviceDao";
	@Override
	public Device updateOrSave(Device device) {
		try{
			DeviceSelection where=new DeviceSelection();
			where.userGuid(device.getUser_guid());
			DeviceCursor deviceCursor= where.query(context);
			int count=deviceCursor.getCount();
			deviceCursor.close();
			if(count>0){
				return upDevice(device);
			}else{
				return add(device);
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}
}