package com.bisa.health.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class UserPwdDto implements Serializable{
    private int user_guid;
    private List<UserBindDto> userForPwdDto;

    public int getUser_guid() {
        return user_guid;
    }

    public void setUser_guid(int user_guid) {
        this.user_guid = user_guid;
    }

    public List<UserBindDto> getUserBindDto() {
        return userForPwdDto;
    }

    public void setUserBindDto(List<UserBindDto> userForPwdDto) {
        this.userForPwdDto = userForPwdDto;
    }

    public UserPwdDto(int user_guid, List<UserBindDto> userForPwdDto) {
        this.user_guid = user_guid;
        this.userForPwdDto = userForPwdDto;
    }

    @Override
    public String toString() {
        return "UserPwdDto{" +
                "user_guid=" + user_guid +
                ", userForPwdDto=" + userForPwdDto +
                '}';
    }
}
