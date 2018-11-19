package com.bisa.health;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.HealthServer;
import com.bisa.health.model.ResultData;
import com.bisa.health.model.User;
import com.bisa.health.model.dto.UserPwdDto;
import com.bisa.health.rest.HttpFinal;
import com.bisa.health.rest.service.IRestService;
import com.bisa.health.rest.service.RestServiceImpl;
import com.bisa.health.utils.MD5Help;
import com.bisa.health.utils.Utility;
import com.google.gson.reflect.TypeToken;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/25.
 */

public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener {


    @NotEmpty(messageResId = R.string.title_cur_pwd_tip)
    @Order(1)
    private EditText etxt_curpwd;

    @Password(min = 8, scheme = Password.Scheme.ANY, messageResId = R.string.title_pwd_not_match)
    @Order(2)
    private EditText etext_newpwd;

    @ConfirmPassword(messageResId = R.string.title_pwd_not_match)
    @NotEmpty(messageResId = R.string.title_commit_pwd_tip)
    @Order(3)
    private EditText etext_commitpwd;

    private boolean isValidation = false;

    private UserPwdDto userPwdDto;
    private IRestService mRestService;
    private SharedPersistor sharedPersistor;
    private HealthServer mHealthServer;
    private User mUser;
    private Button btn_login;
    private Validator validator;
    private static final String TAG = "BindAccessActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_pwd_layout);
        sharedPersistor = new SharedPersistor(this);
        mUser = sharedPersistor.loadObject(User.class.getName());
        mHealthServer = sharedPersistor.loadObject(HealthServer.class.getName());
        mRestService = new RestServiceImpl(this,mHealthServer);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        validator = new Validator(this);
        validator.setValidationMode(Validator.Mode.BURST);
        validator.setValidationListener(this);

        etxt_curpwd = (EditText) this.findViewById(R.id.etxt_curpwd);
        etext_newpwd = (EditText) this.findViewById(R.id.etext_newpwd);
        etext_commitpwd = (EditText) this.findViewById(R.id.etext_commitpwd);
    }


    @Override
    public void onClick(View v) {

        if (v == btn_login) {

            validator.validate();

            final String curpwd_text = etxt_curpwd.getText().toString();
            final String newpwd_text = etext_newpwd.getText().toString();
            final String commitpwd_text = etext_commitpwd.getText().toString();


            if (!isValidation) {
                return;
            }

            FormBody body = new FormBody.Builder()
                    .add("password", MD5Help.md5EnBit32(curpwd_text))
                    .add("new_password", MD5Help.md5EnBit32(newpwd_text))
                    .add("commit_password", MD5Help.md5EnBit32(commitpwd_text))
                    .build();

            show_Dialog(true);
            Call call = mRestService.modifyPwd(body);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog_Dismiss();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {


                    final String json = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog_Dismiss();

                            ResultData<Object> result = Utility.jsonToObject(json, new TypeToken<ResultData<Object>>() {
                            }.getType());

                            if (result == null) {
                                return;
                            }

                            if (result.getCode() == HttpFinal.CODE_200) {
                                show_Toast(result.getMessage(), Toast.LENGTH_LONG);
                                finish();
                            } else {
                                show_Toast(result.getMessage(), Toast.LENGTH_LONG);
                            }
                        }
                    });

                }
            });

        }

    }


    @Override
    public void onValidationSucceeded() {
        isValidation = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        isValidation = false;
        for (ValidationError error : errors) {
            String message = error.getCollatedErrorMessage(this);
            show_Toast(message);
            break;
        }
    }
}
