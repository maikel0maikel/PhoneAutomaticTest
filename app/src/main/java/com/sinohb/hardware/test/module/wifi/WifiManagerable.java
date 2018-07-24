package com.sinohb.hardware.test.module.wifi;

public interface WifiManagerable {

    int openWifi();

    int closeWifi();

    int startScan();

    int connectWifi(String ssid,String pwd);
}
