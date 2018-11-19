package com.bisa.health.model.enumerate;

public enum ShapeTypeEnum {
	THIN(1),COMMON(2),FAT(3),FATTER(4);
	
	private int value;

	private ShapeTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	
	public static ShapeTypeEnum getByValue(int value){
	      for (ShapeTypeEnum shapeType : values()) {  
	            if (shapeType.getValue() == value) {  
	                return shapeType;  
	            }  
	        }  
	        return null;  
	}
}
