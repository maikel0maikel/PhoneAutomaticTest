package com.sinohb.hardware.test.module.wifi;


import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface WifiPresenter {

    public interface View extends BaseView<Controller>{
        //Context getViewContext();
    }

    public interface Controller extends BasePresenter{

        int openWifi();

        int closeWifi();

        int startScan();

        int connectWifi();

    }


}
