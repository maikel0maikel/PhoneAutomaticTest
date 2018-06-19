package com.sinohb.hardware.test.module.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.GPSConstants;

public class GPSController implements GPSPresenter.Controller {

    private GPSManagerable gpsManager;
    private GPSPresenter.View mView;
    private GpsStateReceiver mReceiver;

    public GPSController(GPSPresenter.View view) {
        this.mView = view;
        this.mView.setPresenter(this);
        gpsManager = new GPSManager();
        registGPSReceiver();
    }

    private void registGPSReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GpsStateReceiver.GPS_ACTION);
        mReceiver = new GpsStateReceiver();
        HardwareTestApplication.getContext().registerReceiver(mReceiver, filter);
    }

    private void unRegistGPSReceiver() {
        if (mReceiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public int openGPS() {
        return gpsManager == null ? GPSConstants.DEVICE_NOT_SUPPORT : gpsManager.openGPS();
    }

    @Override
    public int closeGPS() {
        return gpsManager == null ? GPSConstants.DEVICE_NOT_SUPPORT : gpsManager.closeGPS();
    }

    @Override
    public int startLocate() {
        return gpsManager == null ? GPSConstants.DEVICE_NOT_SUPPORT : gpsManager.startLocate();
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void destroy() {
        unRegistGPSReceiver();
    }

    class GpsStateReceiver extends BroadcastReceiver {
        private static final String GPS_ACTION = "android.location.PROVIDERS_CHANGED";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (GPS_ACTION.matches(intent.getAction())) {

            }
        }
    }
}
