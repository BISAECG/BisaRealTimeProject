package com.bisa.health.cust;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bisa.health.MainActivity;
import com.bisa.health.OtherWechatBindActivity;
import com.bisa.health.R;
import com.bisa.health.auth.CryptogramService;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.WxUserInfo;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.LoginTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.ArrayUtil;
import com.bisa.health.utils.DateUtil;
import com.bisa.health.utils.GsonUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.ToastUtil;
import com.bisa.health.utils.Utility;
import com.bisa.health.utils.WxConstants;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/13.
 */

public class OtherLoginFragment extends Fragment implements  View.OnClickListener{

    private static final String TAG = "OtherLoginFragment";
    private ImageView img_wechat;
    private IWXAPI api;
    private SharedPersistor sharedPersistor;
    private RestServiceImpl restService;
    private HealthServer mHealthServer;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getActivity(), WxConstants.APP_ID, false);
        api.registerApp(WxConstants.APP_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        sharedPersistor=new SharedPersistor(getActivity());
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        view = inflater.inflate(R.layout.other_login_layout, container, false);
        img_wechat = (ImageView) view.findViewById(R.id.img_wechat);
        img_wechat.setOnClickListener(this);

        restService = new RestServiceImpl(getActivity(),mHealthServer);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v == img_wechat) {//微信登入
            if (!Utility.isConnectInternet(getActivity())) {
                ToastUtil.getInstance(getActivity()).show(getString(R.string.error_400));
                return;
            }
            if (!api.isWXAppInstalled()) {
                ToastUtil.getInstance(getActivity()).show(getString(R.string.dialog_wx_notinstall));
                return;
            }

            final SendAuth.Req req = new SendAuth.Req();
            // 授权读取用户信息
            req.scope = "snsapi_userinfo";
            // 自定义信息
            req.state = "bisa_login";
            // 向微信发送请求
            api.sendReq(req);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WxUserInfo wx_user = sharedPersistor.flashLoad(WxUserInfo.class.getName(), true);
        Log.i(TAG, "onResume: " + (wx_user == null));
        if (wx_user != null) {
            LoadDiaLogUtil.getInstance().show(getActivity(), false);
            wxLogin(wx_user);
        }
    }

    /**
     * 微信登入
     */
    public synchronized  void wxLogin(final WxUserInfo wxUser) {


        final Long requestStart = SystemClock.elapsedRealtime();
        synchronized (this) {

            String timeStamp=""+ DateUtil.getServerMilliSeconds(mHealthServer.getTimeZone());
            String digest= CryptogramService.getInstance().hmacDigest(
                    ArrayUtil.sort(new String[]{"username","area_code","time_zone","loginType","clientKey","timeStamp"},
                            new String[]{wxUser.getOpenid(), mHealthServer.getAreaCode(), mHealthServer.getTimeZone(),LoginTypeEnum.WECHAT.name(),wxUser.getOpenid(),timeStamp}));

            FormBody body = new FormBody.Builder()
                    .add("username", wxUser.getOpenid())
                    .add("area_code",  mHealthServer.getAreaCode())
                    .add("time_zone",  mHealthServer.getTimeZone())
                    .add("loginType",  LoginTypeEnum.WECHAT.name())
                    .add("clientKey", wxUser.getOpenid())
                    .add("digest",digest)
                    .add("timeStamp",timeStamp)
                    .build();

            Call call = restService.wxLogin(body);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.getInstance(getActivity()).show(getResources().getString(R.string.server_error));
                            LoadDiaLogUtil.getInstance().dismiss();
                            return;
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String json = response.body().string();
                    Log.i(TAG, "onResponse: "+json);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadDiaLogUtil.getInstance().dismiss();
                            ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                            if(result==null){
                                return;
                            }

                            if(result.getCode() == HttpFinal.CODE_200||result.getCode() == HttpFinal.CODE_201||result.getCode() == HttpFinal.CODE_206) {
                                sharedPersistor.saveObject(result.getData());
                                mHealthServer.setToken(result.getToken());
                                sharedPersistor.saveObject(mHealthServer);

                                switch (result.getCode()){
                                    case HttpFinal.CODE_200:
                                        ActivityUtil.startActivity(getActivity(),MainActivity.class,true,ActionEnum.DOWN);
                                        break;
                                    case HttpFinal.CODE_206:
                                        ActivityUtil.startActivity(getActivity(),OtherWechatBindActivity.class,true,ActionEnum.DOWN);
                                        break;
                                }
                                return;
                            }else {
                                ToastUtil.getInstance(getActivity()).show(result.getMessage());
                            }
                        }
                    });


                }
            });


        }


    }

}
