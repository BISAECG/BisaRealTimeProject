package com.bisa.health.cust.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.bisa.health.R;

public class KillOutDialog extends Dialog  {

    public KillOutDialog(Context context) {
        super(context);
    }

    public KillOutDialog(Context context, int theme) {
        super(context, theme);  
    }

    public static class Builder {
        private Context context;
        private Button positiveButton;
        private OnClickListener positiveButtonClickListener;


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


        public KillOutDialog create() {


            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final KillOutDialog dialog = new KillOutDialog(context,R.style.Dialog);
            View view = inflater.inflate(R.layout.dialog_killout_layout, null);
            positiveButton=(Button) view.findViewById(R.id.positiveButton);
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
