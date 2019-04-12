package com.bisa.health.camera.lib.funsdk.support;

import android.os.Message;

import com.lib.MsgContent;

public interface FunVideoViewListener extends OnFunListener {
    void startPlay(Message msg, MsgContent msgContent);
    void onPlayInfo(MsgContent msgContent);
    void onPlayEnd();
    void onPlayBufferBegin();
    void onPlayBufferEnd();
    void onFrameUsrData(Message msg, MsgContent msgContent);
}
