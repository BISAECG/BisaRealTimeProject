package com.bisa.health.cust;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2018/5/15.
 */

public class CustomDownProgressDialog extends Dialog {




    public CustomDownProgressDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDownProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

    }

    protected CustomDownProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }
    private static  RelativeLayout ll_main_a;
    private static  RelativeLayout ll_main_b;

    private static ProgressBar progressBar;

    public void switchView(boolean isPro){

        if(ll_main_a!=null&&ll_main_b!=null){
            if(isPro){
                ll_main_a.setVisibility(View.VISIBLE);
                ll_main_b.setVisibility(View.GONE);
            }else{
                ll_main_a.setVisibility(View.GONE);
                ll_main_b.setVisibility(View.VISIBLE);
            }
        }


    }

    //设置进度条
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    //获取进度条
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    //设置进度
    public void setProgress(int progress){
        progressBar.setProgress(progress);
    }

    public static class Builder {

        private TextView tv_title;
        private Button btn_commit;
        private Button btn_cancel;
        private TextView tv_desc;
        private TextView tv_desc_error;
        private Context context;
        private OnClickListener negativeClickListener;
        private OnClickListener positiveClickListener;
        private String yes_text;
        private String no_text;

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.yes_text=positiveButtonText;
            this.positiveClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.no_text=negativeButtonText;
            this.negativeClickListener = listener;
            return this;
        }

        public Builder(Context context) {
            this.context = context;
        }

        public CustomDownProgressDialog create(){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDownProgressDialog dialog = new CustomDownProgressDialog(context,R.style.Dialog);
            View view = inflater.inflate(R.layout.dialog_fee_report_down, null);
            //加载布局
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.rl_main_all);
            progressBar = (ProgressBar) view.findViewById(R.id.pb_Circle);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            tv_desc_error= (TextView) view.findViewById(R.id.tv_desc_error);
            btn_commit=(Button) view.findViewById(R.id.btn_commit);

            if (positiveClickListener != null) {
                btn_commit.setText(yes_text);
                btn_commit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                positiveClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
                        });
            }

            btn_cancel=(Button) view.findViewById(R.id.btn_cancel);

            if (negativeClickListener != null) {
                btn_cancel.setText(no_text);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        negativeClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }




            ll_main_a=(RelativeLayout) view.findViewById(R.id.ll_main_a);
            ll_main_b=(RelativeLayout) view.findViewById(R.id.ll_main_b);
            // 设置布局，设为全屏
            dialog.setContentView(view, new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            return dialog;
        }


        // 设置加载信息
        public void setMessage(String msg){
            tv_desc.setText(msg);
        }


        private static final String TAG = "CustomPackageProgressDialog";

    }




}

