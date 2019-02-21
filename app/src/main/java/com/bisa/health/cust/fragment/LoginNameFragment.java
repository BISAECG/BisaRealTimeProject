package com.bisa.health.cust.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bisa.health.BaseFragment;
import com.bisa.health.R;
import com.bisa.health.adapter.AreaCodeAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustAreaCodePopView;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.AreaUtil;
import com.bisa.health.utils.CountDownTimerUtils;
import com.bisa.health.utils.GsonUtil;
import com.bisa.health.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/13.
 */

public class LoginNameFragment extends BaseFragment implements View.OnTouchListener, View.OnClickListener,PopupWindow.OnDismissListener,IAdapterClickInterFace{

    private static final String TAG = "LoginNameFragment";
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private RestServiceImpl restService;
    private View view;
    private ServerDto mAreaDto;
    private AreaCodeAdapter areaCodeAdapter;
    private List<ServerDto> mListType; // 类型列表
    private CustAreaCodePopView custAreaCodePopView;
    private LinearLayout ll_area_code;
    private TextView tv_areacode;
    private EditText tv_iphone;
    private Button imgbtn_smsSend;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        sharedPersistor=new SharedPersistor(getActivity());
        mHealthServer=sharedPersistor.loadObject(HealthServer.class.getName());
        restService = new RestServiceImpl(getActivity(),mHealthServer);
        view = inflater.inflate(R.layout.login_name_fragment, container, false);
        areaCodeAdapter = new AreaCodeAdapter(getActivity());
        custAreaCodePopView = new CustAreaCodePopView(getActivity());
        custAreaCodePopView.setAdatper(areaCodeAdapter);
        custAreaCodePopView.setItemListener(this);
        custAreaCodePopView.setOnDismissListener(this);

        ll_area_code = (LinearLayout) view.findViewById(R.id.ll_area_code);
        ll_area_code.setOnTouchListener(this);
        tv_areacode = (TextView) view.findViewById(R.id.tv_areacode);
        tv_iphone = (EditText) view.findViewById(R.id.tv_iphone);

        imgbtn_smsSend = (Button) view.findViewById(R.id.imgbtn_smsSend);
        imgbtn_smsSend.setOnClickListener(this);
        mListType=AreaUtil.getListArea(getContext());
        areaCodeAdapter.setmObjects(mListType);
        areaCodeAdapter.notifyDataSetChanged();
        Log.i(TAG, "onCreateView: "+mListType.size());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v==imgbtn_smsSend){
            {

                /**
                 * 发送验证码
                 */

                String username = tv_iphone.getText().toString();
                String phonechode = tv_areacode.getText().toString();

                if (username.isEmpty() || phonechode.isEmpty()) {
                    ToastUtil.getInstance(getActivity()).show(getString(R.string.other_bind_area_iphone));
                    return;
                }



                synchronized (this) {


                    showDialog(false);
                    Map<String,String> param=new HashMap<String,String>();
                    param.put("phonecode",phonechode);
                    param.put("iphone",username);
                    Call call=restService.sendCodeByIphone(param);

                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    showToast(getString(R.string.network_error));

                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String json = response.body().string();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                    final CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(imgbtn_smsSend, 60000, 1000, getActivity());
                                    mCountDownTimerUtils.start();
                                    ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                                    if(result==null){
                                        return;
                                    }

                                    if(result.getCode()== HttpFinal.CODE_203){
                                        ToastUtil.getInstance(getActivity()).show(getString(R.string.tip_user_not_regeidt));
                                    }
                                }
                            });


                        }
                    });

                    /**
                     * END发送验证码
                     */
                }


            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    @Override
    public void onItemClick(int pos) {
        if(pos<0){
            return;
        }
        mAreaDto = mListType.get(pos);

        tv_areacode.setText(mAreaDto.getPhoneCode());

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        custAreaCodePopView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        setBackgroundAlpha(0.5f);
        return false;
    }
    //设置屏幕背景透明效果
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().setAttributes(lp);
    }
}
