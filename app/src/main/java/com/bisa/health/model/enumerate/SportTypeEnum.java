package com.bisa.health.model.enumerate;

public enum SportTypeEnum {
	NEVER(1),LESS(2),OFFEN(3);
	
	private int value;

	private SportTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static SportTypeEnum getByValue(int value){
	      for (SportTypeEnum sportType : values()) {  
	            if (sportType.getValue() == value) {  
	                return sportType;  
	            }  
	        }  
	        return null;  
	}
	
}
