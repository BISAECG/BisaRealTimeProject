package com.bisa.health.ecg.model;


public enum ReportStatus {
	//0-待上传数据 1-未被查看 2-已被查看  3-报告生成中 5-已上传数据 6-失效报告
	NEED_DATA(0),
	UNREAD(1),
	READ(2),
	GENERATED(3),
	HAS_DATA(5),
	INVALID(6),
	WRONGDATA(7);
	
	private int value;
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}

	private ReportStatus(int value) {
		this.value = value;
	}
	
	public static ReportStatus getByValue(int value){
		
		
		for(ReportStatus rs : values()){
			if(rs.getValue() == value){
				return rs;
			}
		}
		return null;
	}
	public static ReportStatus getByName(String name) {
		for (ReportStatus rs : values()) {
			if (rs.name().equals(name)){
				return rs;
			}
		}
		return null;
	}

}
