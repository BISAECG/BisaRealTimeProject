package com.bisa.health.cust;


import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.MainActivity;
import com.bisa.health.R;
import com.bisa.health.UserInfoActivity;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.ecg.ReportCalendarActivity;
import com.bisa.health.model.HealthPath;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.User;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.pay.BisaServiceActivity;
import com.bisa.health.provider.device.DeviceSelection;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.ActivityUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserCenterFragment extends android.support.v4.app.Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "UserCenterFragment";
    private View view;
    private SharedPersistor sharedPersistor;
    private CircleImageView img_avatar;
    private TextView txt_dev_address;
    private RelativeLayout rl_nav_title;
    private User mUser;
    private IRestService mRestService;
    private HealthServer mHealthServer;
    private HealthPath mHealthPath;
    private boolean isWriteStatus = true;

    private RelativeLayout rl_sos;
    private RelativeLayout rl_wechat;
    private RelativeLayout rl_callme;
    private RelativeLayout rl_show_service;
    private RelativeLayout rl_bind_access;
    private RelativeLayout rl_exit;

    private MainActivity self;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPersistor = new SharedPersistor(context);
        mHealthPath = sharedPersistor.loadObject(HealthPath.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService = new RestServiceImpl(context, mHealthServer);
        mUser = sharedPersistor.loadObject(User.class.getName());
        this.getLoaderManager().initLoader(0, null, this);
        self = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public UserCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_center, container, false);
        rl_nav_title = (RelativeLayout) view.findViewById(R.id.rl_nav_title);
        rl_nav_title.setOnClickListener(this);
        txt_dev_address = (TextView) view.findViewById(R.id.txt_dev_address);
        img_avatar = (CircleImageView) view.findViewById(R.id.img_avatar);

        rl_sos = (RelativeLayout) view.findViewById(R.id.rl_sos);
        rl_sos.setOnClickListener(this);

        rl_wechat = (RelativeLayout) view.findViewById(R.id.rl_wechat);
        rl_wechat.setOnClickListener(this);

        rl_callme = (RelativeLayout) view.findViewById(R.id.rl_callme);
        rl_callme.setOnClickListener(this);

        rl_show_service = (RelativeLayout) view.findViewById(R.id.rl_show_service);
        rl_show_service.setOnClickListener(this);

        rl_bind_access = (RelativeLayout) view.findViewById(R.id.rl_bind_access);
        rl_bind_access.setOnClickListener(this);

        rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
        rl_exit.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == rl_nav_title) {
            ActivityUtil.startActivity(getActivity(), UserInfoActivity.class, false, ActionEnum.NEXT);
        } else if (v == rl_wechat) {
            ActivityUtil.startActivity(getActivity(), ReportCalendarActivity.class, false, ActionEnum.NULL);
        } else if (v == rl_show_service) {
            ActivityUtil.startActivity(getActivity(), BisaServiceActivity.class, false, ActionEnum.NULL);
        } else if (v == rl_callme) {
            self.show_Toast(getString(R.string.title_not_erect));
        } else if (v == rl_exit) {


            final CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(getActivity())
                    .setIco(getResources().getDrawable(R.drawable.ico_exit))
                    .setMessage(getResources().getString(R.string.title_is_restart_user))
                    .setTitle(getResources().getString(R.string.title_exit))
                    .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mHealthServer.setToken(null);
                            Log.i(TAG, "onClick: exit");
                            sharedPersistor.saveObject(mHealthPath);
                            self.restartApp();
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();

        } else {
            String clzName = v.getTag().toString();
            Log.i(TAG, "onClick: " + v.getTag().toString());
            if (!StringUtils.isEmpty(clzName)) {
                try {
                    Class<?> clz = Class.forName(clzName);
                    ActivityUtil.startActivity(getActivity(), clz, false, ActionEnum.NEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private void init() {

        Log.i(TAG, "init: "+mUser.getUri_pic());
        mUser=sharedPersistor.loadObject(User.class.getName());
        if (!StringUtils.isEmpty(mUser.getUri_pic())) {
            img_avatar.setImageURI(mUser.getUri_pic(),mHealthPath);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DeviceSelection where = new DeviceSelection();
        where.userGuid(mUser.getUser_guid());
        return where.getCursorLoader(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String allDeviceStr = getResources().getString(R.string.title_all_device);
        String fromatStr = String.format(allDeviceStr, "" + data.getCount());
        txt_dev_address.setText(fromatStr);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
