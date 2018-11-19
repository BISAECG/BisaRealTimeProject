package com.bisa.health;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.CircleImageView;
import com.bisa.health.cust.SelectPicPopupWindow;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.GetImagePathUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;


public class UserInfoActivity extends BaseActivity  implements  OnClickListener{

    //头像
    public CircleImageView img_avatar;
    public RelativeLayout rl_headimg;

    private SelectPicPopupWindow menuWindow;

    private static final int CAMERA_REQUEST_CODE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private static final int SELECT_PIC_NOUGAT = 3;        // 相册选图标记
    private static final int IMAGE_REQUEST_CODE = 4;        // 相册选图标记
    private Button btn_commit;
    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private User mUser;
    private HealthPath mHealthPath;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    public static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private File mTempHeadFile;
    private boolean isWriteStatus = true;

    private static final String TAG = "UserInfoActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_activity);
        Log.i(TAG, "onCreate: ");
        AppManager.getAppManager().addActivity(this);

        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthPath = sharedPersistor.loadObject(HealthPath.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService = new RestServiceImpl(this, mHealthServer);
        img_avatar=this.findViewById(R.id.img_avatar);
        rl_headimg=this.findViewById(R.id.rl_headimg);
        rl_headimg.setOnClickListener(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }



    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // 拍照
                case R.id.takePhotoBtn:
                    File file=new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                    Uri fileUri = null;
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.bisa.health.fileProvider", file);
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    } else {
                        fileUri = Uri.fromFile(file);
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                    }

                    startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:
                    File mGalleryFile=new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                    Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT );
                    pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    // 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                    pickIntent.setType("image/*");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
                        Uri uriForFile = FileProvider.getUriForFile
                                (UserInfoActivity.this, "com.bisa.health.fileProvider", mGalleryFile);
                        pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                        pickIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(pickIntent, SELECT_PIC_NOUGAT);
                    } else {
                        startActivityForResult(pickIntent, IMAGE_REQUEST_CODE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case IMAGE_REQUEST_CODE: {//版本<7.0  图库后返回
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    startPhotoZoom(uri);
                }
                break;
            }
            case SELECT_PIC_NOUGAT://版本>= 7.0
                File imgUri = new File(GetImagePathUtil.getPath(this, data.getData()));
                Uri dataUri = FileProvider.getUriForFile
                        (this, "com.bisa.health.fileProvider", imgUri);
                // Uri dataUri = getImageContentUri(data.getData());
                startPhotoZoom(dataUri);
                break;
            case CAMERA_REQUEST_CODE:// 调用相机拍照

                File file=new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                Uri fileUri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    fileUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.bisa.health.fileProvider", file);
                } else {
                    fileUri = Uri.fromFile(file);
                }

                startPhotoZoom(fileUri);
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                Log.i(TAG, "onActivityResult: ");
                File mCropFile=new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
                Uri inputUri = FileProvider.getUriForFile(this, "com.bisa.health.fileProvider", mCropFile);//通过FileProvider创建一个content类型的Uri
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(inputUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Bitmap bitmap = data.getParcelableExtra("data");
                img_avatar.setImageBitmap(bitmap);
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        File mCropFile=new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
        if (uri == null) {
            Log.e("error","The uri is not exist.");
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri outPutUri = Uri.fromFile(mCropFile);
            intent.setDataAndType(uri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri outPutUri = Uri.fromFile(mCropFile);
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                String url = GetImagePathUtil.getPath(this, uri);//这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(uri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }

        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }


    private void init() {
        Log.i(TAG, "init: "+(img_avatar==null));
        synchronized (this){
            if (!StringUtils.isEmpty(mUser.getUri_pic())) {
                img_avatar.setImageURI(mUser.getUri_pic(),mHealthPath);
            } else {
                img_avatar.setImageDrawable(getResources().getDrawable(R.drawable.userico_avatar));

            }
        }

    }


    @Override
    public void onClick(View v) {
        if(v==rl_headimg){
            menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
            menuWindow.showAtLocation(findViewById(R.id.img_avatar),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        }
    }
}
