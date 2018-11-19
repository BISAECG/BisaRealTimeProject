package com.bisa.health.cust;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bisa.health.R;

public class CustomDefaultDialog extends Dialog  {
	 
    public CustomDefaultDialog(Context context) {
        super(context);  
    }  
  
    public CustomDefaultDialog(Context context, int theme) {
        super(context, theme);  
    }  
    
    public static class Builder {  
        private Context context;  
        private String title;
        private Drawable drawable;
        private String message;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
  
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
  
        /** 
         * Set the Dialog title from resource 
         *  
         * @param title 
         * @return 
         */  
        public Builder setTitle(int title) {  
            this.title = (String) context.getText(title);  
            return this;  
        }  
  
        /** 
         * Set the Dialog title from String 
         *  
         * @param title 
         * @return 
         */  
  
        public Builder setTitle(String title) {  
            this.title = title;  
            return this;  
        }  
  
        public Builder setContentView(View v) {  
            this.contentView = v;  
            return this;  
        }

        public Builder setIco(Drawable drawable){
            this.drawable=drawable;
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
  
        public CustomDefaultDialog create() {
            LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final CustomDefaultDialog dialog = new CustomDefaultDialog(context,R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_default_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  
            // set the dialog title  
            ((TextView) layout.findViewById(R.id.txt_body)).setText(message);
            ((TextView) layout.findViewById(R.id.tv_title)).setText(title);
            ((ImageView)layout.findViewById(R.id.iv_ico)).setImageDrawable(drawable);


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
    
            // set the cancel button  
                if (negativeButtonClickListener != null) {  
                    ((Button) layout.findViewById(R.id.negativeButton))  
                            .setOnClickListener(new View.OnClickListener() {  
                                public void onClick(View v) {  
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);  
                                }  
                            });  
                }  
            dialog.setCancelable(false);
            dialog.setContentView(layout);  
            return dialog;  
        }  
    }  
}
