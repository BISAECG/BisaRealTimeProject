package com.bisa.health.camera;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.adapter.CameraAllRvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.model.User;
import com.bisa.health.provider.device.DeviceCursor;
import com.bisa.health.provider.device.DeviceSelection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CameraAllActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView rvCameraAll;
    private CameraAllRvAdapter adapter;
    private ArrayList<FunDevice> cameraList = new ArrayList<>();

    private SharedPersistor sharedPersistor;
    private User mUser;
    private int user_guid;

    private SharedPreferences sharedPref;

    private OnFunDeviceListener onFunDeviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        CameraSdkInit.init(this);

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());

        user_guid = mUser.getUser_guid();

        rvCameraAll = findViewById(R.id.rv_camera_all);
        rvCameraAll.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAllRvAdapter(this, user_guid);
        rvCameraAll.setAdapter(adapter);


        sharedPref = getSharedPreferences(String.valueOf(mUser.getUser_guid()), Context.MODE_PRIVATE);

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
                    FunSupport.getInstance().requestDeviceStatus(funDevice);
                    FunSupport.getInstance().deviceListAdd(funDevice);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }while(deviceCursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        //FunSupport.getInstance().requestAllDeviceStatus();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceListener(onFunDeviceListener);
        super.onDestroy();
    }
}
