package com.bisa.health.camera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.widget.PreviewFunVideoView;

import java.util.ArrayList;
import java.util.List;

public class CameraDevicesPreviewGvAdapter extends BaseAdapter {
    private Context mcontext;
    private List<FunDevice> funDeviceList = new ArrayList<>();
    private boolean isHide = false;

    public CameraDevicesPreviewGvAdapter(Context context) {
        mcontext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH vh;
        if(convertView == null) {
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item_camera_devices_preview_gv, null);
            vh = new VH(convertView);
            convertView.setTag(vh);
        }
        else {
            vh = (VH)convertView.getTag();
        }
        playRealMedia(funDeviceList.get(position), vh.previewFunVideoView, position);
        System.out.println("-----------------stopPlayback-----------play");

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return funDeviceList.size();
    }

    class VH {
        PreviewFunVideoView previewFunVideoView;

        VH(View v) {
            previewFunVideoView = v.findViewById(R.id.funVideoView_item_devicesPreview);
        }
    }

    public void setPreviewHide() {
        funDeviceList.clear();
        notifyDataSetChanged();
    }
    public void setPreviewShow() {
        funDeviceList.addAll(FunSupport.getInstance().getDeviceList());
        notifyDataSetChanged();
    }

    private void playRealMedia(FunDevice funDevice, PreviewFunVideoView previewFunVideoView, int channel) {

        if (funDevice.isRemote) {
            previewFunVideoView.setRealDevice(funDevice.getDevSn(), channel);
        } else {
            String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
            previewFunVideoView.setRealDevice(deviceIp, channel);
        }

        // 打开声音
        previewFunVideoView.setMediaSound(true);

    }
}
