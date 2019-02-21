package com.bisa.health.ecg.config;

public interface ECGViewConfig {
    //通道数量
    public static final int CHCOUNT=3;

    public static final int RATE=250;//采样率
    public static final int ECGDefaultOneSecond=25;//1秒25个小格
    public static final int ECGDefaultMaxPoint=1000;//ecg最大点数
    public static final int DrawDefaultScale=8;//画4个刻度
    public static final int ECGMidNumber=ECGDefaultMaxPoint/2;//ecg中间数
}
