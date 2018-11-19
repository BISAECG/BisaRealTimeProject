package com.bisa.health.cust;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.cust.anim.CircleAnimation;

import static com.bisa.health.exception.CrashHandler.TAG;

public class CloudDialog extends Dialog  {

    public CloudDialog(Context context) {
        super(context);
    }

    public CloudDialog(Context context, int theme) {
        super(context, theme);  
    }  
    
    public static class Builder {  
        private Context context;  
        private String title;
        private Drawable drawable;
        private String message;
        private View contentView;
        private TextView txt_body;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private LinearLayout ll_btnbody;
        private ImageView iv_glass;
        private  CircleAnimation animation;
  
        public Builder(Context context) {  
            this.context = context;  
        }  
  
        public Builder setMessage(String message) {  
            this.message = message;  
            return this;  
        }  
  
        /** 
         * Set the Dialog message from resource 
         *  
         * @param message
         * @return 
         */  
        public Builder setMessage(int message) {  
            this.message = (String) context.getText(message);  
            return this;  
        }  
  

        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
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

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonClickListener = listener;
            return this;
        }

        public void showMsg(String msg){
            if(txt_body!=null){
                txt_body.setText(msg);
            }
        }

        public void showTry(){
            if(ll_btnbody!=null){
                Log.i(TAG, "showTry: >>>>>>>>>>>>>show");
                ll_btnbody.setVisibility(View.VISIBLE);
            }
            if(iv_glass!=null&&animation!=null){
                iv_glass.clearAnimation();
            }
        }

        public void hideTry(){
            if(ll_btnbody!=null){
                ll_btnbody.setVisibility(View.GONE);
            }
            if(iv_glass!=null&&animation!=null){
                iv_glass.setAnimation(animation);
            }
        }


        public CloudDialog create() {
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final CloudDialog dialog = new CloudDialog(context,R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_cloud_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title

            txt_body=((TextView) layout.findViewById(R.id.txt_body));
            txt_body.setText(message);
            ll_btnbody=(LinearLayout) layout.findViewById(R.id.ll_btnbody);
            animation = new CircleAnimation(20);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());//不停顿
            animation.setRepeatMode(Animation.RESTART);
            animation.setRepeatCount(Animation.INFINITE);
            iv_glass = (ImageView) layout.findViewById(R.id.iv_glass);
            iv_glass.setAnimation(animation);
            // set the confirm button
                if (positiveButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.positiveButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);  
                                }  
                            });  
                }
                if(negativeButtonClickListener!=null){
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }

            dialog.setCancelable(true);
            dialog.setContentView(layout);  
            return dialog;  
        }  
    }  
}
