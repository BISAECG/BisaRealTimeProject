package com.bisa.health.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bisa.health.R;

public class CameraViewVideoActivity extends Activity {
    private ImageButton ibtnBack;
    private TextView tvTitle;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_video);

        ibtnBack = findViewById(R.id.ibtn_camera_view_video_back);
        tvTitle = findViewById(R.id.tv_camera_view_video_title);
        videoView = findViewById(R.id.videoView_camera_view_video);

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String path = getIntent().getStringExtra("filePath");

        String[] strs = path.split("/");
        tvTitle.setText(strs[strs.length-1]);

        videoView.setVideoPath(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        videoView.start();

    }

}
