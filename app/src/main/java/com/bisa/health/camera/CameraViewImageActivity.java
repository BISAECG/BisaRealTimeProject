package com.bisa.health.camera;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bisa.health.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class CameraViewImageActivity extends Activity {

    private PhotoView photoView;


    private String path;
    private File imageFile;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view_image);

        photoView = findViewById(R.id.photo_view);

        ActionBar actionBar = getActionBar();

        path = getIntent().getStringExtra("filePath");
        String[] strs = path.split("/");

        actionBar.setTitle(strs[strs.length-1]);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imageFile = new File(path);
        uri = Uri.fromFile(imageFile);
        photoView.setImageURI(uri);

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
                MediaStore.Images.Media.insertImage(getContentResolver(), BitmapFactory.decodeFile(path), imageFile.getName(), getString(R.string.xixin_camera));
                Toast.makeText(this, getString(R.string.camera_file_save_success), Toast.LENGTH_LONG).show();
                break;
            case R.id.camera_file_menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, uri);
                startActivity(Intent.createChooser(intent, getString(R.string.xixin_camera)));
                break;
            case R.id.camera_file_menu_delete:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.camera_file_delete_tips)
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean result = imageFile.delete();
                                if(result) {
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
