package com.bisa.health.ecg.dao;

import com.bisa.health.ecg.model.AppReport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/24.
 */

public interface IReportDao {
    public AppReport add(AppReport appReport);
    public List<AppReport> loadByUserGuid(int userGuid);
    public ArrayList<AppReport> loadByUserGuidAndStatus(int userGuid,int reportType);
    public AppReport loadByGuidAndStatus(int userGuid, int status);
    public AppReport loadByGuidAndNumber(int userGuid, String number);
    public AppReport loadByGuidAndStatus(int userGuid, int status,int reportType);
    public int loadByAllCount(int userGuid);
    public AppReport update(AppReport appReport);
    public AppReport updateOrSave(AppReport appReport);
    public void updateOrSave(List<AppReport> reportList);
    public boolean del(int user_guid,String number);
    public AppReport updateStatus(AppReport appReport);
    public List<AppReport> loadByCalendarData(int user_guid,Date subtime);
    public  List<AppReport> loadByCalendarData(int year, int month,int user_guid,int rTyep);
    public List<AppReport> loadReportList(int user_guid,int year,int month,int day,String order,int size,int offset);
    public int loadCountByDay(int userGuid,int year,int month,int day);
}
