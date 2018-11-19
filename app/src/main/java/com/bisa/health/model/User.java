package com.bisa.health.model;


import com.bisa.health.model.enumerate.SexTypeEnum;
import com.bisa.health.model.enumerate.ShapeTypeEnum;
import com.bisa.health.model.enumerate.SportTypeEnum;
import com.bisa.health.model.enumerate.UsersActivateEnum;
import com.bisa.health.model.enumerate.UsersVerifiedEnum;

import java.io.Serializable;

public class User implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int user_guid;	 //
	private String username;
	private String name;    //
	private int	height;    //
	private int	weight;    //
	private SexTypeEnum sex;    //
	private int	age;    //
	private SportTypeEnum sport_type;    //
	private ShapeTypeEnum shape_type;    //
	private String uri_pic;
	private String birthday;    //
	private String area_code;    //
	private UsersActivateEnum activate;//是否添加了个人资料
	private UsersVerifiedEnum verified;

	public int getUser_guid() {
		return user_guid;
	}
	public void setUser_guid(int user_guid) {
		this.user_guid = user_guid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public SexTypeEnum getSex() {
		return sex;
	}
	public void setSex(SexTypeEnum sex) {
		this.sex = sex;
	}
	public SportTypeEnum getSport_type() {
		return sport_type;
	}
	public void setSport_type(SportTypeEnum sport_type) {
		this.sport_type = sport_type;
	}
	public ShapeTypeEnum getShape_type() {
		return shape_type;
	}
	public void setShape_type(ShapeTypeEnum shape_type) {
		this.shape_type = shape_type;
	}
	public String getUri_pic() {
		return uri_pic;
	}
	public void setUri_pic(String uri_pic) {
		this.uri_pic = uri_pic;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public UsersVerifiedEnum getVerified() {
		return verified;
	}
	public void setVerified(UsersVerifiedEnum verified) {
		this.verified = verified;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public UsersActivateEnum getActivate() {
		return activate;
	}

	public void setActivate(UsersActivateEnum activate) {
		this.activate = activate;
	}

	public User() {
		super();
	}

	@Override
	public String toString() {
		return "User{" +
				"user_guid=" + user_guid +
				", username='" + username + '\'' +
				", name='" + name + '\'' +
				", height=" + height +
				", weight=" + weight +
				", sex=" + sex +
				", age=" + age +
				", sport_type=" + sport_type +
				", shape_type=" + shape_type +
				", uri_pic='" + uri_pic + '\'' +
				", birthday='" + birthday + '\'' +
				", area_code='" + area_code + '\'' +
				", activate=" + activate +
				", verified=" + verified +
				'}';
	}
}
