package com.bisa.health.cust.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.adapter.AreaCodeAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustAreaCodePopView;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.utils.AreaUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/4/13.
 */

public class AreaNameFragment extends Fragment implements View.OnTouchListener,PopupWindow.OnDismissListener,IAdapterClickInterFace{

    private static final String TAG = "AreaNameFragment";
    private SharedPersistor sharedPersistor;
    private View view;
    private AreaCodeAdapter areaCodeAdapter;
    private ServerDto areaDto;
    private List<ServerDto> mListType; // 类型列表
    private CustAreaCodePopView custAreaCodePopView;
    private LinearLayout ll_area_code;
    private TextView tv_areacode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        sharedPersistor=new SharedPersistor(getActivity());
        view = inflater.inflate(R.layout.area_name_fragment, container, false);
        areaCodeAdapter = new AreaCodeAdapter(getActivity());
        custAreaCodePopView = new CustAreaCodePopView(getActivity());
        custAreaCodePopView.setAdatper(areaCodeAdapter);
        custAreaCodePopView.setItemListener(this);
        custAreaCodePopView.setOnDismissListener(this);

        ll_area_code = (LinearLayout) view.findViewById(R.id.ll_area_code);
        ll_area_code.setOnTouchListener(this);
        tv_areacode = (TextView) view.findViewById(R.id.tv_areacode);
        mListType=AreaUtil.getListArea(getActivity());
        areaCodeAdapter.setmObjects(mListType);
        areaCodeAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i(TAG, "onAttach: ");
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
        areaDto = mListType.get(pos);
        tv_areacode.setText(areaDto.getPhoneCode());

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
