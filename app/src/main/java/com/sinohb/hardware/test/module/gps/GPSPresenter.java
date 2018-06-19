package com.sinohb.hardware.test.module.gps;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface GPSPresenter {

    interface View extends BaseView<Controller> {

    }

    interface Controller extends BasePresenter {
        int openGPS();
        int closeGPS();
        int startLocate();
    }
}
