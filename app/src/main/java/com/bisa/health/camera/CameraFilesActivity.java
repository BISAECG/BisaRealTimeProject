package com.bisa.health.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.adapter.CameraFilesLvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.config.OPCompressPic;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.funsdk.support.models.FunFileData;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.lib.SDKCONST;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CameraFilesActivity extends BaseActivity {
    private ListView lvFiles;
    private CameraFilesLvAdapter adapter;
    private List<File> fileList = new ArrayList<>();

    private String fileDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files);

        fileDir = getIntent().getStringExtra("fileDir");

        lvFiles = findViewById(R.id.lv_camera_files);
        adapter = new CameraFilesLvAdapter(this, fileList);
        lvFiles.setAdapter(adapter);

        File file = new File(fileDir);
        File[] files = file.listFiles();

        for(File file1:files) {
            fileList.add(0, file1);
        }

        adapter.notifyDataSetChanged();

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = adapter.getItem(position);
                if(path.endsWith("jpg")) {
                    Intent intent = new Intent(CameraFilesActivity.this, CameraViewImageActivity.class);
                    intent.putExtra("filePath", path);
                    startActivity(intent);
                }
            }
        });

    }


}
