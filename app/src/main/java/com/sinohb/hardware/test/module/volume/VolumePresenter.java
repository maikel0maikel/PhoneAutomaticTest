package com.sinohb.hardware.test.module.volume;

import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface VolumePresenter {
    interface View extends BaseExecuteView<Controller> {

    }
    interface Controller extends BaseExecutePresenter{

        void adjustLow();

        void adjustMedium();

        void adjustHight();
    }
}
