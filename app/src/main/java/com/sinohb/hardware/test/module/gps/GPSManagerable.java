package com.sinohb.hardware.test.module.gps;

public interface GPSManagerable {
    boolean isGPSEnable();
    int openGPS();
    int closeGPS();
    int startLocate();
    void destroy();
}
