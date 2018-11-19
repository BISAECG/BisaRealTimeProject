package com.bisa.health.wxapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.bisa.health.AppManager;
import com.bisa.health.BaseActivity;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.AccessToken;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.WxUserInfo;
import com.bisa.health.utils.WxConstants;
import com.bisa.health.rest.service.IWxRestService;
import com.bisa.health.rest.service.WxRestServiceImpl;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	private IWXAPI api;
    private IWxRestService wxRestService;
    private  TelephonyManager manager;
    private SharedPersistor sharedPersistor;
    private static final String TAG = "WXEntryActivity";
    private Toast mToast;
    private HealthServer mHealthServer;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        api = WXAPIFactory.createWXAPI(this, WxConstants.APP_ID, false);
        sharedPersistor=new SharedPersistor(this);
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        if(wxRestService==null){
            wxRestService=new WxRestServiceImpl(this,mHealthServer);
        }
        AppManager.getAppManager().addActivity(this);

		// 将你收到的intent和实现IWXAPIEventHandler接口的对象传递给handleIntent方法

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
           boolean wxResult=api.handleIntent(getIntent(), this);
            if(!wxResult){
                finish();
            }
            Log.i(TAG, "onCreate: "+wxResult);
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

	@Override
	protected void onNewIntent(Intent intent) {

        Log.i(TAG, "onNewIntent: ");
        super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);

	}

	@Override
	public void onReq(BaseReq res) {

        Log.i(TAG, "onReq: ");


	}

	@Override
	public void onResp(BaseResp resp) {

        Log.i(TAG, "onResp: ");
        String code = null;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                code = ((SendAuth.Resp) resp).code;
                Log.i(TAG,"Code:"+code);
                getWeiXinInfo(code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:

                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                finish();
                break;
                
            default:

                finish();
                break;
        }


		
	}
	
	// 线程TOST
	public void showToast(String text) {
		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_LONG);
		toast.show();
	}
	
    public void getWeiXinInfo(String code) {
        new AsyncTask<String,Integer,WxUserInfo>(){
            @Override
            protected WxUserInfo doInBackground(String... params) {

                Map<String,String> param=new HashMap<String,String>();
                param.put("code",params[0]);
                AccessToken accessToken = wxRestService.getAccesstokenByCode(param);

                if(accessToken!=null){
                    param=new HashMap<String, String>();
                    param.put("access_token",accessToken.getAccess_token());
                    param.put("openid",accessToken.getOpenid());
                    WxUserInfo wxUserInfo=wxRestService.getWxUserinfo(param);
                    Log.i(TAG, "onPostExecute: "+wxUserInfo);
                    return wxUserInfo;
                }
                return null;
            }

            @Override
            protected void onPostExecute(WxUserInfo wxUserInfo) {

                if(wxUserInfo!=null){
                    Log.i(TAG, "onPostExecute: "+wxUserInfo);
                    sharedPersistor.flashSave(wxUserInfo);
                }

                finish();
                overridePendingTransition(0, 0);
            }

        }.execute(code);

    }

    
    
}