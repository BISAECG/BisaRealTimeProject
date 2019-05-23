package com.bisa.health.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.models.FunFileData;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.List;

public class CameraFilesCameraLvAdapter extends BaseAdapter {
    private Context context;
    private List<FunFileData> picDataList;

    public CameraFilesCameraLvAdapter(Context context, List<FunFileData> picDataList) {
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

        //if(funDevRecordFileTime != null) {
            //if(position == 0) {
            //vh.ivType.setImageResource(R.drawable.icon_camera_file_record);
            //String str = funDevRecordFileTime.getRecStartTime() + "  -  " + funDevRecordFileTime.getRecEndTime();
            //vh.tvTime.setText(str);
            //}

        vh.ivType.setImageResource(R.drawable.icon_camera_file_capture);
        vh.tvTime.setText(picDataList.get(position).getBeginTimeStr());
        //}

        return convertView;
    }

    @Override
    public int getCount() {
        return picDataList.size();
    }

    @Override
    public FunFileData getItem(int position) {
        return picDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class VH {
        TextView tvTime;
        ImageView ivType;
        VH(View view) {
            tvTime = view.findViewById(R.id.tv_item_camera_files_camera_time);
            ivType = view.findViewById(R.id.iv_item_camera_files_camera_typeicon);
        }
    }
}
