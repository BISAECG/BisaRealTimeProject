package com.bisa.health.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.bisa.health.BisaApplication;
import com.bisa.health.R;

public final class Notificator {
	public static final int NOTIFI_ID = 1986;
	public static final int FOREGROUND_PUST_ID=1;
	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	NotificationManager notimanager;
	private Context context;

	public Notificator(Context context) {
		super();
		this.context = context;
		if (null == notimanager)
			notimanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationChannel notificationChannel = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			notificationChannel = new NotificationChannel(BisaApplication.NOTIFICATION_CHANNEL_ID_TASK,
					"Bisa health", NotificationManager.IMPORTANCE_HIGH);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setShowBadge(true);
			notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
			notimanager.createNotificationChannel(notificationChannel);
		}
	}
	
	
	

	public synchronized void RunningNotifiction(String msg,Class<?> clz) {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(context,clz);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
				.setChannelId(BisaApplication.NOTIFICATION_CHANNEL_ID_TASK)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(msg)
				.setContentText(msg)
				.setOngoing(true)
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)
				.setTicker(context.getResources().getString(R.string.app_name))
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
