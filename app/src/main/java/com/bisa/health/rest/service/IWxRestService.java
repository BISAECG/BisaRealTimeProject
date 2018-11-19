package com.bisa.health.rest.service;

import com.bisa.health.model.AccessToken;
import com.bisa.health.model.WxUserInfo;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/2.
 */

public interface IWxRestService {
    public AccessToken getAccesstokenByCode(Map<String,String> param);
    public WxUserInfo getWxUserinfo(Map<String,String>  param);

}
