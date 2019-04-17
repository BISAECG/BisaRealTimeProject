package com.bisa.health.camera.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.interfaces.OnItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraPlaybackDateRvAdapter extends RecyclerView.Adapter<CameraPlaybackDateRvAdapter.VH> {
    private Context context;
    private List<String> dateList;
    private OnItemClickListener onItemClickListener;

    private int thisPosition;

    public CameraPlaybackDateRvAdapter(Context context, List<String> dateList) {
        this.context = context;
        this.dateList = dateList;
        thisPosition = 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setThisPosition(int thisPosition) {
        this.thisPosition = thisPosition;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_camera_playback_date, null);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int i) {
        SimpleDateFormat sdfS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdfD = new SimpleDateFormat("MM/dd", Locale.getDefault());
        try {
            Date date = sdfS.parse(dateList.get(dateList.size()-1-i));//倒序
            viewHolder.tvDate.setText(sdfD.format(date));
            viewHolder.tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null) {
                        thisPosition = i;
                        onItemClickListener.onItemClick(date);
                        notifyDataSetChanged();
                    }
                }
            });
        }catch (ParseException e) {
            e.printStackTrace();
        }
        if (i == thisPosition) {
            //设置选中颜色
            viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.text_camera_playback_date_sel));
            viewHolder.tvDate.setBackgroundResource(R.drawable.bg_tv_camera_playback_date_sel);
        } else {
            //设置未选中颜色
            viewHolder.tvDate.setTextColor(context.getResources().getColor(R.color.text_camera_playback_date));
            viewHolder.tvDate.setBackgroundResource(R.drawable.bg_tv_camera_playback_date);
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    class VH extends RecyclerView.ViewHolder {
        TextView tvDate;
        VH(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_camera_playback_date);
        }
    }
}
