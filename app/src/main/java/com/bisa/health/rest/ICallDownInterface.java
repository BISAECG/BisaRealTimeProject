package com.bisa.health.rest;

/**
 * Created by Administrator on 2018/5/23.
 */

public interface ICallDownInterface {
    public void onDownloadFailed();
    public void onDownloading(int size);
    public void onDownloadSuccess();
}
