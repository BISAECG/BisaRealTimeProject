package com.bisa.health.rest.service;

import com.bisa.health.rest.ICallDownInterface;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;

public interface IRestService {

	public Call sendCodeByUser(Map<String,String> param);
	public Call sendCodeByIphone(Map<String,String> param);
	public Call loginPwd(FormBody param);
	public Call login(FormBody param);
	public Call wxLogin(FormBody param);
	public Call updateInfo(MultipartBody param);
	public Call downHeadIco(String uri);
	public Call addContact(FormBody param);
	public Call appDeleteContact(FormBody param);
	public Call sendSOS(Map<String,String> param);
	public Call generateReport(FormBody param);
	public Call downServerList(Map<String,String> param);
	public Call iphoneRegedit(FormBody param);
	public Call findAccount(Map<String, String> param);
	public Call findPassword(FormBody param);
	public Call getContacts();
	public Call getAccount();
	public Call modifyPwd(FormBody param);
	public Call otherbind(FormBody param);
	public Call bindAccount(FormBody param);
	public Call bindOther(FormBody param);
	public Call varifyBindToken(FormBody param);
	public Call initDat();
	public Call syncVersion(Map<String, String> param);
	public void downapp(File file, Map<String, String> param, ICallDownInterface callDownInterface);
}
