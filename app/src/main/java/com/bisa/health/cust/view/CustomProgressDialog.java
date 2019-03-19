package com.bisa.health.cust.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2018/5/15.
 */

public class CustomProgressDialog extends Dialog {


   public interface DialogCall{
        void setProgress(int p);
        void switchView(boolean isPro);
    }

    public interface CallDialogBuild{
        void builder(DialogCall dialogCall);
    }

    public CustomProgressDialog(@NonNull Context context) {
        super(context);
    }

    public CustomProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

    }

    protected CustomProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }


    public static class Builder{


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
        private ImageView img;
        private RelativeLayout ll_main_a;
        private RelativeLayout ll_main_b;
        private int title;
        private int body;
        private int icoid;
        private int errorBody;
        private ProgressBar progressBar;
        private DialogCall dialogCall;
        private CallDialogBuild mBuild;


        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.yes_text=positiveButtonText;
            this.positiveClickListener = listener;
            return this;
        }
        public Builder setTitle(int title){
            this.title=title;
            return this;
        }

        public Builder setICO(int icoid){
            this.icoid=icoid;
            return this;
        }

        public Builder setBody(int body){
            this.body=body;
            return this;
        }
        public Builder setErrorBody(int errorBody){
            this.errorBody=errorBody;
            return this;
        }

        public Builder setDialogCall(CallDialogBuild build){
            mBuild=build;
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

        public CustomProgressDialog create(){

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomProgressDialog dialog = new CustomProgressDialog(context,R.style.Dialog);
            View view = inflater.inflate(R.layout.dialog_progress_layout, null);
            //加载布局
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.rl_main_all);
            progressBar = (ProgressBar) view.findViewById(R.id.pb_Circle);
            img=view.findViewById(R.id.img_ico);
            img.setImageDrawable(context.getResources().getDrawable(icoid));

            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText(context.getString(title));

            tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            tv_desc.setText(body);

            tv_desc_error= (TextView) view.findViewById(R.id.tv_desc_error);
            tv_desc_error.setText(errorBody);

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

            dialogCall=new DialogCall() {
                @Override
                public void setProgress(int p) {
                    progressBar.setProgress(p);
                }

                @Override
                public void switchView(boolean isPro) {
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
            };
            if(mBuild!=null){
                mBuild.builder(dialogCall);
            }

            // 设置布局，设为全屏
            dialog.setContentView(view, new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            return dialog;
        }


    }




}

