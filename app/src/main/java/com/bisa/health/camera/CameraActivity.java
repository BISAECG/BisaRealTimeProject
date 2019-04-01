package com.bisa.health.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.camera.adapter.CameraAllRvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.model.User;
import com.bisa.health.provider.device.DeviceCursor;
import com.bisa.health.provider.device.DeviceSelection;

import java.util.ArrayList;

public class CameraActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView rvCameraAll;
    private CameraAllRvAdapter adapter;
    private ArrayList<FunDevice> cameraList = new ArrayList<>();

    private SharedPersistor sharedPersistor;
    private User mUser;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        rvCameraAll = findViewById(R.id.rv_camera_all);
        rvCameraAll.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CameraAllRvAdapter(this, cameraList);
        rvCameraAll.setAdapter(adapter);

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());

        sharedPref = getSharedPreferences("camera", Context.MODE_PRIVATE);

        getSupportLoaderManager().initLoader(0, null, this);

        CameraSdkInit.init(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DeviceSelection where = new DeviceSelection();
        where.userGuid(mUser.getUser_guid());
        return  where.getCursorLoader(this,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cameraList.clear();

        if(data!=null && data.moveToFirst()){
            DeviceCursor deviceCursor = new DeviceCursor(data);
            do{
                FunDevice funDevice = new FunDevice();
                funDevice.devSn = deviceCursor.getDevnum();
                funDevice.devName = deviceCursor.getCustName();
                cameraList.add(funDevice);
            }while(deviceCursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
