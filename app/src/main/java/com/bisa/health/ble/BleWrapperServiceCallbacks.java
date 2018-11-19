package com.bisa.health.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface BleWrapperServiceCallbacks {


	public void callDeviceFound(final BluetoothDevice device, int rssi, byte[] record);
	
	public void callDeviceConnected(final BluetoothGatt gatt,
                                    final BluetoothDevice device);
	
	public void callDeviceDisconnected(final BluetoothGatt gatt,
                                       final BluetoothDevice device);
	
	public void callAvailableServices(final BluetoothGatt gatt,
                                      final BluetoothDevice device,
                                      final BluetoothGattService services);
	
	public void callCharacteristicForService(final BluetoothGatt gatt,
                                             final BluetoothDevice device,
                                             final BluetoothGattService service,
                                             final List<BluetoothGattCharacteristic> chars);

	public void callCharacteristicsDetails(final BluetoothGatt gatt,
                                           final BluetoothDevice device,
                                           final BluetoothGattService service,
                                           final BluetoothGattCharacteristic characteristic);
	
	public void callNewValueForCharacteristic(final BluetoothGatt gatt,
                                              final BluetoothDevice device,
                                              final BluetoothGattService service,
                                              final BluetoothGattCharacteristic ch,
                                              final byte[] rawValue);
	
	public void callGotNotification(final BluetoothGatt gatt,
                                    final BluetoothDevice device,
                                    final BluetoothGattService service,
                                    final BluetoothGattCharacteristic characteristic);
	
	public void callSuccessfulWrite(final BluetoothGatt gatt,
                                    final BluetoothDevice device,
                                    final BluetoothGattService service,
                                    final BluetoothGattCharacteristic ch,
                                    final String description);
	
	public void callFailedWrite(final BluetoothGatt gatt,
                                final BluetoothDevice device,
                                final BluetoothGattService service,
                                final BluetoothGattCharacteristic ch,
                                final String description);
	
	public void callNewRssiAvailable(final BluetoothGatt gatt, final BluetoothDevice device, final int rssi);
	
	/* define Null Adapter class for that interface */
	public static class Null implements BleWrapperServiceCallbacks {



		@Override
		public void callDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
			
		}

		@Override
		public void callDeviceConnected(BluetoothGatt gatt, BluetoothDevice device) {
			
		}

		@Override
		public void callDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
			
		}

		@Override
		public void callAvailableServices(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService services) {
			
		}

		@Override
		public void callCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {
			
		}

		@Override
		public void callCharacteristicsDetails(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic characteristic) {
			
		}


		@Override
		public void callGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic characteristic) {
			
		}

		@Override
		public void callSuccessfulWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic ch, String description) {
			
		}

		@Override
		public void callFailedWrite(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service,
				BluetoothGattCharacteristic ch, String description) {
			
		}

		@Override
		public void callNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, int rssi) {
			
		}

		@Override
		public void callNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device,
				BluetoothGattService service, BluetoothGattCharacteristic ch, byte[] rawValue) {
			// TODO Auto-generated method stub
			
		}}
}
