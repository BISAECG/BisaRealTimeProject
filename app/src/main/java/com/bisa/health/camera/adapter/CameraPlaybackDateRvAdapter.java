package com.bisa.health.camera.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bisa.health.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraPlaybackDateRvAdapter extends RecyclerView.Adapter<CameraPlaybackDateRvAdapter.VH> {
    private Context context;
    private List<String> dateList;

    public CameraPlaybackDateRvAdapter(Context context, List<String> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_camera_playback_date, null);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int i) {
        if(i == dateList.size()) {
            viewHolder.tvDate.setText("实时");
            viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.text_camera_playback_date_sel));
            viewHolder.tvDate.setBackgroundResource(R.drawable.bg_tv_camera_playback_date_cur);
        }
        else {
            SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfD = new SimpleDateFormat("MM/dd");
            try {
                Date date = sdfS.parse(dateList.get(dateList.size()-1-i));//倒序
                viewHolder.tvDate.setText(sdfD.format(date));
                viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.text_camera_playback_date));
                viewHolder.tvDate.setBackgroundResource(R.drawable.bg_tv_camera_playback_date);
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size() + 1;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvDate;
        VH(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_camera_playback_date);
        }
    }
}
