package com.bisa.health.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportUtils {
	public final static String DATETIME_FORMAT="yyyyMMddHHmmss";
	
	public static Date getReportFileTimeToDate(String filename){
		
		String[] b1=splitReportFileName(filename);
		if(b1==null)
			return null;
		
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(DATETIME_FORMAT);
			Date date=sdf.parse(b1[1]);
			return new Date(date.getTime());
			
		} catch (Exception e) {
			return null;
		}
		
	}
	
	public static String[] splitReportFileName(String filename){
		String[] a1=filename.split("_");
		if(a1.length!=2){
			return null;
		}
	
		return a1;
	}
	
}
