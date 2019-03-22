package com.bisa.health.camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

public class AddCameraActivity extends BaseActivity {
    private Button btnAdd, btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camera);

        btnAdd = findViewById(R.id.btn_camera_add);
        btnShare = findViewById(R.id.btn_camera_share);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivity(AddCameraActivity.this, CameraGuideActivity.class, ActionEnum.NULL);
            }
        });
    }
}
