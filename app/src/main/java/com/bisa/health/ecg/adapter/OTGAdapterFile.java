package com.bisa.health.ecg.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustomDefaultDialog;
import com.bisa.health.ecg.model.OTGECGFileDto;
import com.bisa.health.model.User;
import com.bisa.health.provider.upload.UploadCursor;
import com.bisa.health.provider.upload.UploadSelection;

import java.util.List;

/**
 * Created by Administrator on 2018/5/10.
 */

public class OTGAdapterFile  extends BaseAdapter {

    private static final String TAG = "OTGAdapterFile";


    private List<OTGECGFileDto> mList;
    private LayoutInflater mInflater;
    private Activity self;
    public static int chindPos=-1;
    private IOnItemPosChindSelectListener itemPosChindSelectListener;
    private SharedPersistor sharedPersistor;
    private User mUser=null;
    public static interface IOnItemPosChindSelectListener{
        public void onItemPosChindListener(int pos);
    };

    public OTGAdapterFile(Activity parent,List<OTGECGFileDto> list,IOnItemPosChindSelectListener itemPosChindSelectListener) {
        super();
        this.mList = list;
        mInflater = parent.getLayoutInflater();
        this.self = parent;
        this.itemPosChindSelectListener=itemPosChindSelectListener;
        this.sharedPersistor=new SharedPersistor(parent);
        this.mUser=sharedPersistor.loadObject(User.class.getName());
    }

    public List<OTGECGFileDto> getmList() {
        return mList;
    }

    public void setmList(List<OTGECGFileDto> mList) {
        this.mList = mList;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FieldReferences fields;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ecdfile_listecd, null);
            fields = new FieldReferences();
            fields.rl_file_name = (RelativeLayout) convertView.findViewById(R.id.rl_file_name);
            fields.iv_select=(ImageView) convertView.findViewById(R.id.iv_select);
            fields.txt_filename=(TextView) convertView.findViewById(R.id.txt_filename);
            convertView.setTag(fields);
        } else {
            fields = (FieldReferences) convertView.getTag();
        }


        final OTGECGFileDto otgecgFileDto=mList.get(position);

        if(otgecgFileDto.isChecked()){
            fields.iv_select.setTag("1");
            fields.iv_select.setImageDrawable(this.self.getResources().getDrawable(R.drawable.ecdchecken));
        }else{
            fields.iv_select.setTag("0");
            fields.iv_select.setImageDrawable(this.self.getResources().getDrawable(R.drawable.ecdcheck));
        }

        fields.txt_filename.setText(otgecgFileDto.getTitle());

        fields.rl_file_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(chindPos!=-1&&chindPos!=position){
                    mList.get(chindPos).setChecked(false);
                }

                UploadSelection where=new UploadSelection();
                where.userGuid(mUser.getUser_guid()).and().filename(otgecgFileDto.getFat128Sys().getFilename()).limit(1);
                UploadCursor mCursor= where.query(self);

                if(mCursor.getCount()>0){

                   final  CustomDefaultDialog.Builder dialog=new CustomDefaultDialog.Builder(self)
                            .setIco(self.getResources().getDrawable(R.drawable.report_ico))
                            .setTitle(self.getResources().getString(R.string.titl_report_create))
                            .setMessage(self.getResources().getString(R.string.tip_otg_isupload))
                           .setPositiveButton(self.getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                                   otgecgFileDto.setChecked(true);
                                   itemPosChindSelectListener.onItemPosChindListener(position);
                                   notifyDataSetChanged();
                               }
                           }).setNegativeButton(self.getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                                    otgecgFileDto.setChecked(false);
                                    itemPosChindSelectListener.onItemPosChindListener(-1);
                                   notifyDataSetChanged();
                                }
                           });
                    dialog.create().show();

                }else{
                    if(fields.iv_select.getTag().toString().equals("1")){
                        otgecgFileDto.setChecked(false);
                        itemPosChindSelectListener.onItemPosChindListener(-1);
                    }else{
                        otgecgFileDto.setChecked(true);
                        itemPosChindSelectListener.onItemPosChindListener(position);
                    }
                    notifyDataSetChanged();
                }

                chindPos=position;

            }
        });



        return convertView;
    }



    private class FieldReferences {
        ImageView iv_select;
        TextView txt_filename;
        RelativeLayout rl_file_name;

    }
}
