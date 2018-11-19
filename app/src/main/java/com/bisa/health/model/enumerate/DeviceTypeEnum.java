package com.bisa.health.model.enumerate;

/**
 * Created by Administrator on 2018/4/26.
 */

public enum DeviceTypeEnum {
     ECG(0),CAMERA(1),BP(2);


    DeviceTypeEnum(int index) {
        this.index = index;
    }

    private int index;

    public int vlaueOf(){
        return index;
    }
}
