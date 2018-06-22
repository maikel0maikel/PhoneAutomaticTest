package com.sinohb.hardware.test.module.volume;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface VolumePresenter {
    interface View extends BaseView<Controller>{

    }
    interface Controller extends BasePresenter{

        void adjustLow();

        void adjustMedium();

        void adjustHight();
    }
}
