package com.sinohb.hardware.test.module.gps;

import android.location.Location;

public interface GPSManagerable {
    boolean isGPSEnable();
    int openGPS();
    int closeGPS();
    int startLocate();
    int stopLocate();
    void destroy();

    interface GpsChangeListener{
       void onLocationChanged(Location location,int Satellites);
       void onGpsLocateStart();
       void onGpsLocateStop();
    }
}
