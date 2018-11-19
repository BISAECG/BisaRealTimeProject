package com.bisa.health.model.enumerate;

public enum UsersVerifiedEnum {
	INVERIFIED(0),VERIFIED(1);
	private int value;

	public int getValue() {
		return value;
	}

	private UsersVerifiedEnum(int value) {
		this.value = value;
	}
	
	public static UsersVerifiedEnum getByValue(int value){
	      for (UsersVerifiedEnum auction : values()) {  
	            if (auction.getValue() == value) {  
	                return auction;  
	            }  
	        }  
	        return null;  
	}
}
