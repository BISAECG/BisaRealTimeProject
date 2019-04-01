package com.bisa.health.camera.lib.funsdk.support;

import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.lib.MsgContent;

/**
 * Created by zhangyongyong on 2017-07-25-08:55.
 */

public interface OnAddSubDeviceResultListener extends OnFunListener{
    /***子设备添加成功
     * @param funDevice
     * @param msgContent
     */
    void onAddSubDeviceFailed(FunDevice funDevice, MsgContent msgContent);

    /****子设备添加失败
     *
     * @param funDevice
     * @param msgContent
     */
    void onAddSubDeviceSuccess(FunDevice funDevice, MsgContent msgContent);

}
