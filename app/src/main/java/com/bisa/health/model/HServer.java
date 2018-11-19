package com.bisa.health.model;

import com.bisa.health.model.dto.ServerDto;

import java.util.List;

/**
 * Created by Administrator on 2018/7/30.
 */

public class HServer {

    private String version="20171111";
    private List<ServerDto> list;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public List<ServerDto> getList() {
        return list;
    }
    public void setList(List<ServerDto> list) {
        this.list = list;
    }

    public ServerDto convertToServerByCountryCode(String codeid){
        for(ServerDto server : list){
            if(server.getCountryCode().equals(codeid)){
                return server;
            }
        }
        return null;
    }

    public ServerDto convertToServerByIphoneCode(String codeid){
        for(ServerDto server : list){
            if(server.getPhoneCode().equals(codeid)){
                return server;
            }
        }
        return null;
    }

}
