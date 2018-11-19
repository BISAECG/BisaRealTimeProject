package com.bisa.health.utils;

import com.bisa.health.model.dto.ServerDto;

import java.util.List;

public class AreaUtil {

	public  static ServerDto CountryCodeByPhoneCode(List<ServerDto> list, String phoneCode){
		for(ServerDto serverBisa :list){
			if(Integer.parseInt(serverBisa.getPhoneCode())==Integer.parseInt(phoneCode)){
				return serverBisa;
			}
		}
		return null;
	}

}
