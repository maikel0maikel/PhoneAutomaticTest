package com.sinohb.hardware.test.module.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.logger.LogTools;


public class GPSController extends BaseExecuteController implements GPSPresenter.Controller, GPSManagerable.GpsChangeListener {
    private static final String TAG = "GPSController";
    private GPSManagerable gpsManager;
    private GpsStateReceiver mReceiver;
    private int gpsLocateCount = 0;
    private boolean isStopLocate = false;
    public GPSController(BaseExecuteView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        gpsManager = new GPSManager(this);
        registGPSReceiver();
        task = new GPSTestTask(this);
    }

    private void registGPSReceiver() {
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
            mReceiver = new GpsStateReceiver();
            HardwareTestApplication.getContext().registerReceiver(mReceiver, filter);
        }
//        mGpsMonitor = new GpsContentObserver(null);
//        HardwareTestApplication.getContext().getContentResolver()
//
//                .registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED),
//                        false, mGpsMonitor);
    }

    //private   ContentObserver mGpsMonitor;
//    = new ContentObserver(null) {
//
//        @Override
//
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            notifyGpsState();
//
//        }
//
//    };

    private void notifyGpsState() {
        boolean enable = gpsManager != null && gpsManager.isGPSEnable();
        if (mView != null) {
            if (enable) {
                ((GPSPresenter.View) mView ).notifyGPSState(1);
            } else {
                ((GPSPresenter.View) mView).notifyGPSState(0);
            }
        }

    }

    private void unRegistGPSReceiver() {
        if (mReceiver != null) {
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
        isStopLocate = false;
        return gpsManager == null ? Constants.DEVICE_NOT_SUPPORT : gpsManager.startLocate();
    }

    @Override
    public int stopLocate() {
        isStopLocate = true;
        return gpsManager == null ? Constants.DEVICE_NOT_SUPPORT : gpsManager.stopLocate();
    }

    @Override
    public boolean isEnable() {
        return gpsManager != null && gpsManager.isGPSEnable();
    }

    @Override
    public void startLocateInMain() {
        if (mView != null) {
            ((GPSPresenter.View) mView).startLocate();
        }
    }

    @Override
    public void stopLocateInMain() {
        if (mView != null) {
            ((GPSPresenter.View) mView).stopLocate();
        }
    }

    @Override
    public void notifyGPSState(int state) {
        if (mView != null) {
            ((GPSPresenter.View) mView).notifyGPSState(state);
        }
    }

    @Override
    public void destroy() {
        unRegistGPSReceiver();
        if (gpsManager != null) {
            gpsManager.destroy();
        }
    }

    @Override
    public void onLocationChanged(Location location,int satellites) {
        if (location != null) {
            gpsLocateCount++;
        }
        if (gpsLocateCount >= 5) {
            LogTools.p(TAG,"获取到5个gps点定位成功");
            gpsLocateCount = 0;
            if (task!=null){
                ((GPSTestTask)task).notifyLocateSuccess();
            }
            if (gpsManager!=null){
                gpsManager.stopLocate();
            }
            if(mView!=null){
                ((GPSPresenter.View) mView).notifyLocateResult(location.getLatitude(),location.getLongitude(),satellites);
            }
        }
    }

    @Override
    public void onGpsLocateStart() {
        if (mView!=null){
            ((GPSPresenter.View) mView).notifyGpsStartLocate();
        }
    }

    @Override
    public void onGpsLocateStop() {
        if (mView!=null){
            ((GPSPresenter.View) mView).notifyGpsStopLocate();
        }
    }

    @Override
    public void complete() {
        super.complete();
        if (!isStopLocate){
            stopLocateInMain();
        }
    }

    class GpsStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            LogTools.p(TAG, "intent.getAction():" + intent.getAction());
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
                notifyGpsState();
            }
        }
    }
//    class GpsContentObserver extends ContentObserver{
//
//        /**
//         * Creates a content observer.
//         *
//         * @param handler The handler to run {@link #onChange} on, or null if none.
//         */
//        public GpsContentObserver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            LogTools.p(TAG,"onChange :"+selfChange);
//        }
//    }

}
