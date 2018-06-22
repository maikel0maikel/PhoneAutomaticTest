package com.sinohb.hardware.test.module.gps;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

import java.util.Iterator;

public class GPSManager implements GPSManagerable {
    private static final String TAG = "GPSManager";
    private LocationManager mManager;
    private boolean mGPSState;
    private boolean isStartGps;
    private boolean isStartNet;
    private GpsChangeListener listener;

    GPSManager(GpsChangeListener listener) {
        mManager = (LocationManager) HardwareTestApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        mGPSState = isGPSEnable();
        this.listener = listener;
    }

    @Override
    public boolean isGPSEnable() {
        boolean gps = mManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    @Override
    public int openGPS() {
        return realOpenGps();
    }

    @Override
    public int closeGPS() {
        return realCloseGps();
    }

    @Override
    public int startLocate() {
        if (mManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        mManager.addGpsStatusListener(gpsStatuslistener);
        startGpsLocate();
        startNetLocate();
        return Constants.DEVICE_SUPPORTED;
    }

    private void startGpsLocate() {
        if (mManager.isProviderEnabled(mManager.GPS_PROVIDER)) {
            isStartGps = true;
            mManager.requestLocationUpdates(mManager.GPS_PROVIDER, 0, 1, gpsLocationListener);
        }
    }

    private void startNetLocate() {
        if (mManager.isProviderEnabled(mManager.GPS_PROVIDER)) {
            isStartNet = true;
            mManager.requestLocationUpdates(mManager.GPS_PROVIDER, 1000l, 1, netLocationListener);
        }
    }

    @Override
    public void destroy() {
        if (!mGPSState) {
            Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
        }
        if (isStartGps) {
            mManager.removeUpdates(gpsLocationListener);
            isStartGps = false;
        }
        if (isStartNet) {
            mManager.removeUpdates(netLocationListener);
            isStartNet = false;
        }
    }

    private int realOpenGps() {
        if (mManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (!isGPSEnable()) {
            //Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, true);
            //Settings.Secure.putInt(HardwareTestApplication.getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_STATE_ERROR;
    }

    private int realCloseGps() {
        if (mManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (isGPSEnable()) {
            //Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
            //Settings.Secure.putInt(HardwareTestApplication.getContext().getContentResolver(), Settings.Secure. LOCATION_PROVIDERS_ALLOWED, Settings.Secure.LOCATION_MODE_OFF);
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_STATE_ERROR;
    }

    private LocationListener gpsLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LogTools.p(TAG, "gpsLocationListener 时间：" + location.getTime());
            LogTools.p(TAG, "gpsLocationListener 经度：" + location.getLongitude());
            LogTools.p(TAG, "gpsLocationListener 纬度：" + location.getLatitude());
            LogTools.p(TAG, "gpsLocationListener 海拔：" + location.getAltitude());
            locateChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    LogTools.p(TAG, "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    LogTools.p(TAG, "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LogTools.p(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            LogTools.p(TAG, "provider:" + provider + " is enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            LogTools.p(TAG, "provider:" + provider + " is Disabled");
        }
    };
    private LocationListener netLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LogTools.p(TAG, "netLocationListener 时间：" + location.getTime());
            LogTools.p(TAG, "netLocationListener 经度：" + location.getLongitude());
            LogTools.p(TAG, "netLocationListener 纬度：" + location.getLatitude());
            LogTools.p(TAG, "netLocationListener 海拔：" + location.getAltitude());
            locateChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    LogTools.p(TAG, "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    LogTools.p(TAG, "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LogTools.p(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            LogTools.p(TAG, "provider:" + provider + " is enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            LogTools.p(TAG, "provider:" + provider + " is Disabled");
        }
    };
    GpsStatus.Listener gpsStatuslistener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    LogTools.p(TAG, "第一次定位");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    LogTools.p(TAG, "卫星状态改变");
                    GpsStatus gpsStatus = mManager.getGpsStatus(null);
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    LogTools.p(TAG, "搜索到：" + count + "颗卫星");
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    LogTools.p(TAG, "定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    LogTools.p(TAG, "定位结束");
                    break;
            }
        }
    };

    private void locateChanged(Location location) {
        if (listener != null) {
            listener.onLocationChanged(location);
        }
    }

}
