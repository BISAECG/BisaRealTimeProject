package com.bisa.health.cust;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bisa.health.R;
import com.bisa.health.adapter.IAdapterClickInterFace;

/**
 * Created by Administrator on 2017/7/27.
 */

public class CustAreaCodePopView extends PopupWindow implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private ListAdapter mAdapter;
    private IAdapterClickInterFace mItemSelectListener;
    private Context context;

    public CustAreaCodePopView(Context context) {
        super(context);
        init(context);
    }

    public CustAreaCodePopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustAreaCodePopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setItemListener(IAdapterClickInterFace listener){
        mItemSelectListener = listener;
    }


    public void setAdatper(ListAdapter adapter){
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }


    public void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.spiner_list_area_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xff);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.PopupWindow);
        mListView = (ListView) view.findViewById(R.id.list_spiner);
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismiss();
        if (mItemSelectListener != null){
            mItemSelectListener.onItemClick(position);
        }
    }

}
