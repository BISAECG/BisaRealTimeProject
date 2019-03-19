package com.bisa.health.ecg.config;

public interface ECGViewConfig {
    //通道数量
    int CHCOUNT=3;

    int RATE=250;//采样率
    int ECGDefaultOneSecond=25;//1秒25个小格
    int ECGDefaultMaxPoint=1000;//ecg最大点数
    int DrawDefaultScale=8;//画4个刻度
    int ECGMidNumber=ECGDefaultMaxPoint/2;//ecg中间数
}
