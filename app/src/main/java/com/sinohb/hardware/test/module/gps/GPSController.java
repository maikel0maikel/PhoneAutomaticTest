package com.sinohb.hardware.test.module.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.util.concurrent.FutureTask;

public class GPSController implements GPSPresenter.Controller, GPSManagerable.GpsChangeListener {
    private static final String TAG = "GPSController";
    private GPSManagerable gpsManager;
    private GPSPresenter.View mView;
    private GpsStateReceiver mReceiver;
    private GPSTask gpsTask;
    public GPSController(GPSPresenter.View view) {
        gpsManager = new GPSManager(this);
        registGPSReceiver();
        this.mView = view;
        this.mView.setPresenter(this);
    }

    private void registGPSReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        mReceiver = new GpsStateReceiver();
        HardwareTestApplication.getContext().registerReceiver(mReceiver, filter);
//        HardwareTestApplication.getContext().getContentResolver()
//                .registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED),
//                        false, mGpsMonitor);
    }

//    private final ContentObserver mGpsMonitor = new ContentObserver(null) {
//
//        @Override
//
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            notifyGpsState();
//        }
//
//    };

    private void notifyGpsState() {
        boolean enable = gpsManager.isGPSEnable();
        if (enable) {
            gpsTask.notifyGPSOpened();
        } else {
            gpsTask.notifyGPSClosed();
        }
    }

    private void unRegistGPSReceiver() {
        if (mReceiver!=null){
            HardwareTestApplication.getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
       // HardwareTestApplication.getContext().getContentResolver().unregisterContentObserver(mGpsMonitor);
    }

    @Override
    public int openGPS() {
        return gpsManager == null ? Constants.DEVICE_NOT_SUPPORT : gpsManager.openGPS();
    }

    @Override
    public int closeGPS() {
        return gpsManager == null ? Constants.DEVICE_NOT_SUPPORT : gpsManager.closeGPS();
    }

    @Override
    public int startLocate() {
        return gpsManager == null ? Constants.DEVICE_NOT_SUPPORT : gpsManager.startLocate();
    }

    @Override
    public void start() {
        gpsTask = new GPSTask(this);
        FutureTask futureTask = new FutureTask(gpsTask);
        ThreadPool.getPool().execute(futureTask);
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
        gpsManager.destroy();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    class GpsStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            LogTools.p(TAG, "intent.getAction():" + intent.getAction());
            if ( LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                notifyGpsState();
            }
        }
    }
}
