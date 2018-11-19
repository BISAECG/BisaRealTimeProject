package com.bisa.health;

import android.app.Activity;

import com.bisa.health.utils.ActivityUtil;

import java.util.Stack;

public class AppManager {


	private  Stack<Activity> activityStack;

	private static AppManager instance;

	private AppManager() {
		activityStack = new Stack<Activity>();
	}

	public int size(){
		return activityStack.size();
	}



	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {

		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		if(activityStack.size()<=0){
			return null;
		}
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		if(activityStack.size()<=0){
			return;
		}
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	public Activity firstActivity(){
		if(activityStack.size()>0){
			return activityStack.firstElement();
		}else {
			return null;
		}

	}
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null&&activityStack.size()>0) {
			activityStack.remove(activity);
			ActivityUtil.finishNotAnim(activity);
			activity = null;
		}
	}


	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
				break;
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0; i < activityStack.size(); i++) {
			if (null != activityStack.get(i)) {
				ActivityUtil.finishNotAnim(activityStack.get(i));
			}
		}
		activityStack.clear();
	}

}
