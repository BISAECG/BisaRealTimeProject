package com.bisa.health.rest;

/**
 * Created by Administrator on 2017/7/29.
 */

public interface HttpFinal {


    public final int CONN_BASIC_HTTP=1;
    public final int CONN_CUST_HTTPS=3;
    public final int CONN_HTTPS=2;
    public final int DEFALUT_CONN_HTTP=4;

   // public final int UPDATE_DATA=10;


    /**
     * 返回状态码ODL版本
     */
    public final int  ERROR=500;
    public  final int SUCCESS=200;
    public  final int FAIL=400;
    public  final int NOT_REG=201;
    public  final int REG_SUCCESS=202;
    public  final int LOGIN_SUCCESS=203;
    public  final int NOT_USERINFO=204;
    public  final int OPT_SUCCESS=205;
    public  final int EVENT_SUUCESS=213;
    public  final int EVENT_FAIL=214;
    public  final int PARAM_ERROR=215 ;
    public  final int NOT_EVENT=216;
    public  final int NOT_HEALTH_SERVICE=217;
    public  final int SOS_SUCCESS=218;
    public  final int REPORT_TIME_INVAlID=218;
    public  final int REPORT_NOT_DATA=219;
    public  final int BIND_OTHER_EXISTS=224;
    public final int FAIL_401=401;
    public final int FAIL_402=402;
    public  final int RESTART_LOGIN=223;
    public final int REG_NOTAUTH_LOGIN=222;
    /**
     * 返回状态码新版本
     */



    public static final int CODE_400=400;//操作异常
    public static final int CODE_401=401;//认证不通过

    public static final int CODE_200=200; //操作成功
    public static final int CODE_201=201; //操作成功需要更新
    public static final int CODE_203=203; //已存在
    public static final int CODE_204=204; //密码错误
    public static final int CODE_205=205; //验证码错误
    public static final int CODE_206=206; //需要绑定手机号码

    public static final int CODE_230=230; //报告存在
    public static final int CODE_223=223; //需要重新登入
    public static final int CODE_217=217;//金额不足

    public static final int CODE_216=216; //没有紧急联系人

    /**
     * 信鸽推送状态码
     */
    public final static int STATUS_400=400;
    public final static int STATUS_200=200;

    /**
     * MSG
     */

    public static final int DOWN_SUCCESS_200=217;//金额不足

    public static final int DOWN_ING_203=203; //没有紧急联系人



}
