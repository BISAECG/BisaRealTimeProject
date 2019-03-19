package com.bisa.health.rest;

/**
 * Created by Administrator on 2017/7/29.
 */

public interface HttpFinal {


    int CONN_BASIC_HTTP=1;
    int CONN_CUST_HTTPS=3;
    int CONN_HTTPS=2;
    int DEFALUT_CONN_HTTP=4;

   // int UPDATE_DATA=10;


    /**
     * 返回状态码ODL版本
     */
    int  ERROR=500;
     int SUCCESS=200;
     int FAIL=400;
     int NOT_REG=201;
     int REG_SUCCESS=202;
     int LOGIN_SUCCESS=203;
     int NOT_USERINFO=204;
     int OPT_SUCCESS=205;
     int EVENT_SUUCESS=213;
     int EVENT_FAIL=214;
     int PARAM_ERROR=215 ;
     int NOT_EVENT=216;
     int NOT_HEALTH_SERVICE=217;
     int SOS_SUCCESS=218;
     int REPORT_TIME_INVAlID=218;
     int REPORT_NOT_DATA=219;
     int BIND_OTHER_EXISTS=224;
    int FAIL_401=401;
    int FAIL_402=402;
     int RESTART_LOGIN=223;
    int REG_NOTAUTH_LOGIN=222;
    /**
     * 返回状态码新版本
     */



    int CODE_400=400;//操作异常
     int CODE_401=401;//认证不通过

     int CODE_200=200; //操作成功
     int CODE_201=201; //操作成功需要更新
     int CODE_203=203; //已存在
     int CODE_204=204; //密码错误
     int CODE_205=205; //验证码错误
     int CODE_206=206; //需要绑定手机号码

     int CODE_230=230; //报告存在
     int CODE_223=223; //需要重新登入
     int CODE_217=217;//金额不足

     int CODE_216=216; //没有紧急联系人

    /**
     * 信鸽推送状态码
     */
     int STATUS_400=400;
     int STATUS_200=200;

    /**
     * MSG
     */

     int DOWN_SUCCESS_200=217;//金额不足

     int DOWN_ING_203=203; //没有紧急联系人



}
