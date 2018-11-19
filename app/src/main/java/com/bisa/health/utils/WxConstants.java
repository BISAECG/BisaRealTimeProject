package com.bisa.health.utils;

/**
 * Created by Administrator on 2017/8/2.
 */

public  abstract class WxConstants {
    static public final String APP_ID="wx10cb6e84c36d25b0";
    static public final String APP_SECRET="4ecf0d823a8dca975491a4218d1d9ae2";
    static public final String WeiXinOpenIdUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?"
            + "appid="
            + WxConstants.APP_ID
            + "&secret="
            + WxConstants.APP_SECRET
            + "&grant_type=authorization_code";
    static public final String WeixinUserInfoUrl="https://api.weixin.qq.com/sns/userinfo";
}
