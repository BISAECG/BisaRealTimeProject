package com.bisa.health.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

public class CameraAddActivity extends BaseActivity {
    private Button btnAdd, btnFromShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_add);

        btnAdd = findViewById(R.id.btn_camera_add);
        btnFromShare = findViewById(R.id.btn_camera_add_from_share);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivity(CameraAddActivity.this, CameraGuideActivity.class, ActionEnum.NULL);
            }
        });

        btnFromShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivity(CameraAddActivity.this, CameraAddFromSnActivity.class, ActionEnum.NULL);
            }
        });
    }
}
