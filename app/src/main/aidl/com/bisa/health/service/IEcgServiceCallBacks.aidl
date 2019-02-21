package com.bisa.health.service;

 interface IEcgServiceCallBacks{ 
  void uiNewRssiAvailable(int Rssi);
  void ecgDrawing(in int[] value);
  void uiCallNotifiConnEvent(int mStatus);
  void uiCallNotifiAlam(int mStatus,String msg);
  void uiUpdateDeviceBattert(int battert);
  void uiUpdateNetWork(int status);
}