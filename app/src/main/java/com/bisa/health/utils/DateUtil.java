package com.bisa.health.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

	public static String getGMTDateTime(String formattext) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		String datetime = DateFormat.format(formattext, cal).toString();
		return datetime;
	}

	public static String getGMTDateTime(String formattext, int seconds) {

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		cal.add(Calendar.SECOND, +seconds);
		String datetime = DateFormat.format(formattext, cal).toString();
		return datetime;
	}
	public static long getGMTDateTime() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}

	public static String getGMTDateTime(String formattext, long milliSeconds) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.setTime(new Date(milliSeconds));
		String datetime = DateFormat.format(formattext, cal).toString();
		return datetime;
	}

	public static Calendar getUCalendar(long milliSeconds){
		Date date=new Date(milliSeconds);
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public static Calendar getUCalendar(long milliSeconds, String timeZoneStr){
        TimeZone timeZone=TimeZone.getTimeZone(timeZoneStr);
        Date date=new Date(milliSeconds);
        long chineseMills = date.getTime() + timeZone.getRawOffset();
        Date chineseDateTime = new Date(chineseMills);
        Calendar cal=Calendar.getInstance(timeZone);
        cal.setTime(chineseDateTime);
        return cal;
    }
	public static Date getUTimeDate(long milliSeconds, String timeZoneStr) {
		TimeZone timeZone=TimeZone.getTimeZone(timeZoneStr);
		Date date=new Date(milliSeconds);
		long chineseMills = date.getTime() + timeZone.getRawOffset();
		Date chineseDateTime = new Date(chineseMills);
		return chineseDateTime;

	}

	public static long getServerMilliSeconds(String serverTimeZoneStr){
		TimeZone servertTimeZone=TimeZone.getTimeZone(serverTimeZoneStr);
		TimeZone defaultTimeZone=TimeZone.getDefault();
		return System.currentTimeMillis()+(servertTimeZone.getRawOffset()-defaultTimeZone.getRawOffset());
	}
	public static Date getServerDate(String serverTimeZoneStr){
		TimeZone servertTimeZone=TimeZone.getTimeZone(serverTimeZoneStr);
		TimeZone defaultTimeZone=TimeZone.getDefault();
		Date date=new Date(System.currentTimeMillis()+(servertTimeZone.getRawOffset()-defaultTimeZone.getRawOffset()));
		return date;
	}


	public static Date getUTimeDate(long milliSeconds){
		Date date=new Date(milliSeconds);
		return date;
	}
	public static Calendar getCalendar(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public static Calendar getCalendar(long milliSeconds){
		Date date=new Date(milliSeconds);
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	public static String getUTimeStr(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	public static String getUTimeStr(long milliSeconds) {
		Date date=new Date(milliSeconds);
		SimpleDateFormat userSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return userSdf.format(date);
	}
	public static String getUTimeStr(long milliSeconds, String timeZoneStr) {
		TimeZone timeZone=TimeZone.getTimeZone(timeZoneStr);
		Date date=new Date(milliSeconds);
		long chineseMills = date.getTime() + timeZone.getRawOffset();
		Date chineseDateTime = new Date(chineseMills);
		SimpleDateFormat userSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return userSdf.format(chineseDateTime);
	}

	public static long getUserDateLong(String formattext,String sources,long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat(formattext);
		try {
			Date date = formatter.parse(sources);
			return date.getTime()+milliSeconds;
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public static String getUserDateStr(String formattext,String sources,String timeZoneStr,long milliSeconds) {
		SimpleDateFormat formatter = new SimpleDateFormat(formattext);
		try {
			Date date = formatter.parse(sources);
			TimeZone timeZone=TimeZone.getTimeZone(timeZoneStr);
			long chineseMills = date.getTime() + timeZone.getRawOffset()+milliSeconds;
			Date chineseDateTime = new Date(chineseMills);
			Calendar cal=Calendar.getInstance(timeZone);
			cal.setTime(chineseDateTime);
			return getStringDate("MM-dd HH:mm",cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String getStringDate(String formattext,Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(formattext);
		String dateString = formatter.format(date);
		return dateString;
	}
	public static long getStringDate(String formattext,String dateStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(formattext);
		try {
			Date date = formatter.parse(dateStr);
			return date.getTime();
		} catch (ParseException e) {
			return 0l;
		}

	}

	public static Date getDateByDateStr(String formattext,String dateStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(formattext);
		try {
			Date date = formatter.parse(dateStr);
			return date;
		} catch (ParseException e) {
			return null;
		}

	}




	/*
	时分秒转换
	 */
	public static String formatTime(long milliseconds,String[] timeStrs){
		String time="";
		long hour=milliseconds/3600000;
		long minute=0;
		long second=0;
		if(hour==milliseconds){
			hour=0;
			minute=milliseconds/60000;
			second=(milliseconds%60000)/1000;
		}else{
			minute=(milliseconds%3600000)/60000;
			second=((milliseconds%3600000)%60000)/1000;
		}
		if(hour!=0){
			time+=(hour+" "+timeStrs[0]);
		}
		time+=(minute+" "+timeStrs[1]);
		time+=(second+" "+timeStrs[2]);
		return  time;

	}

	public static String formatTime(long milliseconds){
		String time="";
		long hour=milliseconds/3600000;
		long minute=0;
		long second=0;
		String mHour="";
		String mInute="";
		String mSecond="";
		if(hour==milliseconds){
			hour=0;
			minute=milliseconds/60000;
			second=(milliseconds%60000)/1000;
		}else{
			minute=(milliseconds%3600000)/60000;
			second=((milliseconds%3600000)%60000)/1000;
		}
		if(hour<10){
			mHour="0"+hour;
		}else{
			mHour=""+hour;
		}
		if(minute<10){
			mInute="0"+minute;
		}else{
			mInute=""+minute;
		}
		if(second<10){
			mSecond="0"+second;
		}else{
			mSecond=""+second;
		}
		time+=(mHour+":");
		time+=(mInute+":");
		time+=(mSecond);
		return  time;

	}
}
