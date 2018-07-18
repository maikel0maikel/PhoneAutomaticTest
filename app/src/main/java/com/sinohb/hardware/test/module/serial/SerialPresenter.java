package com.sinohb.hardware.test.module.serial;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.app.BaseExecutePresenter;

public interface SerialPresenter extends BaseExecutePresenter{

    interface View extends BaseDisplayViewView{
        void notifyVersion(String version);
    }

    interface Controller extends BaseExecutePresenter{
        void getVersion();

       // void notifyResult(String result);
    }

}
