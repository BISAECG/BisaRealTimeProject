package com.bisa.health.cust;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.bisa.health.R;
import com.bisa.health.utils.CountDefaultTimerUtils;

public class CustomOTGWelcomeDialog extends Dialog  {

    public CustomOTGWelcomeDialog(Context context) {
        super(context);
    }

    public CustomOTGWelcomeDialog(Context context, int theme) {
        super(context, theme);  
    }

    public static class Builder {
        private Context context;
        private Button positiveButton;
        private OnClickListener positiveButtonClickListener;
        private ImageView iv_otg_right_line;

        public Builder(Context context) {
            this.context = context;
        }



        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }


        public CustomOTGWelcomeDialog create() {


            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomOTGWelcomeDialog dialog = new CustomOTGWelcomeDialog(context,R.style.Dialog);
            View view = inflater.inflate(R.layout.dialog_otg_welcome_layout, null);
            Animation paningAnim = AnimationUtils.loadAnimation(
                    context, R.anim.panning_inleft);
            iv_otg_right_line=(ImageView) view.findViewById(R.id.iv_otg_right_line);
            iv_otg_right_line.setAnimation(paningAnim);
            positiveButton=(Button) view.findViewById(R.id.positiveButton);
            final CountDefaultTimerUtils mCountDefaultTimerUtils=new CountDefaultTimerUtils(positiveButton, 6000, 1000, context,context.getString(R.string.otg_conn_commit_s),context.getString(R.string.otg_conn_commit));
            mCountDefaultTimerUtils.start();
                if (positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }



            dialog.setCancelable(false);
            dialog.setContentView(view, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return dialog;
        }
    }
}
