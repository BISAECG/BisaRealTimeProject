package com.bisa.health.camera;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.adapter.CameraAllRvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.cust.view.CustomDefaultDialog;
import com.bisa.health.dao.DeviceDao;
import com.bisa.health.model.User;
import com.bisa.health.provider.device.DeviceCursor;
import com.bisa.health.provider.device.DeviceSelection;

import org.json.JSONException;
import org.json.JSONObject;

public class CameraAllActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ImageButton ibtnBack;
    private ImageButton ibtnTopDel;
    private RecyclerView rvCameraAll;
    private CameraAllRvAdapter adapter;

    private LinearLayout llayoutDelBottom;
    private Button btnDelCancel;
    private Button btnDel;

    private SharedPersistor sharedPersistor;
    private User mUser;
    private int user_guid;

    private DeviceDao deviceDao;
    private SharedPreferences sharedPref;

    private OnFunDeviceListener onFunDeviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_all);

        CameraSdkInit.init(this);

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());

        user_guid = mUser.getUser_guid();

        ibtnBack = findViewById(R.id.ibtn_camera_all_back);
        ibtnTopDel = findViewById(R.id.ibtn_camera_all_topDel);

        llayoutDelBottom = findViewById(R.id.llayout_camera_all_delBottom);
        btnDelCancel = findViewById(R.id.btn_camera_all_delCancel);
        btnDel = findViewById(R.id.btn_camera_all_del);

        rvCameraAll = findViewById(R.id.rv_camera_all);
        rvCameraAll.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAllRvAdapter(this, user_guid);
        rvCameraAll.setAdapter(adapter);

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibtnTopDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ibtnTopDel.isSelected()) {
                    ibtnTopDel.setSelected(false);
                    adapter.setDelMode(false);
                    llayoutDelBottom.setVisibility(View.GONE);
                }
                else {
                    ibtnTopDel.setSelected(true);
                    adapter.setDelMode(true);
                    llayoutDelBottom.setVisibility(View.VISIBLE);
                }

            }
        });

        sharedPref = getSharedPreferences(String.valueOf(user_guid), Context.MODE_PRIVATE);

        deviceDao = new DeviceDao(this);

        getSupportLoaderManager().initLoader(0, null, this);

        onFunDeviceListener = new OnFunDeviceListener() {
            @Override
            public void onDeviceListChanged() {

            }

            @Override
            public void onDeviceStatusChanged(FunDevice funDevice) {
                if(adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDeviceAddedSuccess() {

            }

            @Override
            public void onDeviceAddedFailed(Integer errCode) {

            }

            @Override
            public void onDeviceRemovedSuccess() {

            }

            @Override
            public void onDeviceRemovedFailed(Integer errCode) {

            }

            @Override
            public void onAPDeviceListChanged() {

            }

            @Override
            public void onLanDeviceListChanged() {

            }
        };

        FunSupport.getInstance().registerOnFunDeviceListener(onFunDeviceListener);


        btnDelCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibtnTopDel.setSelected(false);
                adapter.setDelMode(false);
                llayoutDelBottom.setVisibility(View.GONE);
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDefaultDialog.Builder(CameraAllActivity.this)
                        .setIco(getResources().getDrawable(R.drawable.warning_ico))
                        .setMessage(getResources().getString(R.string.commit_del))
                        .setTitle(getResources().getString(R.string.title_del_device))
                        .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                SharedPreferences.Editor editor = sharedPref.edit();
                                for(String sn : adapter.getDelSnList()) {
                                    FunSupport.getInstance().deviceListDelDevice(sn);
                                    deviceDao.deleteByUserSn(mUser.getUser_guid(), sn);
                                    editor.remove(sn);
                                    editor.apply();
                                }
                                ibtnTopDel.callOnClick();
                            }
                        }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();

            }
        });

        //Android6.0 以上动态申请权限 当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int j = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            // 权限是否已经授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED) {
                //没有权限，向用户请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DeviceSelection where = new DeviceSelection();
        where.userGuid(mUser.getUser_guid());
        return  where.getCursorLoader(this,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        FunSupport.getInstance().deviceListClear();

        if(data!=null && data.moveToFirst()){
            DeviceCursor deviceCursor = new DeviceCursor(data);
            String devSn;
            do{
                FunDevice funDevice = new FunDevice();
                devSn = deviceCursor.getDevnum();
                String devJsonStr = sharedPref.getString(devSn, "");
                try {
                    JSONObject jsonObject = new JSONObject(devJsonStr);
                    funDevice.initWith(jsonObject);
                    FunSupport.getInstance().deviceListAdd(funDevice);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }while(deviceCursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        FunSupport.getInstance().requestAllDeviceStatus();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onBackPressed() {
        if(ibtnTopDel.isSelected()) {
            btnDelCancel.callOnClick();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceListener(onFunDeviceListener);
        super.onDestroy();
    }
}
