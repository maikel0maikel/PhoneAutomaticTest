package com.sinohb.hardware.test.module.usb;

import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;

public interface USBPresenter {
    interface View extends BaseExecuteView {

        void notifyStoregeStatus(int state);
    }


    interface Controller extends BaseExecutePresenter{
        void setDirPath();

        void notifyStatus(int state);
    }
}
