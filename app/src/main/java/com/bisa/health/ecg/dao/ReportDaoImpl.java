package com.bisa.health.ecg.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.bisa.health.ecg.model.AppReport;
import com.bisa.health.provider.appreport.AppreportContentValues;
import com.bisa.health.provider.appreport.AppreportCursor;
import com.bisa.health.provider.appreport.AppreportSelection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24.
 */

public class ReportDaoImpl implements IReportDao {

    private static final String TAG = "ReportDaoImpl";

    public ReportDaoImpl(Context context) {
        this.context =context;
    }
    private Context context;
    @Override
    public AppReport add(AppReport appReport) {
        AppreportContentValues values=appReport.toUserContentValues();
        Uri uri = context.getContentResolver().insert(values.uri(), values.values());
        if (uri == null) {
            return null;
        }
        return appReport;
    }

    @Override
    public List<AppReport> loadByUserGuid(int userGuid) {

        List<AppReport> listDto=new ArrayList<AppReport>();
        AppreportSelection where =new AppreportSelection();
        where.userGuid(userGuid);
        AppreportCursor appreportCursor=where.query(context);
        while(appreportCursor.moveToNext()){
            AppReport appReport=new AppReport().toAppReport(appreportCursor);
            listDto.add(appReport);
        }

        appreportCursor.close();
        return listDto;

    }



    @Override
    public ArrayList<AppReport> loadByUserGuidAndStatus(int userGuid, int reportType) {
        ArrayList<AppReport> listDto=new ArrayList<AppReport>();
        AppreportSelection where =new AppreportSelection();
        where.userGuid(userGuid).and().reportType(reportType);
        AppreportCursor appreportCursor=where.query(context);
        while(appreportCursor.moveToNext()){
            AppReport appReport=new AppReport().toAppReport(appreportCursor);
            listDto.add(appReport);

        }
        appreportCursor.close();
        return listDto;
    }

    @Override
    public AppReport loadByGuidAndStatus(int userGuid, int status) {

        AppreportSelection where =new AppreportSelection();
        where.userGuid(userGuid).and().reportStatus(status);
        AppreportCursor appreportCursor=where.query(context);
        if( appreportCursor.moveToFirst()){
            AppReport appReport=new AppReport().toAppReport(appreportCursor);

            appreportCursor.close();
            return appReport;
        }else{
            return null;
        }


    }

    @Override
    public AppReport loadByGuidAndNumber(int userGuid, String number) {
        AppreportSelection where =new AppreportSelection();
        where.userGuid(userGuid).and().reportNumber(number);
        AppreportCursor appreportCursor=where.query(context);
        if( appreportCursor.moveToFirst()){
            AppReport appReport=new AppReport().toAppReport(appreportCursor);
            appreportCursor.close();
            return appReport;
        }else{
            return null;
        }

    }

    @Override
    public AppReport loadByGuidAndStatus(int userGuid, int status, int reportType) {

        AppreportSelection where =new AppreportSelection();
        where.userGuid(userGuid).and().reportStatus(status).and().reportType(reportType);
        AppreportCursor appreportCursor=where.query(context);
        if( appreportCursor.moveToFirst()){
            AppReport appReport=new AppReport().toAppReport(appreportCursor);
            appreportCursor.close();
            return appReport;
        }else{
            return null;
        }

    }

    @Override
    public int loadByAllCount(int userGuid) {

        AppreportSelection where=new AppreportSelection();
        where.userGuid(userGuid);
        AppreportCursor appreportCursor=where.query(context);
        int count=appreportCursor.getCount();
        appreportCursor.close();
        return count;

    }

    @Override
    public AppReport update(AppReport appReport) {

        AppreportSelection where = new AppreportSelection();
        where.userGuid(appReport.getUser_guid()).and().reportNumber(appReport.getReport_number());
        AppreportContentValues values=appReport.toUserContentValues();

        int dbCount = values.update(context, where);
        if (dbCount <= 0) {
            return null;
        }
        return appReport;
    }

