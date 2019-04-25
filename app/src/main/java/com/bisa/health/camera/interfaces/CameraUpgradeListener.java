package com.bisa.health.camera.interfaces;

import android.os.Message;

import com.bisa.health.camera.lib.funsdk.support.OnFunListener;
import com.lib.MsgContent;

public interface CameraUpgradeListener extends OnFunListener {
    void devCheckUpgrade(Message msg, MsgContent msgContent);
    void devStartUpgrade(Message msg, MsgContent msgContent);
    void devStopUpgrade();
    void devOnUpgradeProgress(Message msg, MsgContent msgContent);
    void devSetJson(Message msg, MsgContent msgContent);
}
