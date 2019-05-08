package com.bisa.health.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.adapter.CameraFilesLvAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraFilesActivity extends BaseActivity {
    private ListView lvFiles;
    private CameraFilesLvAdapter adapter;
    private List<File> fileList = new ArrayList<>();

    private String fileDir;
    private File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files);

        fileDir = getIntent().getStringExtra("fileDir");

        lvFiles = findViewById(R.id.lv_camera_files);
        adapter = new CameraFilesLvAdapter(this, fileList);
        lvFiles.setAdapter(adapter);

        dir = new File(fileDir);


        refreshFiles();

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = adapter.getItem(position);
                if(path.endsWith("jpg")) {
                    Intent intent = new Intent(CameraFilesActivity.this, CameraViewImageActivity.class);
                    intent.putExtra("filePath", path);
                    startActivity(intent);
                }
                else if(path.endsWith("mp4")) {
                    Intent intent = new Intent(CameraFilesActivity.this, CameraViewVideoActivity.class);
                    intent.putExtra("filePath", path);
                    startActivity(intent);
                }
            }
        });

    }

    private void refreshFiles() {
        File[] files = dir.listFiles();
        fileList.clear();
        for(File file:files) {
            fileList.add(file);
        }

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                long diff = o1.lastModified() - o2.lastModified();
                if (diff > 0) {
                    return -1;
                }
                else if (diff < 0) {
                    return 1;
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        refreshFiles();
        super.onResume();
    }
}
