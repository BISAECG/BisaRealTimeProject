package com.bisa.health.ble;

/**
 * Created by Administrator on 2017/9/4.
 */

public interface BleDefinedConnStatus {
    public final static int BLE_CONN_ING=0;
    public final static int BLE_CONN_SECCUESS=1;
    public final static int BLE_CONN_FAILED=2;
    public final static int BLE_RESTART_CONN=3;
    public final static int BLE_FIND_SERVICE_FAILED=4;
    public final static int BLE_CONN_TRY_COUNT=5;
    public final static int APP_EXIT_HIDE=6;
    public final static int BLE_CONN_TIME_INTERVAL=8000;

}
