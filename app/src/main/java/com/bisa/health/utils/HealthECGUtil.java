package com.bisa.health.utils;

/**
 * Created by Administrator on 2018/8/24.
 */

public class HealthECGUtil {

    public static String buildReportNumberByFile(String fileName){
        String reportNumber=null;
        String[] a1=fileName.split("\\.");
        if(a1.length!=2){
            return null;
        }
        if(a1[0].indexOf("HC+")==-1){
            reportNumber=a1[0].replaceAll("HC","HC+");
            return reportNumber;
        }
        return null;
    }

    public static String buildReportNumberByFileName(String fileName){

        if(fileName.indexOf("HC+")==-1){
            String mFileName=fileName.replaceAll("HC","HC+");
            return mFileName;
        }
        return null;
    }
    public static String buildReportNumberByFileZipName(String fileName){

        if(fileName.indexOf("HC+")==-1){
            String mFileName=fileName.replaceAll("HC","HC+");
            String[] a=mFileName.split("\\.");

            return a[0]+".zip";
        }
        return null;
    }
}
