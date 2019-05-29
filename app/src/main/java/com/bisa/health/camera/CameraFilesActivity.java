package com.bisa.health.camera;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bisa.health.BaseActivity;
import com.bisa.health.R;
import com.bisa.health.camera.adapter.CameraFilesCameraLvAdapter;
import com.bisa.health.camera.adapter.CameraFilesPhoneLvAdapter;
import com.bisa.health.camera.lib.funsdk.support.FunSupport;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceOptListener;
import com.bisa.health.camera.lib.funsdk.support.OnFunDeviceRecordListener;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevRecordFile;
import com.bisa.health.camera.lib.funsdk.support.models.FunDevice;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.bisa.health.camera.lib.sdk.struct.H264_DVR_FINDINFO;
import com.bisa.health.camera.lib.sdk.struct.SDK_SearchByTime;
import com.lib.SDKCONST;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CameraFilesActivity extends BaseActivity {
    private LinearLayout lLayout;
    private ImageButton ibtnBack;
    private ImageButton ibtnCalendar;
    private TabHost tabHost;

    private ListView lvFilesPhone;
    private CameraFilesPhoneLvAdapter phoneAdapter;
    private List<File> fileList = new ArrayList<>();

    private String fileDir;
    private File dir;

    private TextView tvCameraDate;
    private ListView lvFilesCamera;
    private CameraFilesCameraLvAdapter cameraAdapter;
    private List<H264_DVR_FILE_DATA> picDataList = new ArrayList<>();
    private View viewCamLvHeader;
    private ImageView ivThumbCamLvHeader;
    private TextView tvTimeCamLvHeader;

    private FunDevice mFunDevice;
    private OnFunDeviceRecordListener onFunDeviceRecordListener;
    private OnFunDeviceOptListener onFunDeviceOptListener;

    private Calendar calendar = Calendar.getInstance();;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private BottomSheetDialog bottomSheetDialog;
    private CalendarView calendarView;

    private int dialogCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_files);

        fileDir = getIntent().getStringExtra("fileDir");

        lLayout = findViewById(R.id.llayout_camera_files);
        ibtnBack = findViewById(R.id.ibtn_camera_files_back);
        ibtnCalendar = findViewById(R.id.ibtn_camera_files_calendar);

        tabHost = findViewById(R.id.tabhost_camera_files);

        //初始化TabHost容器
        tabHost.setup();
        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        tabHost.addTab(tabHost.newTabSpec("phone").setIndicator(getString(R.string.camera_files_tab_phone) , null).setContent(R.id.lv_camera_files_phone));
        tabHost.addTab(tabHost.newTabSpec("camera").setIndicator(getString(R.string.camera_files_tab_camera) , null).setContent(R.id.tab2_camera_files));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "phone":
                        ibtnCalendar.setVisibility(View.GONE);
                        break;
                    case "camera":
                        ibtnCalendar.setVisibility(View.VISIBLE);
                        tvCameraDate.setText(sdf.format(calendar.getTime()));

                        lvFilesCamera.removeHeaderView(viewCamLvHeader);
                        picDataList.clear();
                        cameraAdapter.notifyDataSetChanged();

                        showDialog(false);
                        dialogCount = 2;
                        onSearchRecordFile();
                        onSearchPicture();
                        break;
                }
            }
        });

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ibtnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Android5.0 以上使用，兼容性问题
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bottomSheetDialog.show();
                }
                else {
                    new DatePickerDialog(CameraFilesActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                            tvCameraDate.setText(sdf.format(calendar.getTime()));

                            lvFilesCamera.removeHeaderView(viewCamLvHeader);
                            picDataList.clear();
                            cameraAdapter.notifyDataSetChanged();

                            showDialog(false);
                            dialogCount = 2;
                            onSearchRecordFile();
                            onSearchPicture();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            }
        });

        lvFilesPhone = findViewById(R.id.lv_camera_files_phone);
        phoneAdapter = new CameraFilesPhoneLvAdapter(this, fileList);
        lvFilesPhone.setAdapter(phoneAdapter);

        dir = new File(fileDir);


        refreshFiles();

        lvFilesPhone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = phoneAdapter.getItem(position);
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


        tvCameraDate = findViewById(R.id.tv_camera_files_camera_date);
        tvCameraDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibtnCalendar.callOnClick();
            }
        });
        lvFilesCamera = findViewById(R.id.lv_camera_files_camera);
        cameraAdapter = new CameraFilesCameraLvAdapter(CameraFilesActivity.this, picDataList);
        lvFilesCamera.setAdapter(cameraAdapter);

        lvFilesCamera.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < lvFilesCamera.getHeaderViewsCount()) {
                    Intent intent = new Intent(CameraFilesActivity.this, CameraFilesViewRecordActivity.class);
                    intent.putExtra("calendar", calendar.getTimeInMillis());
                    intent.putExtra("fileDir", fileDir);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(CameraFilesActivity.this, CameraFilesViewPicActivity.class);
                    intent.putExtra("position", position - lvFilesCamera.getHeaderViewsCount());
                    intent.putExtra("fileDir", fileDir);
                    intent.putExtra("calendar", calendar.getTimeInMillis());
                    intent.putExtra("title", ((H264_DVR_FILE_DATA)(parent.getAdapter().getItem(position))).getStartTimeOfYear());
                    startActivity(intent);
                }
            }
        });

        viewCamLvHeader = LayoutInflater.from(this).inflate(R.layout.item_camera_files_camera, null);
        ivThumbCamLvHeader = viewCamLvHeader.findViewById(R.id.iv_item_camera_files_camera_thumbnail);
        tvTimeCamLvHeader = viewCamLvHeader.findViewById(R.id.tv_item_camera_files_camera_time);

        bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bottom_camera_files_calendar, null);
        calendarView = view.findViewById(R.id.calendarView_camera_files);
        Button btnCancel = view.findViewById(R.id.btn_camera_files_calendar_cancel);
        bottomSheetDialog.setContentView(view);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                tvCameraDate.setText(sdf.format(calendar.getTime()));

                bottomSheetDialog.dismiss();
                lvFilesCamera.removeHeaderView(viewCamLvHeader);
                picDataList.clear();
                cameraAdapter.notifyDataSetChanged();

                showDialog(false);
                dialogCount = 2;
                onSearchRecordFile();
                onSearchPicture();
            }
        });



        mFunDevice = FunSupport.getInstance().mCurrDevice;

        onFunDeviceRecordListener = new OnFunDeviceRecordListener() {
            @Override
            public void onRequestRecordListSuccess(List<FunDevRecordFile> files) {
                dialogCount --;
                if(dialogCount == 0) {
                    dialogDismiss();
                }
                if (files.size() > 0) {
                    lvFilesCamera.removeHeaderView(viewCamLvHeader);
                    String str = files.get(0).getRecStartTime() + "  -  " + files.get(0).getRecEndTime();
                    ivThumbCamLvHeader.setImageResource(R.drawable.camera_files_video);
                    tvTimeCamLvHeader.setText(str);
                    lvFilesCamera.addHeaderView(viewCamLvHeader);
                }

            }

            @Override
            public void onRequestRecordListFailed(Integer errCode) {
                dialogCount --;
                if(dialogCount == 0) {
                    dialogDismiss();
                }
            }
        };
        onFunDeviceOptListener = new OnFunDeviceOptListener() {
            @Override
            public void onDeviceLoginSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

            }

            @Override
            public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

            }

            @Override
            public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

            }

            @Override
            public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

            }

            @Override
            public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

            }

            @Override
            public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

            }

            @Override
            public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice) {

            }

            @Override
            public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {
                if (funDevice.getId() == mFunDevice.getId()) {

                    dialogCount --;
                    if(dialogCount == 0) {
                        dialogDismiss();
                    }

                    picDataList.clear();

                    Collections.addAll(picDataList, datas);

                    //此处传入下载不了图片
                    //mFunDevice.mDatas = picDataList;

                    cameraAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDeviceFileListGetFailed(FunDevice funDevice) {
                if(funDevice.getId() == mFunDevice.getId()) {
                    dialogCount --;
                    if(dialogCount == 0) {
                        dialogDismiss();
                    }
                }
            }
        };

        FunSupport.getInstance().registerOnFunDeviceRecordListener(onFunDeviceRecordListener);

    }

    @Override
    protected void onDestroy() {
        FunSupport.getInstance().removeOnFunDeviceRecordListener(onFunDeviceRecordListener);

        super.onDestroy();
    }

    private void refreshFiles() {
        File[] files = dir.listFiles();
        fileList.clear();

        Collections.addAll(fileList, files);

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
        phoneAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        FunSupport.getInstance().registerOnFunDeviceOptListener(onFunDeviceOptListener);
        refreshFiles();
        super.onResume();
    }

    @Override
    protected void onPause() {
        FunSupport.getInstance().removeOnFunDeviceOptListener(onFunDeviceOptListener);
        super.onPause();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                calendar.setTimeInMillis(data.getLongExtra("calendar", 0));

                tvCameraDate.setText(sdf.format(calendar.getTime()));

                lvFilesCamera.removeHeaderView(viewCamLvHeader);
                picDataList.clear();
                cameraAdapter.notifyDataSetChanged();

                showDialog(false);
                dialogCount = 2;
                onSearchRecordFile();
                onSearchPicture();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onSearchRecordFile() {
        int[] time = { calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE) };

        SDK_SearchByTime search_info = new SDK_SearchByTime();
        search_info.st_6_nHighStreamType = 0;
        search_info.st_7_nLowStreamType = 0;
        search_info.st_1_nLowChannel = MasktoInt(mFunDevice.CurrChannel);
        search_info.st_2_nFileType = 0;
        search_info.st_3_stBeginTime.st_0_year = time[0];
        search_info.st_3_stBeginTime.st_1_month = time[1];
        search_info.st_3_stBeginTime.st_2_day = time[2];
        search_info.st_3_stBeginTime.st_4_hour = 0;
        search_info.st_3_stBeginTime.st_5_minute = 0;
        search_info.st_3_stBeginTime.st_6_second = 0;
        search_info.st_4_stEndTime.st_0_year = time[0];
        search_info.st_4_stEndTime.st_1_month = time[1];
        search_info.st_4_stEndTime.st_2_day = time[2];
        search_info.st_4_stEndTime.st_4_hour = 23;
        search_info.st_4_stEndTime.st_5_minute = 59;
        search_info.st_4_stEndTime.st_6_second = 59;
        FunSupport.getInstance().requestDeviceFileListByTime(mFunDevice, search_info);
    }
    private int MasktoInt(int channel){
        int MaskofChannel = 0;
        MaskofChannel = (1 << channel) | MaskofChannel;
        return MaskofChannel;
    }


    private void onSearchPicture() {
        H264_DVR_FINDINFO findInfo = new H264_DVR_FINDINFO();
        findInfo.st_1_nFileType = SDKCONST.SDK_File_Type.SDK_PIC_ALL;
        initSearchInfo(findInfo, mFunDevice.CurrChannel);
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, findInfo);
    }

    private void initSearchInfo(H264_DVR_FINDINFO info, int channel) {

        info.st_2_startTime.st_0_dwYear = calendar.get(Calendar.YEAR);
        info.st_2_startTime.st_1_dwMonth = calendar.get(Calendar.MONTH) + 1;
        info.st_2_startTime.st_2_dwDay = calendar.get(Calendar.DATE);

        info.st_2_startTime.st_3_dwHour = 0;
        info.st_2_startTime.st_4_dwMinute = 0;
        info.st_2_startTime.st_5_dwSecond = 0;

        info.st_3_endTime.st_0_dwYear = calendar.get(Calendar.YEAR);
        info.st_3_endTime.st_1_dwMonth = calendar.get(Calendar.MONTH) + 1;
        info.st_3_endTime.st_2_dwDay = calendar.get(Calendar.DATE);
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = channel;
    }

}
