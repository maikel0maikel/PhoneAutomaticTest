package com.sinohb.hardware.test.module.brightness;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface BrightnessPresenter {

    interface View extends BaseView<Controller>{

    }

    interface Controller extends BasePresenter{

        void changeLow();

        void changeMedium();

        void changeHigh();
    }
}
