package com.sinohb.hardware.test.module.gps;

import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;

public interface GPSPresenter {

    interface View extends BaseExecuteView {

        void startLocate();

        void stopLocate();

        void notifyGPSState(int state);

        void notifyGpsStartLocate();

        void notifyGpsStopLocate();

        void notifyLocateResult(double lat,double lon,int st);

    }

    interface Controller extends BaseExecutePresenter {
        int openGPS();

        int closeGPS();

        int startLocate();

        int stopLocate();

        boolean isEnable();

        void startLocateInMain();

        void stopLocateInMain();

        void notifyGPSState(int state);
    }
}
