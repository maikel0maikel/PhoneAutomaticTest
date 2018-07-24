package com.sinohb.hardware.test.module.wifi;


import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;

public interface WifiPresenter {

    public interface View extends BaseExecuteView {
        //Context getViewContext();
        void notifyOpenOrCloseState(int state);
    }

    public interface Controller extends BaseExecutePresenter{

        int openWifi();

        int closeWifi();

        int startScan();

        int connectWifi(String ssid,String pwd);

    }


}
