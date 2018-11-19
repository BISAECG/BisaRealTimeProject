package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.bisa.health.adapter.SecurityDefaultAdapter;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.CustBindDefaultPopView;
import com.bisa.health.cust.IOnClickCallInterFace;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.WxUserInfo;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.LoginTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.Utility;
import com.bisa.health.utils.WxConstants;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessActivity extends BaseActivity implements PopupWindow.OnDismissListener, View.OnClickListener, IOnClickCallInterFace {


    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private List<UserBindDto> mList = new ArrayList<UserBindDto>();
    private CustBindDefaultPopView custBindPopView;
    private IWXAPI api;

    private static final String TAG = "BindAccessActivity";

    private SecurityDefaultAdapter defaultAdapterTip;
    private ListView listView = null;
    private UserBindDto userBindDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_security_layout);
        sharedPersistor = new SharedPersistor(this);
        api = WXAPIFactory.createWXAPI(this, WxConstants.APP_ID, false);
        api.registerApp(WxConstants.APP_ID);

        mUser = sharedPersistor.loadObject(User.class.getName());

        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);
        defaultAdapterTip = new SecurityDefaultAdapter(this);
        listView = (ListView) this.findViewById(R.id.lv_list);
        listView.setAdapter(defaultAdapterTip);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                userBindDto = null;
                if (position < 0) {
                    return;
                }

                userBindDto = (UserBindDto) defaultAdapterTip.getItem(position);
                if (userBindDto.getLoginType() == LoginTypeEnum.PWD) {
                    ActivityUtil.startActivity(BindAccessActivity.this,ModifyPwdActivity.class, false, ActionEnum.NEXT);
                } else {

                    if (userBindDto.getLoginType() == LoginTypeEnum.PHONE) {
                        custBindPopView.setTitle(getString(R.string.title_replace_iphone));
                        custBindPopView.setMessage(getString(R.string.title_replace_iphone_warning));
                    } else if (userBindDto.getLoginType() == LoginTypeEnum.EMAIL) {
                        custBindPopView.setTitle(getString(R.string.title_replace_mail));
                        custBindPopView.setMessage(getString(R.string.title_replace_mail_warning));
                    } else if (userBindDto.getLoginType() ==LoginTypeEnum.WECHAT) {
                        custBindPopView.setTitle(getString(R.string.title_replace_weixin));
                        custBindPopView.setMessage(getString(R.string.title_replace_weixin_warning));
                    }

                    custBindPopView.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    setBackgroundAlpha(0.5f);
                }


            }
        });

        custBindPopView = new CustBindDefaultPopView(this);
        custBindPopView.setItemOnClickListener(this);
        custBindPopView.setOnDismissListener(this);


        show_Dialog(false);
        Call call = mRestService.getAccount();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show_Toast(getString(R.string.title_error_try));
                        dialog_Dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String json = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_Dismiss();

                        Log.i(TAG, "onResponse: " + json);
                        ResultData<List<UserBindDto>> result = Utility.jsonToObject(json, new TypeToken<ResultData<List<UserBindDto>>>() {
                        }.getType());
                        if (result == null) {
                            return;
                        }

                        if (result.getCode() == HttpFinal.CODE_200) {
                            List<UserBindDto> _mList = result.getData();
                            updateComponents(_mList);
                        }

                    }
                });


            }
        });


    }


    protected void updateComponents(List<UserBindDto> _mList) {
        mList.clear();
        for(LoginTypeEnum mEnum :LoginTypeEnum.values()){
            boolean isExists=true;
            if(mEnum==LoginTypeEnum.PWD){
                UserBindDto userBindDto=new UserBindDto();
                userBindDto.setUsername(getString(R.string.bind_modify));
                userBindDto.setLoginType(mEnum);
                mList.add(userBindDto);

            }else{
                for(UserBindDto userBindDto: _mList){
                    if(userBindDto.getLoginType().name()==mEnum.name()){
                        isExists=false;
                        mList.add(userBindDto);
                        break;
                    }
                }
                if(isExists){
                    UserBindDto userBindDto=new UserBindDto();
                    userBindDto.setUsername(getString(R.string.bind_isnot));
                    userBindDto.setLoginType(mEnum);
                    mList.add(userBindDto);
                }
            }


        }

        defaultAdapterTip.setData(mList);
        defaultAdapterTip.notifyDataSetChanged();

    }

    protected synchronized void bindAccount(FormBody param) {
        show_Dialog(true);
        Call call = mRestService.bindOther(param);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog_Dismiss();
                        show_Toast(getString(R.string.title_error_try), Toast.LENGTH_LONG);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                Log.i(TAG, "onResponse: "+json);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dialog_Dismiss();

                        ResultData<List<UserBindDto>> result = Utility.jsonToObject(json, new TypeToken<ResultData<List<UserBindDto>>>() {
                        }.getType());
                        if (result == null) {
                            return;
                        }
                        if (result.getCode() == HttpFinal.CODE_200) {
                            List<UserBindDto> _mList = result.getData();
                            updateComponents(_mList);
                            show_Toast(getString(R.string.tip_bind_success), Toast.LENGTH_LONG);
                        } else {
                            show_Toast(result.getMessage(), Toast.LENGTH_LONG);
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        WxUserInfo wx_user = sharedPersistor.flashLoad(WxUserInfo.class.getName(), true);
        Log.i(TAG, "onResume: " + (wx_user == null));

        if(wx_user!=null) {
            FormBody param = new FormBody.Builder()
                    .add("username", wx_user.getOpenid())
                    .add("loginType", LoginTypeEnum.WECHAT.name())
                    .build();
            bindAccount(param);
        }
    }

    @Override
    public void onItemClick(boolean isStatus) {

        if (isStatus && userBindDto != null) {

            if (userBindDto.getLoginType() == LoginTypeEnum.WECHAT) {
                if (!Utility.isConnectInternet(this)) {
                    show_Toast(getString(R.string.error_400));
                    return;
                }
                if (!api.isWXAppInstalled()) {
                    show_Toast(getString(R.string.dialog_wx_notinstall));
                    return;
                }

                final SendAuth.Req req = new SendAuth.Req();
                // 授权读取用户信息
                req.scope = "snsapi_userinfo";
                // 自定义信息
                req.state = "bisa_login";
                // 向微信发送请求
                api.sendReq(req);

            } else {
                sharedPersistor.flashSave(mList);
                Bundle body=new Bundle();
                body.putSerializable("BINDUSER",userBindDto);
                ActivityUtil.startActivity(BindAccessActivity.this, BindAccessTypeActivity.class, false,body,ActionEnum.NEXT);
            }


        }

    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = alpha;
        this.getWindow().setAttributes(lp);
    }
}
