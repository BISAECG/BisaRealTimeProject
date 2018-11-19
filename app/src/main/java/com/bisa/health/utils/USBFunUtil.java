package com.bisa.health.utils;

/**
 * Created by Administrator on 2018/5/8.
 */

public class USBFunUtil {

    public static int cluster_to_sector(int startClusters,int roorSector){
        int sectors=(startClusters-2)*32+32;
        return sectors+roorSector;
    }
}
