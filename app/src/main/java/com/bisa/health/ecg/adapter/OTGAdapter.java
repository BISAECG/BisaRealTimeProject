package com.bisa.health.ecg.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.ecg.model.OTGECGDto;
import com.bisa.health.utils.LvHeightUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class OTGAdapter extends BaseAdapter {

    private static final String TAG = "OTGAdapter";
    private int mPos=-1;
    private  List<OTGECGDto> mapList;
    private LayoutInflater mInflater;
    private Activity parent;
    private OTGAdapterFile otgAdapterFile;
    private IOnItemPosSelectListener iOnListener;
    private OTGAdapterFile.IOnItemPosChindSelectListener chindPosListener;

    public static interface IOnItemPosSelectListener{
        public void onItemPosListener(int pos);
    };

    public OTGAdapter(Activity parent,IOnItemPosSelectListener iOnListener,OTGAdapterFile.IOnItemPosChindSelectListener chindPosListener) {
        super();
        mapList = new ArrayList<OTGECGDto>();
        mInflater = parent.getLayoutInflater();
        this.parent = parent;
        this.iOnListener=iOnListener;
        this.chindPosListener=chindPosListener;
    }

    public IOnItemPosSelectListener getiOnListener() {
        return iOnListener;
    }

    public void setiOnListener(IOnItemPosSelectListener iOnListener) {
        this.iOnListener = iOnListener;
    }

    public List<OTGECGDto> getMapList() {
        return mapList;
    }

    public void setMapList(List<OTGECGDto> mapList) {
        this.mapList = mapList;
    }


    public void setUpdateView(int mPos){
        OTGECGDto otgecgDto=mapList.get(mPos);
        otgecgDto.setShow(false);
        for(int i=0;i<otgecgDto.getList().size();i++){
            otgecgDto.getList().get(i).setChecked(false);
        }
        mapList.remove(mPos);
        mapList.add(mPos,otgecgDto);
        OTGAdapterFile.chindPos=-1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ecdfile_list, null);
            fields = new FieldReferences();
            fields.rlTitle = (RelativeLayout) convertView.findViewById(R.id.rl_file_name);
            fields.iv_arrow_open = (ImageView) convertView.findViewById(R.id.iv_arrow_open);
            fields.listView = (ListView) convertView.findViewById(R.id.list_dayecd);
            fields.tv_title=(TextView) convertView.findViewById(R.id.txt_contactname);


            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }

        final OTGECGDto mOtgEcgDto=mapList.get(position);

        Log.i(TAG, "isShow: "+mOtgEcgDto.isShow());

        otgAdapterFile =new OTGAdapterFile(this.parent,mOtgEcgDto.getList(),this.chindPosListener);
        fields.listView.setAdapter(otgAdapterFile);
        LvHeightUtil.setListViewHeightBasedOnChildren(fields.listView);
        fields.tv_title.setText(mOtgEcgDto.getTitle());
        fields.rlTitle.setTag(position);
        fields.rlTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(mOtgEcgDto.isShow()){
                    mOtgEcgDto.setShow(false);
                }else{
                    mOtgEcgDto.setShow(true);
                }
                iOnListener.onItemPosListener(position);
                notifyDataSetChanged();
            }

        });

        if(mOtgEcgDto.isShow()){
            fields.iv_arrow_open.setImageDrawable(this.parent.getResources().getDrawable(R.drawable.arrow_close));
            fields.listView.setVisibility(View.VISIBLE);
        }else{
            fields.iv_arrow_open.setImageDrawable(this.parent.getResources().getDrawable(R.drawable.arrow_open));
            fields.listView.setVisibility(View.GONE);
        }

        return convertView;
    }


    private class FieldReferences {
        RelativeLayout rlTitle;
        ImageView iv_arrow_open;
        ListView listView;
        TextView tv_title;


    }
}
