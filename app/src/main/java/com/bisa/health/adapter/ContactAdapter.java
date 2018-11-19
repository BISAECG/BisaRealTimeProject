package com.bisa.health.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.model.Event;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter {

	private List<Event> listEvent;
	private LayoutInflater mInflater;

	public ContactAdapter(Activity par) {
		super();
		mInflater = par.getLayoutInflater();
		listEvent=new ArrayList<Event>();
	}

	public List<Event> getListEvent() {
		return listEvent;
	}

	public void setListEvent(List<Event> listEvent) {
		this.listEvent = listEvent;
	}

	public void clearList() {
		listEvent.clear();
	}
	public Event getEvent(int position){
		return listEvent.get(position);
	}

	@Override
	public int getCount() {
		return listEvent.size();
	}

	@Override
	public Object getItem(int position) {
		return getEvent(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get already available view or create new if necessary
		FieldReferences fields;
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.contact_list, null);
        	fields = new FieldReferences();

        	fields.tvContactName = (TextView) convertView.findViewById(R.id.txt_contactname);
			fields.tvContactNumnber = (TextView) convertView.findViewById(R.id.txt_contactnumber);
			fields.txtContactEmail = (TextView) convertView.findViewById(R.id.txt_contactemail);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }			
		
        // set proper values into the view
        Event event= listEvent.get(position);
        if(event!=null)
        	fields.tvContactName.setText(event.getEvent_name());
		if(!StringUtils.isEmpty(event.getEvent_num()))
			fields.tvContactNumnber.setText(event.getEvent_num());
		if(!StringUtils.isEmpty(event.getEvent_mail()))
			fields.txtContactEmail.setText(event.getEvent_mail());
		return convertView;
	}
	
	private class FieldReferences {
		TextView tvContactName;
		TextView tvContactNumnber;
		TextView txtContactEmail;
	}
}
