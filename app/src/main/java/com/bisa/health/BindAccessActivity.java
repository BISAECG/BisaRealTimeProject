package com.bisa.health;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bisa.health.adapter.SecurityDefaultAdapter;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustBindDefaultPopView;
import com.bisa.health.cust.view.IOnClickCallInterFace;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.AdapteDefaultDto;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.VerifyTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.Utility;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/25.
 */

public class BindAccessActivity extends BaseActivity implements PopupWindow.OnDismissListener, View.OnClickListener, IOnClickCallInterFace {


    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private List<AdapteDefaultDto> mList=new ArrayList<AdapteDefaultDto>();
    private UserBindDto userBindDto;
    private CustBindDefaultPopView custBindPopView;


    private static final String TAG = "BindAccessActivity";

    private SecurityDefaultAdapter defaultAdapterTip;
    private ListView listView = null;
    private AdapteDefaultDto adapteDefaultDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_security_layout);
        sharedPersistor = new SharedPersistor(this);

        mUser = sharedPersistor.loadObject(User.class.getName());

        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService =  new RestServiceImpl(this,mHealthServer);
        defaultAdapterTip = new SecurityDefaultAdapter(this);

        listView = (ListView) this.findViewById(R.id.lv_list);
        listView.setAdapter(defaultAdapterTip);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapteDefaultDto = null;
                if (position < 0) {
                    return;
                }

                adapteDefaultDto = (AdapteDefaultDto) defaultAdapterTip.getItem(position);
                if (adapteDefaultDto.getIndex() == VerifyTypeEnum.PWD.name()) {
                    ActivityUtil.startActivity(BindAccessActivity.this,ModifyPwdActivity.class, false, ActionEnum.NEXT);
                } else {

                    if (adapteDefaultDto.getIndex() == VerifyTypeEnum.PHONE.name()) {
                        custBindPopView.setTitle(getString(R.string.title_replace_iphone));
                        custBindPopView.setMessage(getString(R.string.title_replace_iphone_warning));
                        userBindDto.setBindType(VerifyTypeEnum.PHONE.name());
                    } else if (adapteDefaultDto.getIndex() == VerifyTypeEnum.EMAIL.name()) {
                        custBindPopView.setTitle(getString(R.string.title_replace_mail));
                        custBindPopView.setMessage(getString(R.string.title_replace_mail_warning));
                        userBindDto.setBindType(VerifyTypeEnum.EMAIL.name());
                    }

                    custBindPopView.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    setBackgroundAlpha(0.5f);
                }


            }
        });

        custBindPopView = new CustBindDefaultPopView(this);
        custBindPopView.setItemOnClickListener(this);
        custBindPopView.setOnDismissListener(this);


        showDialog(false);
        Call call = mRestService.getAccount();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.title_error_try));
                        dialogDismiss();
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
                        dialogDismiss();

                        Log.i(TAG, "onResponse: " + json);
                        ResultData<UserBindDto> result = Utility.jsonToObject(json, new TypeToken<ResultData<UserBindDto>>() {
                        }.getType());
                        if (result == null) {
                            return;
                        }

                        if (result.getCode() == HttpFinal.CODE_200) {
                            userBindDto=result.getData();
                            updateComponents(userBindDto);
                        }

                    }
                });


            }
        });


    }


    protected void updateComponents(UserBindDto userBindDto) {
        mList.clear();
        AdapteDefaultDto adaptePwd=new AdapteDefaultDto();
        adaptePwd.setTitle(getString(R.string.bind_modify));
        adaptePwd.setIndex(VerifyTypeEnum.PWD.name());
        adaptePwd.setFlag(userBindDto.getUsername());
        mList.add(adaptePwd);

        AdapteDefaultDto adaptePhone=new AdapteDefaultDto();
        adaptePhone.setTitle(StringUtils.isEmpty(userBindDto.getPhone())?getString(R.string.bind_isnot):userBindDto.getPhone());
        adaptePhone.setIndex(VerifyTypeEnum.PHONE.name());
        adaptePhone.setFlag(userBindDto.getUsername());
        mList.add(adaptePhone);

        AdapteDefaultDto adapteEmail=new AdapteDefaultDto();
        adapteEmail.setTitle(StringUtils.isEmpty(userBindDto.getEmail())?getString(R.string.bind_isnot):userBindDto.getEmail());
        adapteEmail.setIndex(VerifyTypeEnum.EMAIL.name());
        adapteEmail.setFlag(userBindDto.getUsername());
        mList.add(adapteEmail);

        defaultAdapterTip.setData(mList);
        defaultAdapterTip.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(boolean isStatus) {

        if (isStatus && userBindDto != null) {
                Bundle body=new Bundle();
                body.putSerializable(UserBindDto.class.getName(),userBindDto);
                ActivityUtil.startActivity(BindAccessActivity.this, BindAccessTypeActivity.class, false,body,ActionEnum.NEXT);
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
