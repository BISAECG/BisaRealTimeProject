package com.bisa.health.camera.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.bisa.health.camera.CameraSettingsActivity;
import com.bisa.health.camera.lib.funsdk.support.FunPath;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevStatus;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;

import java.io.File;
import java.util.List;

public class CameraAllRvAdapter extends RecyclerView.Adapter<CameraAllRvAdapter.VH> {
    private Context mContext;
    private List<FunDevice> cameraList;
    private int user_guid;

    private final String shareStr = "https://hk-server.bisahealth.com/mi/call/h5/camera/help?lang=zh_CN&key=";

    public CameraAllRvAdapter(Context context, int user_guid) {
        mContext = context;
        cameraList = FunSupport.getInstance().getDeviceList();
        this.user_guid = user_guid;
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_camera_all_rv, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        File file = new File(FunPath.getDefaultPath() + user_guid + "last" + cameraList.get(position).getDevSn() + ".jpg");
        if(file.exists()) {
            viewHolder.ivBg.setBackground(Drawable.createFromPath(file.getAbsolutePath()));
        }
        viewHolder.tvName.setText(cameraList.get(position).devName);
        viewHolder.tvOnlineStatus.setText(cameraList.get(position).devStatus.getStatusResId());
        if (cameraList.get(position).devStatus == FunDevStatus.STATUS_ONLINE || cameraList.get(position).devStatus == FunDevStatus.STATUS_UNKNOWN) {
            viewHolder.tvOnlineStatus.setBackgroundResource(R.drawable.bg_tv_camera_online);
        } else {
            viewHolder.tvOnlineStatus.setBackgroundResource(R.drawable.bg_tv_camera_offline);
        }
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
        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareStr + cameraList.get(position).getDevSn());
                mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.xixin_camera)));
            }
        });
        viewHolder.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunSupport.getInstance().mCurrDevice = cameraList.get(position);
                Intent intent = new Intent(mContext, CameraSettingsActivity.class);
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
