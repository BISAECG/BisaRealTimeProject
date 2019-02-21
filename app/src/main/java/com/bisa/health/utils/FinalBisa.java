package com.bisa.health.utils;

/**
 * Created by Administrator on 2017/8/23.
 */

public abstract class FinalBisa {
    public final static int ALAM_CHECK_INTERVAL=5000;
    public final static long SYNC_TIME=1000;//2*60
    public final static int PLAY_AlAM=1001;
    public final static int NOTIFI_AlAM=1002;

    public final static int HEALTH_ALAM=3;
    public final static int HEALTH_REPORT_HOURS=1;
    public final static int HEALTH_REPORT_DAY=2;
    public final static int NOTIFI_AlAM_SUCCESS=10;
    public final static int NOTIFI_AlAM_FINAL=11;
    public final static int NEWWPRK_SATAUS_SUCCESS=20;
    public final static int NEWWPRK_SATAUS_FAIL=21;
    public final static int UPDATE_REAL_TIME=22;

    public final static int SERVER_BACK_STATUS=23;
    public final static int SERVER_BACK_STOP=0;
    public final static int SERVER_BACK_START=1;

    public final static int UPDATE_DAT=200;

    public final static int ECG_CONFIG=1000;
//
//    public final static int REPORT_DATA_OK=2;
//    public final static int REPORT_DATA_NOTOK=1;

    public final static int REPORT_1_TYPE=12;
    public final static int REPORT_15_TYPE=10;
    public final static int REPORT_24_TYPE=11;


    public final static int REPORT_FREE_MSG=60;
    public final static int REPORT_DEPTH_MSG=61;


    //OTG

    public final static int OTG_READ_ERROR=400;
    public final static int OTG_UPLOAD_ERROR=401;
    public final static int OTG_READ_SUCCESS=200;
    public final static int OTG_UPDATE_FILE=200;


    //CallResult
    public final static int CALL_ECG_CODE=10001;
   // public final static int REPORT1_MONITORING_TIME=1;
   // public final static int REPORT15_MONITORING_TIME=15;
}
