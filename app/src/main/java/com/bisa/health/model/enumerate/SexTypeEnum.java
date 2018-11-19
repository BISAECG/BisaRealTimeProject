package com.bisa.health.model.enumerate;

public enum SexTypeEnum{
	MALE(0),FEMALE(1);
	private int value;

	private SexTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public static SexTypeEnum getByValue(int value){
	      for (SexTypeEnum sexType : values()) {  
	            if (sexType.getValue() == value) {  
	                return sexType;  
	            }  
	        }  
	        return null;  
	}
	
}
