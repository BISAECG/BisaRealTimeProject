package com.bisa.health.model.enumerate;

/**
 * Created by Administrator on 2018/4/25.
 */

public enum LoginEnum {

    IPHONE(1),MAIL(3),WECHAT(2),WEIBO(4);

    LoginEnum(int index) {
        this.index = index;
    }

    private int index;

    public int valueOf(){
        return this.index;
    }
}
