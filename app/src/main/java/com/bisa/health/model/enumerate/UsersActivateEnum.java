package com.bisa.health.model.enumerate;

public enum UsersActivateEnum {
	INACTIVATED(0),ACTIVATE(1);
	private int value;

	private UsersActivateEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public static UsersActivateEnum getByValue(int value){
	      for (UsersActivateEnum auction : values()) {  
	            if (auction.getValue() == value) {  
	                return auction;  
	            }  
	        }  
	        return null;  
	}
}
