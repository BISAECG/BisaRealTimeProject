package com.bisa.health.usb.fat;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 */

public class FsysFat {
    private final static int FS_NAME=0;
    private final static int FS_EXTENSION_NAME=8;
    private final static int FS_ATTR_NAME=11;
    private final static int FS_RESERVED=12;

    private final static int FS_CREATE_TIME=14;
    private final static int FS_CREATE_DATE=16;
    private final static int FS_UPDATE_DATE=18;
    private final static int FS_FIRST_INDEX_HIGH=20;

    private final static int FS_MODIFY_TIME=22;
    private final static int FS_MODIFY_DATE=24;
    private final static int FS_FIRST_INDEX_LOW=26;
    private final static int FS_DATA_LENGTH=28;

    private String fs_name;
    private String fs_extension_name;
    private int fs_attr_name;

    private String fs_reserved;

    private int fs_create_time;
    private int fs_create_date;
    private int fs_update_date;

    private int fs_first_index_high;

    private int fs_modfiy_time;
    private int fs_modfiy_date;
    private int fs_first_index_low;
    private int fs_data_length;

    public String getFs_name() {
        return fs_name;
    }

    public void setFs_name(String fs_name) {
        this.fs_name = fs_name;
    }

    public String getFs_extension_name() {
        return fs_extension_name;
    }

    public void setFs_extension_name(String fs_extension_name) {
        this.fs_extension_name = fs_extension_name;
    }

    public int getFs_attr_name() {
        return fs_attr_name;
    }

    public void setFs_attr_name(int fs_attr_name) {
        this.fs_attr_name = fs_attr_name;
    }

    public String getFs_reserved() {
        return fs_reserved;
    }

    public void setFs_reserved(String fs_reserved) {
        this.fs_reserved = fs_reserved;
    }

    public int getFs_create_time() {
        return fs_create_time;
    }

    public void setFs_create_time(int fs_create_time) {
        this.fs_create_time = fs_create_time;
    }

    public int getFs_create_date() {
        return fs_create_date;
    }

    public void setFs_create_date(int fs_create_date) {
        this.fs_create_date = fs_create_date;
    }

    public int getFs_update_date() {
        return fs_update_date;
    }

    public void setFs_update_date(int fs_update_date) {
        this.fs_update_date = fs_update_date;
    }

    public int getFs_first_index_high() {
        return fs_first_index_high;
    }

    public void setFs_first_index_high(int fs_first_index_high) {
        this.fs_first_index_high = fs_first_index_high;
    }

    public int getFs_modfiy_time() {
        return fs_modfiy_time;
    }

    public void setFs_modfiy_time(int fs_modfiy_time) {
        this.fs_modfiy_time = fs_modfiy_time;
    }

    public int getFs_modfiy_date() {
        return fs_modfiy_date;
    }

    public void setFs_modfiy_date(int fs_modfiy_date) {
        this.fs_modfiy_date = fs_modfiy_date;
    }

    public int getFs_first_index_low() {
        return fs_first_index_low;
    }

    public void setFs_first_index_low(int fs_first_index_low) {
        this.fs_first_index_low = fs_first_index_low;
    }

    public int getFs_data_length() {
        return fs_data_length;
    }

    public void setFs_data_length(int fs_data_length) {
        this.fs_data_length = fs_data_length;
    }

    private final static String USDATA="USDATA";
    private final static int _INDEX=512;

    public static List<FsysFat> read(ByteBuffer buffer){
        List<FsysFat> list=new ArrayList<FsysFat>();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] byteDat=null;
        int offset=32;

        Log.i(TAG, "read: "+buffer.array().length);
        for(int i=0;i<_INDEX;i++){
            FsysFat result = new FsysFat();

            byteDat=new byte[8];
            buffer.position(i*offset+FS_NAME);
            buffer.get(byteDat,0,byteDat.length);
            result.fs_name=new String(byteDat);

            byteDat=new byte[3];
            buffer.position(i*offset+FS_EXTENSION_NAME);
            result.fs_extension_name=new String(byteDat);

            byteDat=new byte[1];
            buffer.position(i*offset+FS_ATTR_NAME);
            result.fs_attr_name=buffer.get()&0xff;

            buffer.position(i*offset+FS_CREATE_TIME);
            result.fs_create_time=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_CREATE_DATE);
            result.fs_create_date=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_UPDATE_DATE);
            result.fs_update_date=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_FIRST_INDEX_HIGH);
            result.fs_first_index_high=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_MODIFY_TIME);
            result.fs_modfiy_time=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_MODIFY_DATE);
            result.fs_modfiy_date=buffer.getShort()&0xffff;

            buffer.position(i*offset+FS_FIRST_INDEX_LOW);
            result.fs_first_index_low=buffer.getShort()&0xfffff;

            buffer.position(i*offset+FS_DATA_LENGTH);
            result.fs_data_length=buffer.getInt()&0xffffffff;
            list.add(result);
//            if(result.fs_name.equals(USDATA)){
//                return result;
//            }


        }

        return list;

    }

    private static final String TAG = "FsysFat";
    @Override
    public String toString() {
        return "FsysFat{" +
                "fs_name=" + fs_name +
                ", fs_extension_name=" + fs_extension_name +
                ", fs_attr_name=" + fs_attr_name +
                ", fs_reserved=" + fs_reserved +
                ", fs_modfiy_time=" + fs_modfiy_time +
                ", fs_modfiy_date=" + fs_modfiy_date +

                ", fs_create_time=" + fs_create_time +
                ", fs_create_date=" + fs_create_date +
                ", fs_update_date=" + fs_update_date +
                ", fs_first_index_high=" + fs_first_index_high +
                ", fs_first_index_low=" + fs_first_index_low +
                ", fs_data_length='" + fs_data_length + '\'' +
                '}';
    }
    private static String switchAttr(int attr){
//        switch (attr){
//            case 0:
//
//                break;
//            case 1:
//                break;
//            case 2:
//                break;
//            case 4:
//                break;
//            case 4:
//                break;
//            case 8:
//                break;
//            case 16:
//                break;
//            case 24:
//                break;
//        }
        return null;
    }

}
