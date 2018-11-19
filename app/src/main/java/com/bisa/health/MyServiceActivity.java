package com.bisa.health;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bisa.health.model.dto.UserPwdDto;
import com.bisa.health.rest.service.IRestService;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class MyServiceActivity extends BaseActivity implements View.OnClickListener, Validator.ValidationListener{

    @NotEmpty(messageResId = R.string.dialog_tip_error_code)
    @Order(1)
    private EditText tv_code;

    private TextView tv_tip;

    private Button imgbtn_smsSend;

    private Button btn_login;

    private UserPwdDto userPwdDto;

    private boolean isValidation = false;

    private IRestService restService;


    private Validator validator;

    private static final String TAG = "ForPwdVailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {

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
