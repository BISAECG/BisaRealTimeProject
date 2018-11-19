package com.bisa.health.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bisa.health.R;

public final class Notificator {
	public static final int NOTIFI_ID = 1986;
	public static final int FOREGROUND_PUST_ID=1;
	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	NotificationManager notimanager;
	private Context context;
	private final String ticker = "悉心";
	private final String title = "悉心康健";
	private final String content = "健康生活 悉心呵护";
	
	public Notificator(Context context) {
		super();
		this.context = context;
		if (null == notimanager)
			notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	
	

	public synchronized void RunningNotifiction(String msg,Class<?> clz) {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(context,clz);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(msg)
				.setContentText(msg)
				.setOngoing(true)
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setTicker(ticker)
				.setContentIntent(pendingIntent);
		Notification notification=mBuilder.build();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notimanager.notify(NOTIFI_ID,notification);
	}

	public synchronized void CancelRunningNotif() {

		if (null != notimanager) {
			notimanager.cancel(NOTIFI_ID);
		}
	}



}
