package com.bisa.health.cust.fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bisa.health.ProductActivity;
import com.bisa.health.R;
import com.bisa.health.adapter.DeviceAdapter;
import com.bisa.health.cache.CacheManage;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CustomDefaultDialog;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.dao.IDeviceDao;
import com.bisa.health.model.Device;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.provider.device.DeviceCursor;
import com.bisa.health.provider.device.DeviceSelection;
import com.bisa.health.utils.ActivityUtil;

import java.util.ArrayList;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.shape.CircleLightShape;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDeviceFragment extends android.support.v4.app.Fragment implements DeviceAdapter.IOnItemSelectListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {

    
    private  static final String TAG = "MyDeviceFragment";
    private View view;
    private ListView listDeviceView;
    private DeviceAdapter deviceAdapter = null;
    private IDeviceDao deviceDao;
    private ImageButton ibtn_del;
    private Button dell_all_dev;
    private RelativeLayout llDel;
    private SharedPersistor sharedPersistor;
    private Button btn_iknown;
    private static boolean isDel = false;
    private User mUser;

    private CheckBox allSelect;
    private CheckBox ibtn_back;
    public HighLight mHightLight=null;
    private String first=null;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        deviceDao=new DeviceDao(context);
        sharedPersistor=new SharedPersistor(context);
        mUser=sharedPersistor.loadObject(User.class.getName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences setting = getActivity().getSharedPreferences(CacheManage.SHARE_APP_TAG,  Context.MODE_PRIVATE);
        Boolean user_first = setting.getBoolean(this.getClass().getName(),true);
        if(user_first){//第一次
            setting.edit().putBoolean(this.getClass().getName(), false).commit();
            showNextTipViewOnCreated();
        }

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        isDel=false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.i(TAG, "onCreateView: ");

        view=inflater.inflate(R.layout.fragment_my_device, container, false);

        allSelect = (CheckBox) view.findViewById(R.id.checkbox_all);
        allSelect.setOnClickListener(new CheckBoxEventHandler());

        llDel = (RelativeLayout) view.findViewById(R.id.ll_del);
        dell_all_dev=(Button) view.findViewById(R.id.dell_all_dev);
        dell_all_dev.setOnClickListener(new ButtonEventHandler());

        ibtn_back=(CheckBox)view.findViewById(R.id.ibtn_back);
        ibtn_back.setOnCheckedChangeListener(this);

        ibtn_del=(ImageButton) view.findViewById(R.id.ibtn_del);
        ibtn_del.setOnClickListener(this);


        deviceAdapter = new DeviceAdapter(getActivity(),this);
        deviceAdapter.setDel(false);

        listDeviceView = (ListView) view.findViewById(R.id.listview_main);
        listDeviceView.setAdapter(deviceAdapter);
        listDeviceView.setOnItemClickListener(listClickListener);

        return view;
    }

    public void remove(View view)
    {
        mHightLight.remove();
    }
    public  void showNextTipViewOnCreated(){
        mHightLight = new HighLight(getContext())//
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .autoRemove(false)
                .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                    @Override
                    public void onLayouted() {
                        //界面布局完成添加tipview
                        mHightLight.addHighLight(R.id.ibtn_del,R.layout.info_mian_adddevice,new OnBottomPosCallback(20),new CircleLightShape());
                        //然后显示高亮布局
                        //然后显示高亮布局
                        mHightLight.show();

                        View decorLayout = mHightLight.getHightLightView();
                        Button knownView = (Button) decorLayout.findViewById(R.id.btn_iknown);
                        knownView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                remove(null);
                                ActivityUtil.startActivity(getActivity(), ProductActivity.class,false, ActionEnum.NULL);
                            }
                        });
                    }
                })
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        remove(null);
                        ActivityUtil.startActivity(getActivity(), ProductActivity.class,false, ActionEnum.NULL);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v==ibtn_del){
            ActivityUtil.startActivity(getActivity(), ProductActivity.class,false, ActionEnum.NULL);
        }
    }

    private AdapterView.OnItemClickListener listClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < 0 || isDel) {
                return;
            }
            Device bleDev = deviceAdapter.getBleDevice(position);
            bleDev.setCheckbox(0);
            try {
                Class<?> clz = Class.forName(bleDev.getClzName());
                Bundle data=new Bundle();
                data.putParcelable(Device.class.getName(),bleDev);
                ActivityUtil.startActivity(getActivity(),clz,false,data,ActionEnum.NEXT);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    };


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DeviceSelection where = new DeviceSelection();
         where.userGuid(mUser.getUser_guid());
        return  where.getCursorLoader(getActivity(),null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        deviceAdapter.clearList();

        if(data!=null && data.moveToFirst()){
            DeviceCursor deviceCursor = new DeviceCursor(data);
            do{
                Device device = new Device();
                device.setUser_guid(deviceCursor.getUserGuid());
                device.setConnstatus(deviceCursor.getConnstatus());
                device.setClzName(deviceCursor.getClzname());
                device.setCheckbox(deviceCursor.getCheckbox());
                device.setDevnum(deviceCursor.getDevnum());
                device.setDevname(deviceCursor.getDevname());
                device.setIcoflag(deviceCursor.getIcoflag());
                device.setMacadderss(deviceCursor.getMacadderss());
                Log.i(TAG,"onLoadFinished:"+device);
                deviceAdapter.addBleDevice(device);
            }while(deviceCursor.moveToNext());
        }
        deviceAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: check"+isChecked);
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged: gone");
                    llDel.setVisibility(View.VISIBLE);
                    isDel = true;

                } else {
                    Log.i(TAG, "onCheckedChanged: VISIBLE");
                    llDel.setVisibility(View.GONE);
                    isDel = false;
                }
                deviceAdapter.setAllSelect(false);
                allSelect.setChecked(false);
                deviceAdapter.setDel(isDel);
                deviceAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(int selectSize, int maxSize) {

        Log.i(TAG, "onItemClick: "+selectSize+"|"+maxSize);
         if(selectSize==maxSize){
            allSelect.setChecked(true);
        }else{
            allSelect.setChecked(false);
        }


    }


    class ButtonEventHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v == dell_all_dev) {

                final CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(getActivity())
                        .setIco(getResources().getDrawable(R.drawable.warning_ico))
                        .setMessage(getResources().getString(R.string.commit_del))
                    .setTitle(getResources().getString(R.string.title_del_device))
                    .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        ArrayList<Device> mBleDevice = deviceAdapter.getmBleDevice();
                        for (int i = 0; i < mBleDevice.size(); i++) {
                            if (mBleDevice.get(i).getCheckbox()==1) {
                                final  Device _device=mBleDevice.get(i);
                                Log.i(TAG,_device.getDevname());
                                deviceDao.delete(_device.getDevname());
                                deviceAdapter.getmBleDevice().remove(i);
                            }
                        }
                        isDel = false;
                        deviceAdapter.setDel(isDel);
                        llDel.setVisibility(View.GONE);
                        deviceAdapter.notifyDataSetChanged();
                        ibtn_back.setChecked(false);

                    }
                }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();


            }

        }

    }

    /**
     * CHECKBOX处理事件
     *
     * @author Administrator
     */
    class CheckBoxEventHandler implements CompoundButton.OnClickListener {

        @Override
        public void onClick(View v) {
            CheckBox cbView=(CheckBox) v;

            if(cbView.isChecked()){
                deviceAdapter.setAllSelect(true);
            }else{
                deviceAdapter.setAllSelect(false);
            }

            deviceAdapter.notifyDataSetChanged();
        }
    }

}
