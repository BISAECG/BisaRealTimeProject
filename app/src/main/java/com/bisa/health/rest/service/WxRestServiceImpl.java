package com.bisa.health.rest.service;

import android.content.Context;

import com.bisa.health.model.HealthServer;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.HttpHelp;
import com.bisa.health.model.AccessToken;
import com.bisa.health.model.WxUserInfo;
import com.bisa.health.utils.WxConstants;

import java.util.Map;

/**
 * Created by Administrator on 2017/8/2.
 */

public class WxRestServiceImpl extends HttpHelp  implements IWxRestService {


    public WxRestServiceImpl(Context context, HealthServer mHealthServer) {
        super(context,mHealthServer,1);
    }

    @Override
    public AccessToken getAccesstokenByCode(Map<String,String>  param) {
            String url=WxConstants.WeiXinOpenIdUrl;
            AccessToken accessToken=get(url,param,AccessToken.class, HttpFinal.DEFALUT_CONN_HTTP);
            return accessToken;
    }

    @Override
    public WxUserInfo getWxUserinfo(Map<String,String>  param) {
        String url=WxConstants.WeixinUserInfoUrl;
        WxUserInfo wxUserInfo=get(url,param,WxUserInfo.class, HttpFinal.DEFALUT_CONN_HTTP);
        return wxUserInfo;
    }


}
