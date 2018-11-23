package com.bisa.health.utils;

import android.content.Context;

import com.bisa.health.model.dto.ServerDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class AreaUtil {

	public  static ServerDto CountryCodeByPhoneCode(List<ServerDto> list, String phoneCode){
		for(ServerDto serverBisa :list){
			if(Integer.parseInt(serverBisa.getPhoneCode())==Integer.parseInt(phoneCode)){
				return serverBisa;
			}
		}
		return null;
	}


	public static List<ServerDto> getListArea(Context context){

		Locale locale = Locale.getDefault();
		try {

			InputStream is=context.getAssets().open(locale.getCountry()+"_area.json");
			byte[] bytes=FileIOKit.FromInputStreamToByte(is);
			final Gson gson=new Gson();
			List<ServerDto> listArea=gson.fromJson(new String(bytes,"UTF-8"),new TypeToken<List<ServerDto>>(){}.getType());
			return listArea;
		}catch (IOException e){
			try{
				InputStream is=context.getAssets().open("US_area.json");
				byte[] bytes=FileIOKit.FromInputStreamToByte(is);
				final Gson gson=new Gson();
				List<ServerDto> listArea=gson.fromJson(new String(bytes,"UTF-8"),new TypeToken<List<ServerDto>>(){}.getType());
				return listArea;
			}catch (IOException ioe){

			}
		}
		return null;
	}

}
