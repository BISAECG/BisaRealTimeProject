package com.bisa.health.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.model.dto.ServerDto;

import java.util.List;

public class CountryAdapter extends BaseAdapter{


	    private List<ServerDto> mObjects;
	    private LayoutInflater mInflater;

	    public CountryAdapter(Activity parent){
	      //  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	mInflater=parent.getLayoutInflater();
	    }


	    public List<ServerDto> getmObjects() {
			return mObjects;
		}

		public void setmObjects(List<ServerDto> mObjects) {
			this.mObjects = mObjects;
		}


		public void refreshData(List<ServerDto> objects, int selIndex){
	        mObjects = objects;
	        if (selIndex < 0){
	            selIndex = 0;
	        }
	        if (selIndex >= mObjects.size()){
	            selIndex = mObjects.size() - 1;
	        }
	    }


	    @Override
	    public int getCount() {

	        return mObjects.size();
	    }

	    @Override
	    public Object getItem(int pos) {
	        return mObjects.get(pos).toString();
	    }

	    @Override
	    public long getItemId(int pos) {
	        return pos;
	    }

	    @Override
	    public View getView(int pos, View convertView, ViewGroup arg2) {
	        ViewHolder viewHolder;

	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
	            viewHolder = new ViewHolder();
	            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_spiner_txt);
	            convertView.setTag(viewHolder);
	        } else {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        viewHolder.mTextView.setText(mObjects.get(pos).getCountry()+"("+mObjects.get(pos).getPhoneCode()+")");
	        return convertView;
	    }


	    public static class ViewHolder
	    {
	        public TextView mTextView;
	    }
}
