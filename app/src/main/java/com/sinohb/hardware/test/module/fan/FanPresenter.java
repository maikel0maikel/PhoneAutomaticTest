package com.sinohb.hardware.test.module.fan;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface FanPresenter {


    interface View extends BaseDisplayViewView{
        void notifyTest();
        void notifyTurnOn();
        void notifyTurnOff();
    }

    interface Controller extends BaseDisplayViewPresenter{

       void turnOnFan();

       void turnOffFan();

        void notifyTest();
    }

}
