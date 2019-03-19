package com.bisa.health;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.CircleImageView;
import com.bisa.health.cust.view.SelectPicPopupWindow;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.model.enumerate.SexTypeEnum;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;
import com.bisa.health.utils.GetImagePathUtil;
import com.bisa.health.utils.GsonUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserInfoActivity extends BaseActivity  implements  OnClickListener{

    //头像
    public CircleImageView img_avatar;
    public RelativeLayout rl_headimg;
    public RelativeLayout rl_name;
    public RelativeLayout rl_sex;

    public RelativeLayout rl_username;
    public RelativeLayout rl_age;

    public TextView tv_name;
    public TextView tv_age;
    public TextView tv_sex;
    public TextView tv_username;

    private SelectPicPopupWindow menuWindow;

    private static final int CAMERA_REQUEST_CODE = 1;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 2;    // 图片裁切标记
    private static final int SELECT_PIC_NOUGAT = 3;        // 相册选图标记
    private static final int IMAGE_REQUEST_CODE = 4;        // 相册选图标记
    public static final int CALL_NAME_CODE = 10;
    public static final int CALL_USERNAME_CODE = 12;
    public static final int CALL_SEX_CODE = 11;
    public static final int CALL_AGE_CODE = 13;
    //需要保存的图片
    private SharedPersistor sharedPersistor;
    private User mUser;
    private HealthPath mHealthPath;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    public static final String IMAGE_FILE_NAME = "avatarImage.jpg";// 头像文件名称
    private static final String TAG = "UserInfoActivity";

    File mCameraFile = new File(Environment.getExternalStorageDirectory(), "IMAGE_FILE_NAME.jpg");//照相机的File对象
    File mCropFile = null;
    File mGalleryFile = new File(Environment.getExternalStorageDirectory(), "IMAGE_GALLERY_NAME.jpg");//相册的File对象


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

        rl_name=this.findViewById(R.id.rl_name);
        rl_name.setOnClickListener(this);
        rl_sex=this.findViewById(R.id.rl_sex);
        rl_sex.setOnClickListener(this);

        rl_age=this.findViewById(R.id.rl_age);
        rl_age.setOnClickListener(this);

        tv_age=this.findViewById(R.id.tv_age);
        tv_sex=this.findViewById(R.id.tv_sex);
        tv_name=this.findViewById(R.id.tv_name);
        tv_username=this.findViewById(R.id.tv_username);

        rl_username=this.findViewById(R.id.rl_username);
        rl_username.setOnClickListener(this);
        getUserInfo();
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
                    Uri fileUri = null;
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.bisa.health.fileProvider", mCameraFile);
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        takeIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        takeIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(mCameraFile));
                    }

                    startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
                    break;
                // 相册选择图片
                case R.id.pickPhotoBtn:

                    Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    pickIntent.setType("image/*");
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

                Uri fileUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.bisa.health.fileProvider", mCameraFile);
                } else {
                    fileUri =Uri.fromFile(mCameraFile);
                }

                startPhotoZoom(fileUri);
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
            case CALL_NAME_CODE:
            case CALL_SEX_CODE:
            case CALL_AGE_CODE:
                if(data==null)return;

                showDialog(true);
                Log.i(TAG, "onActivityResult: ");
                MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if(mCropFile!=null) {
                    RequestBody bodyFile = RequestBody.create(MediaType.parse("image/*"), mCropFile);
                    requestBody.addFormDataPart("head_portrait", mCropFile.getName(), bodyFile);
                }

                String age=data.getStringExtra("age");

                if(!StringUtils.isEmpty(age)){
                    requestBody.addFormDataPart("age", ""+age);
                }

                String nickname=data.getStringExtra("nickname");
                if(!StringUtils.isEmpty(nickname)){
                    requestBody.addFormDataPart("nickname", nickname);
                }

                int mSex=data.getIntExtra("sex",-1);
                SexTypeEnum sexType=SexTypeEnum.getByValue(mSex);
                if(sexType!=null){
                    requestBody.addFormDataPart("sex", "" +sexType);
                }

                mRestService.updateInfo(requestBody.build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(USER_UP_FAIL);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String json = response.body().string();
                        Log.i(TAG, "onResponse: "+json);


                               LoadDiaLogUtil.getInstance().dismiss();
                               ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                               if(result.getCode()==HttpFinal.CODE_200){
                                   Bundle data=new Bundle();
                                   data.putSerializable(User.class.getName(),result.getData());
                                   Message msg=new Message();
                                   msg.setData(data);
                                   msg.what=USER_UP_SUCCESS;
                                   handler.sendMessage(msg);

                               }else{

                                   Message msg=new Message();
                                   msg.obj=result.getMessage();
                                   msg.what=USER_UP_FAIL;
                                   handler.sendEmptyMessage(USER_UP_FAIL);
                               }

                    }
                });
                break;

            case CALL_USERNAME_CODE:
                if(data==null)return;
                String username=data.getStringExtra("username");
                if(!StringUtils.isEmpty(username)){
                    mUser.setUsername(username);
                    tv_username.setText(mUser.getUsername());

                }
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
        if (uri == null) {
            Log.e("error","The uri is not exist.");
            return;
        }
        mCropFile=new File(Environment.getExternalStorageDirectory(), "PHOTO_FILE_NAME.jpg");//裁剪后的File对象
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
        Log.i(TAG, "init: "+mUser);
        synchronized (this){
            if (!StringUtils.isEmpty(mUser.getUri_pic())) {
                img_avatar.setImageURI(mUser.getUri_pic(),mHealthPath);
            } else {
                img_avatar.setImageDrawable(getResources().getDrawable(R.drawable.userico_avatar));

            }

            if(!StringUtils.isEmpty(mUser.getNickname())){
                tv_name.setText(mUser.getNickname());
            }else{
                tv_name.setText(R.string.hint_set);
            }

            if(mUser.getAge()!=0){
                tv_age.setText(""+mUser.getAge());
            }else{
                tv_age.setText(R.string.hint_set);
            }

            if(!StringUtils.isEmpty(mUser.getUsername())){
                tv_username.setText(mUser.getUsername());
            }else{
                tv_username.setText(R.string.hint_set);
            }

            if(mUser.getSex()==null){
                tv_sex.setText(R.string.hint_set);
            }else if(mUser.getSex().getValue()==0){
                tv_sex.setText(R.string.s_sex_male);
            }else if(mUser.getSex().getValue()==1){
                tv_sex.setText(R.string.s_sex_female);
            }
        }

    }


    @Override
    public void onClick(View v) {
        if(v==rl_headimg){
            menuWindow = new SelectPicPopupWindow(this, itemsOnClick);
            menuWindow.showAtLocation(findViewById(R.id.img_avatar),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        }else if(v==rl_name){
            Intent mainIntent = new Intent(this, UserNickNameActivity.class);
            mainIntent.putExtra("nickname",mUser.getNickname());
            ActivityUtil.startActivityResult(mainIntent,CALL_NAME_CODE,this,ActionEnum.NEXT);
        }else if(v==rl_sex){
            Intent mainIntent = new Intent(this, UserSexActivity.class);
            mainIntent.putExtra("sex",mUser.getSex()==null?0:mUser.getSex().getValue());
            ActivityUtil.startActivityResult(mainIntent,CALL_SEX_CODE,this,ActionEnum.NEXT);
        }else if(v==rl_username&&mUser.getVerified()==0){
            Intent mainIntent = new Intent(this, UserNameActivity.class);
            mainIntent.putExtra("username",mUser.getUsername());
            ActivityUtil.startActivityResult(mainIntent,CALL_USERNAME_CODE,this,ActionEnum.NEXT);
        }else if(v==rl_age){
            Intent mainIntent = new Intent(this, UserAgeActivity.class);
            mainIntent.putExtra("age",""+mUser.getAge());
            Log.i(TAG, "onClick: "+mUser.getAge());
            ActivityUtil.startActivityResult(mainIntent,CALL_AGE_CODE,this,ActionEnum.NEXT);
        }


    }

    protected void getUserInfo(){
        showDialog(true);
        Call call=mRestService.getUserInfo();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(USER_UP_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                Log.i(TAG, "onResponse: "+json);
                ResultData<User> result = GsonUtil.getInstance().parse(json,new TypeToken<ResultData<User>>(){}.getType());
                if(result.getCode()==HttpFinal.CODE_200){

                    Bundle data=new Bundle();
                    data.putSerializable(User.class.getName(),result.getData());
                    Message msg=new Message();
                    msg.setData(data);
                    msg.what=USER_UP_SUCCESS;
                    handler.sendMessage(msg);

                }else{

                    Message msg=new Message();
                    msg.obj=result.getMessage();
                    msg.what=USER_UP_FAIL;
                    handler.sendEmptyMessage(USER_UP_FAIL);
                }
            }
        });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dialogDismiss();

            switch (msg.what){
                case USER_UP_SUCCESS:
                    mUser=(User)msg.getData().getSerializable(User.class.getName());
                    sharedPersistor.saveObject(mUser);
                    init();
                    break;
                case USER_UP_FAIL:
                    break;
            }

        }
    };

    final  int USER_UP_SUCCESS=1;
    final  int USER_UP_FAIL=0;

}
