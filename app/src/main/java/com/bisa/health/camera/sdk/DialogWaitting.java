package com.bisa.health.camera.sdk;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.bisa.health.R;

public class DialogWaitting {

	private Dialog mDialog;
	
	public DialogWaitting(Context context) {
		mDialog = new Dialog(context, R.style.dialog_translucent);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mDialog.setContentView(R.layout.dialog_waiting);
	}
	
	
	public void show() {
		mDialog.show();
	}

	
	public void dismiss() {
		mDialog.dismiss();
	}

	public boolean isShowing() {
		return mDialog.isShowing();
	}
}
