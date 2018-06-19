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
import com.sinohb.hardware.test.constant.GPSConstants;
import com.sinohb.logger.LogTools;

import java.util.Iterator;

public class GPSManager implements GPSManagerable {
    private static final String TAG = "GPSManager";
    private LocationManager mManager;
    private boolean mGPSState;


    GPSManager() {
        mManager = (LocationManager) HardwareTestApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        mGPSState = isGPSEnable();
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
        if (mManager == null){
            return GPSConstants.DEVICE_NOT_SUPPORT;
        }
        return GPSConstants.DEVICE_SUPPORTED;

    }

    @Override
    public void destroy() {
        if (!mGPSState){
            Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
        }
    }

    private int realOpenGps() {
        if (mManager == null){
            return GPSConstants.DEVICE_NOT_SUPPORT;
        }
        if (!isGPSEnable()) {
            Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, true);
            return GPSConstants.DEVICE_SUPPORTED;
        }
        return GPSConstants.DEVICE_STATE_ERROR;
    }

    private int realCloseGps() {
        if (mManager == null){
            return GPSConstants.DEVICE_NOT_SUPPORT;
        }
        if (isGPSEnable()) {
            Settings.Secure.setLocationProviderEnabled(HardwareTestApplication.getContext().getContentResolver(), LocationManager.GPS_PROVIDER, false);
            return GPSConstants.DEVICE_SUPPORTED;
        }
        return GPSConstants.DEVICE_STATE_ERROR;
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LogTools.p(TAG, "时间：" + location.getTime());
            LogTools.p(TAG, "经度：" + location.getLongitude());
            LogTools.p(TAG, "纬度：" + location.getLatitude());
            LogTools.p(TAG, "海拔：" + location.getAltitude());
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

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位  
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    LogTools.p(TAG, "第一次定位");
                    break;
                // 卫星状态改变  
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    LogTools.p(TAG, "卫星状态改变");
                    // 获取当前状态  
                    GpsStatus gpsStatus = mManager.getGpsStatus(null);
                    // 获取卫星颗数的默认最大值  
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    // 创建一个迭代器保存所有卫星  
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    LogTools.p(TAG, "搜索到：" + count + "颗卫星");
                    break;
                // 定位启动  
                case GpsStatus.GPS_EVENT_STARTED:
                    LogTools.p(TAG, "定位启动");
                    break;
                // 定位结束  
                case GpsStatus.GPS_EVENT_STOPPED:
                    LogTools.p(TAG, "定位结束");
                    break;
            }
        }
    };


}
