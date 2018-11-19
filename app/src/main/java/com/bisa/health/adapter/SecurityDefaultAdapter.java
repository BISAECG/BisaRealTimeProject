package com.bisa.health.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.model.dto.UserBindDto;
import com.bisa.health.model.enumerate.LoginTypeEnum;

import java.util.ArrayList;
import java.util.List;


public class SecurityDefaultAdapter extends BaseAdapter {

	private  static final String TAG = "DeviceAdapter";

	public List<UserBindDto> getData() {
		return data;
	}

	public void setData(List<UserBindDto> data) {
		this.data = data;
	}

	private List<UserBindDto> data;
	private LayoutInflater mInflater;
	private Activity parent;



	public SecurityDefaultAdapter(Activity parent) {
		super();

		data = new ArrayList<UserBindDto>();
		mInflater = parent.getLayoutInflater();
		this.parent = parent;
	}


	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FieldReferences fields;
		PairDeviceAdapter.ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.default_adapter_tip, null);
			fields = new FieldReferences();
			fields.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
			fields.txt_value = (TextView) convertView.findViewById(R.id.txt_value);

			convertView.setTag(fields);
		} else {
			fields = (FieldReferences) convertView.getTag();
		}

		final UserBindDto userForPwdDto=(UserBindDto)getItem(position);

		if(userForPwdDto!=null){
			fields.txt_value.setText(userForPwdDto.getUsername());
			if(userForPwdDto.getLoginType()== LoginTypeEnum.PHONE){
				fields.txt_name.setText(parent.getResources().getString(R.string.bind_iphone_title));
			}else if(userForPwdDto.getLoginType()== LoginTypeEnum.EMAIL){
				fields.txt_name.setText(parent.getResources().getString(R.string.bind_mail_title));
			}else if(userForPwdDto.getLoginType()== LoginTypeEnum.WECHAT){
				fields.txt_name.setText(parent.getResources().getString(R.string.bind_wx_title));
			}else if(userForPwdDto.getLoginType()== LoginTypeEnum.PWD){
				fields.txt_name.setText(parent.getResources().getString(R.string.bind_pwd_title));
			}

		}


		return convertView;

	}

	private class FieldReferences {
		TextView txt_name;
		TextView txt_value;
	}


}