    @Override
    public AppReport updateOrSave(AppReport appReport) {
        try {
            AppreportSelection where=new AppreportSelection();
            where.userGuid(appReport.getUser_guid()).and().reportNumber(appReport.getReport_number());
            AppreportCursor appreportCursor= where.query(context);
            if(appreportCursor.getCount()>0){
                update(appReport);
            }else{
                add(appReport);
            }
            appreportCursor.close();
            return appReport;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void updateOrSave(List<AppReport> reportList) {

        Log.i("----","DB reportList:"+reportList.size());

        for(AppReport report :reportList){
            updateOrSave(report);
        }
    }

    @Override
    public boolean del(int user_guid, String number) {
        try {
            AppreportSelection where=new AppreportSelection();
            where.userGuid(user_guid).and().reportNumber(number);
            context.getContentResolver().delete(where.uri(),where.sel(),where.args());
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public AppReport updateStatus(AppReport appReport) {
        return update(appReport);
    }

    @Override
    public List<AppReport> loadByCalendarData(int user_guid, Date subtime) {
        ArrayList<AppReport> listDto=new ArrayList<AppReport>();
        AppreportSelection where=new AppreportSelection();
        where.userGuid(user_guid).and().startTimeBeforeEq(subtime);
        AppreportCursor cursor = where.query(context);
        while(cursor.moveToNext()){
            AppReport report=new AppReport().toAppReport(cursor);
            listDto.add(report);
        }
        cursor.close();
        return listDto;
    }

    @Override
    public List<AppReport> loadByCalendarData(int year, int month, int user_guid, int rTyep) {


        ArrayList<AppReport> listDto=new ArrayList<AppReport>();
        String[] selectCol={"user_guid","report_number","report_type","report_status","start_time","year","month","day","COUNT(_id) as report_count"};

        AppreportSelection where=new AppreportSelection();
        where.userGuid(user_guid).and().reportType(rTyep).and().year(year).and().month(month).groupBy("day");
        Cursor cursor = context.getContentResolver().query(where.uri(),selectCol,where.sel(),where.args(),null);

        while(cursor.moveToNext()){
            AppReport report=new AppReport();
             String mReport_number=cursor.getString(cursor.getColumnIndex("report_number"));
            int mReport_type=cursor.getInt(cursor.getColumnIndex("report_type"));
            int mReport_status=cursor.getInt(cursor.getColumnIndex("report_status"));
             Long mDateTimeMillSecode=cursor.getLong(cursor.getColumnIndex("start_time"));
            Date mStart_time= new Date(mDateTimeMillSecode);
            int mUser_guid=cursor.getInt(cursor.getColumnIndex("user_guid"));
             int mYear=cursor.getInt(cursor.getColumnIndex("year"));
             int mMonth=cursor.getInt(cursor.getColumnIndex("month"));
             int mDay=cursor.getInt(cursor.getColumnIndex("day"));
             int mReport_count=cursor.getInt(cursor.getColumnIndex("report_count"));
            report.setReport_number(mReport_number);
            report.setReport_type(mReport_type);
            report.setReport_status(mReport_status);
            report.setStart_time(mStart_time);
            report.setUser_guid(mUser_guid);
            report.setYear(mYear);
            report.setMonth(mMonth);
            report.setDay(mDay);
            report.setReport_count(mReport_count);

            listDto.add(report);
        }
        cursor.close();
        return listDto;

    }

    @Override
    public List<AppReport> loadReportList(int user_guid,int year,int month,int day,String order,int size,int offset) {
        ArrayList<AppReport> listDto=new ArrayList<AppReport>();
        String[] selectCol={"user_guid","report_number","report_type","report_status","start_time","year","month","day"};
        AppreportSelection where=new AppreportSelection();
        where.userGuid(user_guid).and().year(year).and().month(month).and().day(day);
        Cursor cursor = context.getContentResolver().query(where.uri(),selectCol,where.sel(),where.args()," start_time "+order+" limit "+size+" offset "+(offset*size));

        while(cursor.moveToNext()){
            AppReport report=new AppReport();
            String mReport_number=cursor.getString(cursor.getColumnIndex("report_number"));
            int mReport_type=cursor.getInt(cursor.getColumnIndex("report_type"));
            int mReport_status=cursor.getInt(cursor.getColumnIndex("report_status"));
            Long mDateTimeMillSecode=cursor.getLong(cursor.getColumnIndex("start_time"));
            Date mStart_time= new Date(mDateTimeMillSecode);
            int mUser_guid=cursor.getInt(cursor.getColumnIndex("user_guid"));
            int mYear=cursor.getInt(cursor.getColumnIndex("year"));
            int mMonth=cursor.getInt(cursor.getColumnIndex("month"));
            int mDay=cursor.getInt(cursor.getColumnIndex("day"));
            report.setReport_number(mReport_number);
            report.setReport_type(mReport_type);
            report.setReport_status(mReport_status);
            report.setStart_time(mStart_time);
            report.setUser_guid(mUser_guid);
            report.setYear(mYear);
            report.setMonth(mMonth);
            report.setDay(mDay);
            report.setReport_count(0);
            listDto.add(report);
        }
        cursor.close();
        return listDto;

    }

    @Override
    public int loadCountByDay(int userGuid, int year, int month, int day) {

        AppreportSelection where=new AppreportSelection();
        where.userGuid(userGuid).and().year(year).and().month(month).and().day(day);
        AppreportCursor appreportCursor=where.query(context);
        int count=appreportCursor.getCount();
        appreportCursor.close();
        return count;
    }
}
