package com.bisa.health.camera;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.github.chrisbanes.photoview.PhotoView;

public class CameraViewImageActivity extends BaseActivity {
    private ImageButton ibtnBack;
    private TextView tvTitle;
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_image);

        ibtnBack = findViewById(R.id.ibtn_camera_view_image_back);
        tvTitle = findViewById(R.id.tv_camera_view_image_title);
        photoView = findViewById(R.id.photo_view);

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String path = getIntent().getStringExtra("filePath");
        String[] strs = path.split("/");
        tvTitle.setText(strs[strs.length-1]);
        photoView.setImageDrawable(Drawable.createFromPath(path));
    }



}
