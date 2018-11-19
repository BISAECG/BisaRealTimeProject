package com.bisa.health.ecg.model;


public enum ReportType {
	
	ALARM(9,"ALARM"),
	MINUTE15(10,"MINUTE15"),
	HOUR24(11,"HOUR24"), 
	MINUTE(12,"MINUTE");
	
    private int value;  
    private String name;  

    public int getValue() {  
        return value;  
    }  

    public void setValue(int value) {  
        this.value = value;  
    }  

    public String getName() {  
        return name;  
    }  

    public void setName(String name) {  
        this.name = name;  
    }  

    private ReportType(int value, String name) {  
        this.setValue(value);  
        this.setName(name);  
    }  
    
    public static ReportType getByValue(int value) {  
        for (ReportType reportType : values()) {  
            if (reportType.getValue() == value) {  
                return reportType;  
            }  
        }  
        return null;  
    }
    public static ReportType getByName(String name) {
        for (ReportType reportType : values()) {
            if (reportType.name().equals(name)){
                return reportType;
            }
        }
        return null;
    }
}  
