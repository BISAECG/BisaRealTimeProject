package com.bisa.health;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.adapter.CountryAdapter;
import com.bisa.health.adapter.IAdapterClickInterFace;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.cust.view.ActionBarView;
import com.bisa.health.cust.view.CustCountrySpinerPopWindow;
import com.bisa.health.cust.view.CustomDefaultDialog;
import com.bisa.health.model.Event;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.ServerDto;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.AreaUtil;
import com.bisa.health.utils.LoadDiaLogUtil;
import com.bisa.health.utils.Utility;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;


public class AddContactActivity extends BaseActivity implements IAdapterClickInterFace, View.OnClickListener, Validator.ValidationListener {

    private IRestService restService;
    private Event event;
    private CustCountrySpinerPopWindow custCountrySpinerPop;
    private CountryAdapter countryAdapter;
    private SharedPersistor sharedObjectPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private List<ServerDto> mListType;
    private boolean isValidation = false;
    private LinearLayout ll_layout;
    private Button spiner_img;
    private RelativeLayout rl_country;
    private Button btn_commit;

    @NotEmpty(messageResId = R.string.dialog_tip_error_name)
    @Order(1)
    public EditText tv_contact_value;

    @NotEmpty(messageResId = R.string.dialog_tip_error_area)
    @Order(2)
    public TextView tv_value;

    @NotEmpty(messageResId = R.string.dialog_tip_error_phone)
    @Order(3)
    public EditText tv_contact_num;

    public EditText tv_contact_email;

    private Validator validator;

