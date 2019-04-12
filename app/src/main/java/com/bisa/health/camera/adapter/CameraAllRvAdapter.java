package com.bisa.health.camera.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.CameraDeviceActivity;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;

import java.util.List;

public class CameraAllRvAdapter extends RecyclerView.Adapter<CameraAllRvAdapter.VH> {
    private Context mContext;
    private List<FunDevice> cameraList;

    public CameraAllRvAdapter(Context context) {
        mContext = context;
        cameraList = FunSupport.getInstance().getDeviceList();
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_camera_all_rv, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        viewHolder.tvName.setText(cameraList.get(position).devName);
        viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunSupport.getInstance().mCurrDevice = cameraList.get(position);
                Intent intent = new Intent();
                intent.setClass(mContext, CameraDeviceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView ivBg;
        TextView tvName;
        TextView tvOnlineStatus;
        Button btnShare, btnSettings;

        public VH(View v) {
            super(v);
            ivBg = v.findViewById(R.id.iv_item_camera_all_bg);
            tvName = v.findViewById(R.id.tv_item_camera_all_name);
            tvOnlineStatus = v.findViewById(R.id.tv_item_camera_all_status);
            btnShare = v.findViewById(R.id.btn_item_camera_share);
            btnSettings = v.findViewById(R.id.btn_item_camera_settings);
        }
    }
}
