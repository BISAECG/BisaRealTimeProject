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

public class AreaCodeAdapter extends BaseAdapter{

	    private List<ServerDto> mObjects;
	    private LayoutInflater mInflater;

	    public AreaCodeAdapter(Activity parent){
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

	        return mObjects==null?0:mObjects.size();
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
	            convertView = mInflater.inflate(R.layout.spiner_item_area_layout, null);
	            viewHolder = new ViewHolder();
	            viewHolder.areaTextView = (TextView) convertView.findViewById(R.id.tv_spiner_area);
	            convertView.setTag(viewHolder);
	        } else {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        viewHolder.areaTextView.setText(mObjects.get(pos).getCountry());
	        return convertView;
	    }


	    public static class ViewHolder
	    {
	        public TextView areaTextView;
	    }
}
