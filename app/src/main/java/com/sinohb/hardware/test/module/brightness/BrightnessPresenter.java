package com.sinohb.hardware.test.module.brightness;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface BrightnessPresenter {

//    interface View extends BaseDisplayViewView  {
//
//        void changeLow();
//
//        void changeMedium();
//
//        void changeHigh();
//    }

    interface Controller extends BaseDisplayViewPresenter{

        void changeLow();

        void changeMedium();

        void changeHigh();
    }
}
