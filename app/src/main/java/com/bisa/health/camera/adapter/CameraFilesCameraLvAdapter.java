package com.bisa.health.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.List;

public class CameraFilesCameraLvAdapter extends BaseAdapter {
    private Context context;
    private List<H264_DVR_FILE_DATA> picDataList;

    public CameraFilesCameraLvAdapter(Context context, List<H264_DVR_FILE_DATA> picDataList) {
        this.context = context;
        this.picDataList = picDataList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_files_camera, null);
            vh = new VH(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (VH)convertView.getTag();
        }

        vh.tvTime.setText(picDataList.get(position).getStartTimeOfDay());

        return convertView;
    }

    @Override
    public int getCount() {
        return picDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return picDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class VH {
        TextView tvTime;

        VH(View view) {
            tvTime = view.findViewById(R.id.tv_item_camera_files_camera_time);
        }
    }
}
