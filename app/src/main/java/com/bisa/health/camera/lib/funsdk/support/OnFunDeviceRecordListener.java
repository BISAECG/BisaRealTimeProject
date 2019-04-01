package com.bisa.health.camera.lib.funsdk.support;

import com.bisa.health.camera.lib.funsdk.support.models.FunDevRecordFile;

import java.util.List;

public interface OnFunDeviceRecordListener extends OnFunListener {

    void onRequestRecordListSuccess(List<FunDevRecordFile> files);

    void onRequestRecordListFailed(final Integer errCode);

}
