package com.bisa.health.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.config.OPCompressPic;
import com.bisa.health.camera.lib.funsdk.support.models.FunFileData;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class CameraFilesCameraRecordLvAdapter extends BaseAdapter {

    private Context mContext;
    private List<FunFileData> fileList = new ArrayList<>();
    private int mPlayingIndex = -1;

    public CameraFilesCameraRecordLvAdapter(Context context, List<H264_DVR_FILE_DATA> files) {
        mContext = context;
        for(H264_DVR_FILE_DATA file : files) {
            fileList.add(new FunFileData(file, new OPCompressPic()));
        }
    }


    public void setPlayingIndex(int index) {
    	mPlayingIndex = index;
    	notifyDataSetChanged();
    }
    public void setBitmapTempPath(String path){
        fileList.get(mPlayingIndex).setCapTempPath(path);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public H264_DVR_FILE_DATA getItem(int position) {
        return fileList.get(position).getFileData();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_camera_files_camera_record, parent, false);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FunFileData data = fileList.get(position);

        String str = data.getBeginTimeStr() + " - " + data.getEndTimeStr();
        viewHolder.tvRecordTime.setText(str);
        
        // 当前正在播放的高亮显示
        if ( mPlayingIndex == position ) {
        	viewHolder.tvRecordTime.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
        	viewHolder.tvRecordTime.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        if(data.getCapTempPath() != null) {
            Glide.with(mContext).load(data.getCapTempPath()).thumbnail(0.1f).into(viewHolder.ivRecordShot);
        }
        else {
            viewHolder.ivRecordShot.setImageResource(R.drawable.icon_media);
        }

        return convertView;
    }


    class ViewHolder {
        ImageView ivRecordShot;
        TextView tvRecordTime;

        ViewHolder(View v) {
            ivRecordShot = v.findViewById(R.id.iv_item_camera_files_camera_record_shot);
            tvRecordTime = v.findViewById(R.id.tv_item_camera_files_camera_record_time);
        }

    }
}
