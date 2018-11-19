package com.bisa.health.rest.service;

import com.bisa.health.rest.ICallDownInterface;
import com.bisa.health.rest.ICallUploadInterface;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/8.
 */

public interface IDataService {
    public Object updatedata(Map<String, String> map, Map<String, File> files, ICallUploadInterface callUploadInterface) throws IOException;
    public void download(File file,Map<String, String> param,ICallDownInterface callDownInterface);

}