    ActionBarView actionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        AppManager.getAppManager().addActivity(this);
        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);


        sharedObjectPersistor = new SharedPersistor(this);

        mHealthServer = sharedObjectPersistor.loadObject(HealthServer.class.getName());
        mUser = sharedObjectPersistor.loadObject(User.class.getName());
        mListType = AreaUtil.getListArea(this);
        actionBarView = findViewById(R.id.abar_title);
        actionBarView.setOnActionClickListenerNext(new ActionBarView.OnActionClickListener() {
            @Override
            public void onActionClick() {
                if (event != null) {

                    final CustomDefaultDialog.Builder builder = new CustomDefaultDialog.Builder(AddContactActivity.this)
                            .setIco(getResources().getDrawable(R.drawable.ico_contact))
                            .setTitle(getResources().getString(R.string.title_sos_contact_user))
                            .setMessage(getResources().getString(R.string.commit_del))
                            .setPositiveButton(getResources().getString(R.string.commit_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {

                                    Log.i(TAG, "onClick: del");

                                    dialog.dismiss();
                                    LoadDiaLogUtil.getInstance().show(AddContactActivity.this, false);
                                    FormBody body = new FormBody.Builder()
                                            .add("event_id", "" + event.getId())
                                            .build();
                                    Call call = restService.appDeleteContact(body);

                                    call.enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadDiaLogUtil.getInstance().dismiss();
                                                    showToast(getResources().getString(R.string.server_error));

                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LoadDiaLogUtil.getInstance().dismiss();
                                                }
                                            });

                                            final String json = response.body().string();
                                            Log.i("----", json);
                                            ResultData<List<Event>> result = Utility.jsonToObject(json, new TypeToken<ResultData<List<Event>>>() {
                                            }.getType());

                                            if (result == null) {
                                                return;
                                            }
                                            sharedObjectPersistor.saveObject(Event.class.getName() + "-" + mUser.getUser_guid(), result.getData());
                                            AddContactActivity.this.finish();
                                        }
                                    });
                                }
                            }).setNegativeButton(getResources().getString(R.string.cancel_no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();


                }
            }
        });


        btn_commit = (Button) this.findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);
        rl_country = (RelativeLayout) this.findViewById(R.id.rl_country);
        rl_country.setOnClickListener(this);
        spiner_img = (Button) this.findViewById(R.id.spiner_img);
        spiner_img.setOnClickListener(this);
        ll_layout = (LinearLayout) this.findViewById(R.id.layout);
        tv_value = (TextView) this.findViewById(R.id.tv_value);
        tv_contact_value = (EditText) this.findViewById(R.id.tv_contact_value);
        tv_contact_num = (EditText) this.findViewById(R.id.tv_contact_num);
        tv_contact_email = (EditText) this.findViewById(R.id.tv_contact_email);
        tv_value = (TextView) this.findViewById(R.id.tv_value);
        countryAdapter = new CountryAdapter(this);
        countryAdapter.setmObjects(mListType);
        custCountrySpinerPop = new CustCountrySpinerPopWindow(this);
        custCountrySpinerPop.setItemListener(this);
        custCountrySpinerPop.setAdatper(countryAdapter);
        restService = new RestServiceImpl(this, mHealthServer);

        event = (Event) getIntent().getSerializableExtra(Event.class.getName());

        if (event != null) {
            init(event);
        } else {
            areaDto = AreaUtil.CountryCodeByPhoneCode(mListType, mHealthServer.getAreaCode());
            tv_value.setText(areaDto.getCountry() + "(" + areaDto.getPhoneCode() + ")");
        }


    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private static final String TAG = "AddContactActivity";

    private void init(Event event) {

        Log.i(TAG, "onCreate: " + event.toString());

        try {
            tv_contact_value.setText(event.getEvent_name());
            String[] numAtr = event.getEvent_num().split("\\-");
            if (numAtr.length != 2) {
                return;
            }
            areaDto = AreaUtil.CountryCodeByPhoneCode(mListType, numAtr[0]);
            if (areaDto != null) {
                tv_value.setText(areaDto.getCountry() + "(" + areaDto.getPhoneCode() + ")");
            }
            tv_contact_num.setText(numAtr[1]);
            tv_contact_email.setText(event.getEvent_mail());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


    }

    /**
     * 国家pop点击回调接口
     *
     * @param pos
     */

    private ServerDto areaDto;

    @Override
    public void onItemClick(int pos) {
        if (pos >= 0) {
            areaDto = mListType.get(pos);
            tv_value.setText(areaDto.getCountry() + "(" + areaDto.getPhoneCode() + ")");
        }
    }

    @Override
    public void onClick(View v) {

        if (v == rl_country || v == spiner_img) {

            custCountrySpinerPop.showAsDropDown(ll_layout);
        }
        if (v == btn_commit) {
            validator.validate();
            if (!isValidation) {
                return;
            }
            LoadDiaLogUtil.getInstance().show(AddContactActivity.this, false);
            final String name = tv_contact_value.getText().toString();
            final String iphone = tv_contact_num.getText().toString();
            final String mail = StringUtils.isEmpty(tv_contact_email.getText().toString()) ? "" : tv_contact_email.getText().toString();
            final String arecCode = areaDto.getPhoneCode();
            Log.i(TAG, "onClick: "+event);
            FormBody body = new FormBody.Builder()

                    .add("event_id", ""+(event!=null?""+event.getId():""))
                    .add("event_name", name)
                    .add("event_num", arecCode + "-" + iphone)
                    .add("event_type", "" + 1)
                    .add("event_mail", "" + mail)
                    .build();

            Call call = restService.addContact(body);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadDiaLogUtil.getInstance().dismiss();
                            showToast(getResources().getString(R.string.server_error));

                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String json = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadDiaLogUtil.getInstance().dismiss();

                            Log.i("----", json);
                            ResultData<List<Event>> result = Utility.jsonToObject(json, new TypeToken<ResultData<List<Event>>>() {
                            }.getType());

                            if (result == null) {
                                return;
                            }
                            if (result.getData() != null) {
                                sharedObjectPersistor.saveObject(Event.class.getName() + "-" + mUser.getUser_guid(), result.getData());
                                AddContactActivity.this.finish();
                            } else {
                                showToast(result.getMessage());
                            }

                        }
                    });

                }
            });

        }
    }


    /**
     * 表单验证成功OR失败处理事件
     */
    @Override
    public void onValidationSucceeded() {
        isValidation = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isValidation = false;
        for (ValidationError error : errors) {
            String message = error.getCollatedErrorMessage(this);
            showToast(message);
            break;
        }

    }

}
