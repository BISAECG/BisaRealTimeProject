package com.bisa.health.camera.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.CameraDeviceActivity;
import com.bisa.health.camera.CameraSettingsActivity;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevStatus;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraAllRvAdapter extends RecyclerView.Adapter<CameraAllRvAdapter.VH> {
    private Context mContext;
    private List<FunDevice> cameraList;
    private int user_guid;

    private boolean isDelMode;
    private List<String> delSnList = new ArrayList<>();

    private final String shareStr;

    public CameraAllRvAdapter(Context context, int user_guid) {
        mContext = context;
        cameraList = FunSupport.getInstance().getDeviceList();
        this.user_guid = user_guid;
        isDelMode = false;

        shareStr = mContext.getString(R.string.camera_share_content_title) + "\nhttps://www.bisahealth.com/mi/call/camera/share?deviceid=";
    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_camera_all_rv, viewGroup, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH viewHolder, int position) {
        FunDevice camera = cameraList.get(position);
        File file = new File(mContext.getCacheDir().getAbsolutePath() + user_guid + camera.getDevSn() + "last.jpg");
        if(file.exists()) {
            viewHolder.ivBg.setBackground(Drawable.createFromPath(file.getAbsolutePath()));
        }
        else {
            viewHolder.ivBg.setBackgroundResource(R.drawable.bg_camera_all_default);
        }
        viewHolder.tvName.setText(camera.devName);
        viewHolder.tvOnlineStatus.setText(camera.devStatus.getStatusResId());
        if (camera.devStatus == FunDevStatus.STATUS_ONLINE || camera.devStatus == FunDevStatus.STATUS_UNKNOWN) {
            viewHolder.tvOnlineStatus.setBackgroundResource(R.drawable.bg_tv_camera_online);
        } else {
            viewHolder.tvOnlineStatus.setBackgroundResource(R.drawable.bg_tv_camera_offline);
        }

        if(isDelMode) {
            viewHolder.checkBoxDel.setVisibility(View.VISIBLE);
            viewHolder.checkBoxDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        delSnList.add(camera.getDevSn());
                    }
                    else {
                        delSnList.remove(camera.getDevSn());
                    }
                }
            });
            viewHolder.ivBg.setOnClickListener(null);
            viewHolder.btnShare.setOnClickListener(null);
            viewHolder.btnSettings.setOnClickListener(null);
        }
        else {
            viewHolder.checkBoxDel.setOnCheckedChangeListener(null);
            viewHolder.checkBoxDel.setChecked(false);
            viewHolder.checkBoxDel.setVisibility(View.GONE);
            viewHolder.ivBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FunSupport.getInstance().mCurrDevice = camera;
                    Intent intent = new Intent(mContext, CameraDeviceActivity.class);
                    intent.putExtra("position", position);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
            viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareStr + camera.getDevSn());
                    mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.xixin_camera)));
                }
            });
            viewHolder.btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FunSupport.getInstance().mCurrDevice = camera;
                    Intent intent = new Intent(mContext, CameraSettingsActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cameraList.size();
    }

    public List<String> getDelSnList() {
        return delSnList;
    }

    public void setDelMode(boolean isDelMode) {
        this.isDelMode = isDelMode;
        if(!isDelMode) {
            delSnList.clear();
        }
        notifyDataSetChanged();

    }

    class VH extends RecyclerView.ViewHolder {
        ImageView ivBg;
        TextView tvName;
        TextView tvOnlineStatus;
        Button btnShare, btnSettings;
        CheckBox checkBoxDel;

        VH(View v) {
            super(v);
            ivBg = v.findViewById(R.id.iv_item_camera_all_bg);
            tvName = v.findViewById(R.id.tv_item_camera_all_name);
            tvOnlineStatus = v.findViewById(R.id.tv_item_camera_all_status);
            btnShare = v.findViewById(R.id.btn_item_camera_all_share);
            btnSettings = v.findViewById(R.id.btn_item_camera_all_settings);
            checkBoxDel = v.findViewById(R.id.checkbox_item_camera_all_del);
        }
    }
}
