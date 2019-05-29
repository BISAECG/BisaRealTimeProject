package com.bisa.health.camera;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bisa.health.R;

import java.io.File;

public class CameraViewVideoActivity extends Activity {
    private VideoView videoView;

    private String path;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_video);

        videoView = findViewById(R.id.videoView_camera_view_video);

        ActionBar actionBar = getActionBar();

        path = getIntent().getStringExtra("filePath");

        String[] strs = path.split("/");
        actionBar.setTitle(strs[strs.length-1]);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        file = new File(path);

        //创建MediaController对象
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);

        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(path);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mediaController.show(0);
            }
        });

        //videoView.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = new MenuInflater(this);

        MenuInflater inflater = getMenuInflater();//另一种方法
        inflater.inflate(R.menu.camera_file, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.camera_file_menu_save:
                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.TITLE, file.getName());
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                contentValues.put(MediaStore.Video.VideoColumns.DATE_TAKEN, file.lastModified());
                contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, file.lastModified());
                contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                contentValues.put(MediaStore.MediaColumns.SIZE, file.length());
                Uri uri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                if(uri != null) {
                    Toast.makeText(this, getString(R.string.camera_file_save_success), Toast.LENGTH_LONG).show();
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                break;
            case R.id.camera_file_menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                Uri fileUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    fileUri = Uri.fromFile(file);
                }
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                intent.setType("video/*");
                startActivity(Intent.createChooser(intent, getString(R.string.xixin_camera)));
                break;
            case R.id.camera_file_menu_delete:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.camera_file_delete_tips)
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean result = file.delete();
                                if(result) {
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.common_cancel, null)
                        .show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
