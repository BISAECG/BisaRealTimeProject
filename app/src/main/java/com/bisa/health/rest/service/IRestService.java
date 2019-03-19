package com.bisa.health.rest.service;

import com.bisa.health.rest.ICallDownInterface;

import java.io.File;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;

 public interface IRestService {

 	 Call updateUsername(FormBody param);
	 Call sendCodeByUser(Map<String,String> param);
	 Call sendCodeByIphone(Map<String,String> param);
	 Call loginPwd(FormBody param);
	 Call login(FormBody param);
	 Call wxLogin(FormBody param);
	 Call updateInfo(MultipartBody param);
	 Call getUserInfo();
	 Call downHeadIco(String uri);
	 Call addContact(FormBody param);
	 Call appDeleteContact(FormBody param);
	 Call sendSOS(Map<String,String> param);
	 Call generateReport(FormBody param);
	 Call downServerList(Map<String,String> param);
	 Call iphoneRegedit(FormBody param);
	 Call findAccount(Map<String, String> param);
	 Call findPassword(FormBody param);
	 Call getContacts();
	 Call getAccount();
	 Call modifyPwd(FormBody param);
	 Call otherbind(FormBody param);
	 Call bindAccount(FormBody param);
	 Call bindOther(FormBody param);
	 Call varifyBindToken(FormBody param);
	 Call initDat();
	 Call syncVersion(Map<String, String> param);
	 void downapp(File file, Map<String, String> param, ICallDownInterface callDownInterface);
}
