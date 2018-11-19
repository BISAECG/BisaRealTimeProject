package com.bisa.health.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.github.mjdev.libaums.driver.BlockDeviceDriver;
import com.github.mjdev.libaums.driver.BlockDeviceDriverFactory;
import com.github.mjdev.libaums.partition.PartitionTable;
import com.github.mjdev.libaums.usb.UsbCommunication;
import com.github.mjdev.libaums.usb.UsbCommunicationFactory;


/**
 * Created by Administrator on 2017/12/28.
 */

public class UsbStorageDeviceManage {

    private UsbManager usbManager;
    private UsbDeviceConnection mConnection;
    private UsbDevice usbDevice;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private BlockDeviceDriver blockDevice;
    private Context context;
    private UsbStorageDeviceManage(){}

    public UsbStorageDeviceManage(Context context){
        this.context=context;

    }

    public  UsbStorageDeviceManage init(UsbDevice usbDevice,UsbManager usbManager) {
        this.usbManager=usbManager;
        if(usbDevice == null) {
            return null;
        }
        usbInterface = usbDevice.getInterface(0);

        // U盘接口0可获取的端点数为2
        if(usbInterface.getEndpointCount() != 2) {
            return null;
        } else {
            this.inEndpoint = usbInterface.getEndpoint(0);   // Bulk-In端点
            this.outEndpoint = usbInterface.getEndpoint(1);  // Bulk_Out端点
        }
        if(inEndpoint==null||outEndpoint==null){
            return null;
        }

            UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
            if (connection != null && connection.claimInterface(usbInterface, true)) {
                mConnection = connection;
            } else {
                mConnection = null;
            }
        if (mConnection == null) {
            return null;
        }

        UsbCommunication communication = UsbCommunicationFactory.createUsbCommunication(mConnection, outEndpoint, inEndpoint);
        byte[] b = new byte[1];
        mConnection.controlTransfer(0b10100001, 0b11111110, 0, usbInterface.getId(), b, 1, 5000);

        BlockDeviceDriver blockDevice = BlockDeviceDriverFactory.createBlockDevice(communication);
        PartitionTable partitionTable=null;
        this.blockDevice=blockDevice;
        return this;

    }

    public UsbManager getUsbManager() {
        return usbManager;
    }

    public void setUsbManager(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    public UsbDeviceConnection getmConnection() {
        return mConnection;
    }

    public void setmConnection(UsbDeviceConnection mConnection) {
        this.mConnection = mConnection;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public UsbInterface getUsbInterface() {
        return usbInterface;
    }

    public void setUsbInterface(UsbInterface usbInterface) {
        this.usbInterface = usbInterface;
    }

    public UsbEndpoint getInEndpoint() {
        return inEndpoint;
    }

    public void setInEndpoint(UsbEndpoint inEndpoint) {
        this.inEndpoint = inEndpoint;
    }

    public UsbEndpoint getOutEndpoint() {
        return outEndpoint;
    }

    public void setOutEndpoint(UsbEndpoint outEndpoint) {
        this.outEndpoint = outEndpoint;
    }

    public BlockDeviceDriver getBlockDevice() {
        return blockDevice;
    }

    public void setBlockDevice(BlockDeviceDriver blockDevice) {
        this.blockDevice = blockDevice;
    }

    public void close(){
        if(this.mConnection == null||this.usbInterface==null) return;
        boolean release = mConnection.releaseInterface(usbInterface);
        if (!release) {
            Log.i("----","could not release interface!");
        }
        this.mConnection.close();
    }
}
