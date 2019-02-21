package com.bisa.health.cust.view;

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
import com.bisa.health.adapter.CountryAdapter;
import com.bisa.health.R;
import com.bisa.health.adapter.IAdapterClickInterFace;

public class CustCountrySpinerPopWindow extends PopupWindow implements OnItemClickListener {

	private ListView mListView;
	private CountryAdapter mAdapter;
	private IAdapterClickInterFace iOnItemSelectListener;

	public CustCountrySpinerPopWindow(Context context) {
		super(context);
		init(context);
	}

	public CustCountrySpinerPopWindow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustCountrySpinerPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
    public void setItemListener( IAdapterClickInterFace listener){
		iOnItemSelectListener = listener;
    }  


	public void setAdatper(CountryAdapter adapter){
    	mAdapter = adapter;  
        mListView.setAdapter(mAdapter);  
    }  
	

	public void setDeviceListAdapter(CountryAdapter countryAdapter) {
		mListView.setAdapter(countryAdapter);
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
	        if (iOnItemSelectListener != null){
				iOnItemSelectListener.onItemClick(position);
	        } 

	}

}
