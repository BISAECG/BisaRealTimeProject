package com.bisa.health.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

public class CameraGuideActivity extends BaseActivity {
    private CheckBox checkBox;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_guide);

        checkBox = findViewById(R.id.checkBox_camera_guide);
        btnNext = findViewById(R.id.btn_camera_guide);

        btnNext.setEnabled(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btnNext.setEnabled(true);
                }
                else {
                    btnNext.setEnabled(false);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.startActivity(CameraGuideActivity.this, CameraWifiActivity.class, ActionEnum.NULL);
            }
        });
    }
}
