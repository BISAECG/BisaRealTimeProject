package com.bisa.health.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bisa.health.utils.DateUtil.getGMTDateTime;

public class FunUtil {




    /**
     * 获取URL的图片地址
     * @param url
     * @return
     */
    public static String buildToHeadFileName(String url){
        String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
        Pattern pat= Pattern.compile("[\\w]+[\\.]("+suffixes+")");//正则判断
        Matcher mc=pat.matcher(url);//条件匹配
        while(mc.find()){
            String substring = mc.group();//截取文件名后缀名
            Log.i("----",substring);
            return substring;
        }
        return null;
    }


	/***
	 * 通过文件的时间来转换年月日
	 * 20160101010107->2016-01-01
	 * @param dateTimeStr
	 * @return
	 */
	public static String BuildYMD(String dateTimeStr){

		String year=dateTimeStr.substring(0,4);
		String method=dateTimeStr.substring(4,6);
		String day=dateTimeStr.substring(6,8);
		return year+"/"+method+"/"+day;

	}

	public static int BuildYMDIndex(String dateTimeStr){

		String year=dateTimeStr.substring(0,4);
		String method=dateTimeStr.substring(4,6);
		String day=dateTimeStr.substring(6,8);
		return Integer.parseInt(year+method+day);

	}

	public static File createDefaultFile(String path, String guid, String deviceName,String suffix) {
		String fileName = (guid != null ? guid + "-" : "") + deviceName + "_" + getGMTDateTime("yyyyMMddkkmmss")
				+ "."+suffix;

		File file = new File(path,fileName);// //update
		return file;
	}

	public static File createDefaultFile(String path, String guid, String deviceName,String suffix,Long curTime) {

		String fileName = (guid != null ? guid + "-" : "") + deviceName + "_" + getGMTDateTime("yyyyMMddkkmmss",curTime)
				+ "."+suffix;
		File file = new File(path,fileName);// //update
		return file;
	}
	public static String createDefaultFile( String guid, String deviceName,String suffix,Long curTime) {

		String fileName = (guid != null ? guid + "-" : "") + deviceName + "_" + getGMTDateTime("yyyyMMddkkmmss",curTime)
				+ "."+suffix;
		return fileName;
	}

    // 创建GPRS文件
    public static File createGSFile(String path, String guid) {

        String fileName = guid + "_" + getGMTDateTime("yyyyMMddkkmmss")
                + ".GS";
        File file = new File(path,fileName);// //update
        return file;
    }


	public static String toJsonStr(final InputStream inputStream) throws IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		// 数组长度
		byte[] buffer = new byte[1024];
		// 初始长度
		int len = 0;
		// 循环
		while ((len = inputStream.read(buffer)) != -1) {
			arrayOutputStream.write(buffer, 0, len);
		}

		return arrayOutputStream.toString();
	}

	public static String[] ecgNameSpilt(String ecgName) {

		String[] ecgNameArray = new String[3];

			String[] ecgNameSprit = ecgName.split("_");
			if (ecgNameSprit.length >= 2) {
				ecgNameArray[0]=ecgNameSprit[0];
				String[] ecgNamePoint = ecgNameSprit[ecgNameSprit.length-1].split("\\.");

				if(ecgNamePoint.length==2) {
					ecgNameArray[1] = ecgNamePoint[0];// 时间
					ecgNameArray[2] = ecgNamePoint[1];// 后缀名
					return ecgNameArray;
				}

			}


		return null;
	}

	public static String h5Lang(){
		String lan = Locale.getDefault().getLanguage();
		String country = Locale.getDefault().getCountry();
		String lang = lan + "_" + country;
		return lang;
	}

}
