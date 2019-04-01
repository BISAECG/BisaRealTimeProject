package com.bisa.health.camera;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bisa.health.R;
import com.bisa.health.camera.sdk.DialogWaitting;
import com.bisa.health.camera.sdk.UIFactory;

import java.util.Arrays;
import java.util.List;

public class ActivityDemo extends FragmentActivity {
	private DialogWaitting mWaitDialog = null;
	private Toast mToast = null;
	
	private View mNavRightView = null;
	
	public void showWaitDialog() {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show();
	}
	
	public void showWaitDialog(int resid) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(resid);
	}
	
	public void showWaitDialog(String text) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(text);
	}
	
	public void hideWaitDialog() {
		if ( null != mWaitDialog ) {
			mWaitDialog.dismiss();
		}
	}
	
	public void showToast(String text){
		if ( null != text ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
	
	public void showToast(int resid){
		if ( resid > 0 ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
	
	/**
	  *  判断某个字符串是否存在于数组中
	  *  用来判断该配置是否通道相关
	  *  @param stringArray 原数组
	  *  @param source 查找的字符串
	  *  @return 是否找到
	  */
	 public static boolean contains(String[] stringArray, String source) {
	  // 转换为list
	  List<String> tempList = Arrays.asList(stringArray);
	  
	  // 利用list的包含方法,进行判断
		 return tempList.contains(source);
	 }
	
	// 只有布局中有指定的标题栏的Activity才允许设置

}
