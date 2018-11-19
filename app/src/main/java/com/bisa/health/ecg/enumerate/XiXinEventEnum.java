package com.bisa.health.ecg.enumerate;

/**
 * Created by Administrator on 2018/10/26.
 */

public enum XiXinEventEnum {

    APP(1000),DEVICE(1001),PASSIVE(1002);

    private int value;

    XiXinEventEnum(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }

    public static XiXinEventEnum getByValue(int value) {
        for (XiXinEventEnum status : values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
}
