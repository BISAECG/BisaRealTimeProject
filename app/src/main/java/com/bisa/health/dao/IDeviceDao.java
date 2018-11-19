package com.bisa.health.dao;

import com.bisa.health.model.Device;

import java.util.List;

public interface IDeviceDao {
	public Device add(Device device);
	public int delete(String deviceName);
	public Device upDevice(Device device);
	public Device loadDeviceByClzName(String className);
	public Device loadDeviceByDevName(String devName);
	public List<Device> list(int user_guid);
	public Device loadDeviceByMac(String mac);
	public List<Device> list();
	public Device updateOrSave(Device device);
}
