package com.bisa.health.model;


import com.bisa.health.model.enumerate.SexTypeEnum;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int user_guid; //
	private String nickname;
	private String username;
	private String phonecode;
	private int verified;
	private SexTypeEnum sex; //
	private int age; //
	private String uri_pic;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getUser_guid() {
		return user_guid;
	}

	public void setUser_guid(int user_guid) {
		this.user_guid = user_guid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhonecode() {
		return phonecode;
	}

	public void setPhonecode(String phonecode) {
		this.phonecode = phonecode;
	}

	public int getVerified() {
		return verified;
	}

	public void setVerified(int verified) {
		this.verified = verified;
	}

	public SexTypeEnum getSex() {
		return sex;
	}

	public void setSex(SexTypeEnum sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getUri_pic() {
		return uri_pic;
	}

	public void setUri_pic(String uri_pic) {
		this.uri_pic = uri_pic;
	}

	@Override
	public String toString() {
		return "User{" +
				"user_guid=" + user_guid +
				", nickname='" + nickname + '\'' +
				", username='" + username + '\'' +
				", phonecode='" + phonecode + '\'' +
				", verified=" + verified +
				", sex=" + sex +
				", age=" + age +
				", uri_pic='" + uri_pic + '\'' +
				'}';
	}
}
