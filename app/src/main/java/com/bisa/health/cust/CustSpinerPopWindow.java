package com.bisa.health.cust;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bisa.health.R;
import com.bisa.health.adapter.DeviceListAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.adapter.PairDeviceAdapter;
import com.bisa.health.adapter.PairDeviceAdapter.IOnItemSelectListener;

public class CustSpinerPopWindow extends PopupWindow implements OnItemClickListener {

	private ListView mListView;
	private PairDeviceAdapter mAdapter;
	private IOnItemSelectListener mItemSelectListener;
	private IAdapterClickInterFace monItemPairDevListener;
	private DeviceListAdapter mdeviceListAdapter;

	public CustSpinerPopWindow(Context context) {
		super(context);
		init(context);
	}

	public CustSpinerPopWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustSpinerPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
    public void setItemListener(IOnItemSelectListener listener){  
        mItemSelectListener = listener;  
    }  
  
    public void setMonItemPairDevListener(IAdapterClickInterFace monItemPairDevListener) {
		this.monItemPairDevListener = monItemPairDevListener;
	}

	public void setAdatper(PairDeviceAdapter adapter){  
    	mAdapter = adapter;  
        mListView.setAdapter(mAdapter);  
    }  
	

	public void setDeviceListAdapter(DeviceListAdapter deviceListAdapter) {
		mdeviceListAdapter = deviceListAdapter;
		mListView.setAdapter(deviceListAdapter);  
	}

	public void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.spiner_list_layout, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xff);
		setBackgroundDrawable(dw);
		mListView = (ListView) view.findViewById(R.id.list_spiner);
		mListView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 dismiss();  
	        if (mItemSelectListener != null){  
	            mItemSelectListener.onItemClick(position);  
	        } 
	        
	        if(monItemPairDevListener!=null){
	        	monItemPairDevListener.onItemClick(position);
	        }
	}

}
