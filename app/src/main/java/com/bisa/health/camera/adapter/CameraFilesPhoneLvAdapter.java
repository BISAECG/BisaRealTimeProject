package com.bisa.health.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFilesPhoneLvAdapter extends BaseAdapter {
    private Context context;
    private List<File> fileList;
    private String country;

    public CameraFilesPhoneLvAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
        country = context.getResources().getConfiguration().locale.getCountry();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_files_phone, null);
            vh = new VH(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (VH)convertView.getTag();
        }
        File file = fileList.get(position);
        Glide.with(context).load(file).thumbnail(0.1f).into(vh.ivThumbnail);
        if(file.getName().endsWith("jpg")) {
            vh.ivIcon.setImageResource(R.drawable.icon_camera_file_capture);
        }
        else {
            vh.ivIcon.setImageResource(R.drawable.icon_camera_file_record);
        }
        Date date = new Date(file.lastModified());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfDate;
        if(country.equals("CN") || country.equals("HK") || country.equals("TW")) {
            sdfDate = new SimpleDateFormat("MM" + context.getString(R.string.month) + "dd" + context.getString(R.string.day), Locale.getDefault());
        }
        else {
            sdfDate = new SimpleDateFormat("MM/dd", Locale.getDefault());
        }
        vh.tvTime.setText(sdfTime.format(date));
        vh.tvDate.setText(sdfDate.format(date));
        return convertView;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public String getItem(int position) {
        return fileList.get(position).getAbsolutePath();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class VH {
        ImageView ivThumbnail;
        TextView tvTime;
        ImageView ivIcon;
        TextView tvDate;
        VH(View v) {
            ivThumbnail = v.findViewById(R.id.iv_camera_files_thumbnail);
            tvTime = v.findViewById(R.id.tv_camera_files_time);
            ivIcon = v.findViewById(R.id.iv_camera_files_typeicon);
            tvDate = v.findViewById(R.id.tv_camera_files_date);
        }
    }
}
