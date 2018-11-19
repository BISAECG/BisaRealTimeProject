package com.bisa.health.usb.fat;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/8.
 */

public class Fat128Sys implements Serializable{


    private final static int DESC_NAME_INDEX=96;
    private final static int SFUUIX_INDEX=104;
    private final static int START_SECTORS_INDEX=122;
    private final static int LENGTH_INDEX=124;
    private final static int EXT_RECODE_INDEX=0;
    private final static int LAST_REVODE_INDEX=0;
    private final static int FILENAME_INDEX=0;

    private String descName;
    private String suffix;
    private int startSectors;
    private int length;
    private int extRecodeIndex;
    private int lastRecodeIndex;
    private String filename;

    public String getDescName() {
        return descName;
    }

    public void setDescName(String descName) {
        this.descName = descName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getStartSectors() {
        return startSectors;
    }

    public void setStartSectors(int startSectors) {
        this.startSectors = startSectors;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getExtRecodeIndex() {
        return extRecodeIndex;
    }

    public void setExtRecodeIndex(int extRecodeIndex) {
        this.extRecodeIndex = extRecodeIndex;
    }

    public int getLastRecodeIndex() {
        return lastRecodeIndex;
    }

    public void setLastRecodeIndex(int lastRecodeIndex) {
        this.lastRecodeIndex = lastRecodeIndex;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private final static int[] FILE_INDEX_ATTR={65,67,71,73,78,80,82,84,86,88,92,94,33,35,39,41,46,48,50,52,54,56,60,62,1,3,5,7,9,14,16,18,20,22};
    //private final static int[] FILE_INDEX_ATTR={65};

    private final static int _INDEX=256;

    public static  List<Fat128Sys> read(ByteBuffer buffer){
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        List<Fat128Sys> list=new ArrayList<Fat128Sys>();
        byte[] byteDat=null;
        int offset=128;

        for(int i=0;i<_INDEX;i++){

            Fat128Sys result=new Fat128Sys();

            //判断是否删除文件
            buffer.position(i*offset+DESC_NAME_INDEX);
            int isDelete=buffer.get()&0x000000ff;
            if(isDelete==229){
                continue;
            }



            byteDat=new byte[96];
            buffer.position(i*offset+FILENAME_INDEX);
            buffer.get(byteDat,0,byteDat.length);
            String _fileName="";


            for(int j=0;j<FILE_INDEX_ATTR.length;j++){

                for(int x=0;x<byteDat.length;x++){
                    if(FILE_INDEX_ATTR[j]==x){
                        String descName=new String(new byte[]{byteDat[x]});
                        _fileName+=descName;
                    }
                }

            }

            result.filename=_fileName;

            byteDat=new byte[6];
            buffer.position(i*offset+DESC_NAME_INDEX);
            buffer.get(byteDat,0,byteDat.length);
            result.descName=new String(byteDat);

            byteDat=new byte[3];
            buffer.position(i*offset+SFUUIX_INDEX);
            buffer.get(byteDat,0,byteDat.length);
            result.suffix=new String(byteDat);

            buffer.position(i*offset+START_SECTORS_INDEX);
            result.startSectors=buffer.getShort()&0xffff;

            buffer.position(i*offset+LENGTH_INDEX);
            result.length=buffer.getInt()&0xffffffff;

//            if(result.length<60*60*250){
//                continue;
//            }

            list.add(result);
        }

        return list;
    }

    @Override
    public String toString() {
        return "Fat128Sys{" +
                "descName='" + descName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", startSectors=" + startSectors +
                ", length=" + length +
                ", extRecodeIndex=" + extRecodeIndex +
                ", lastRecodeIndex=" + lastRecodeIndex +
                ", filename='" + filename + '\'' +
                '}';
    }
}
