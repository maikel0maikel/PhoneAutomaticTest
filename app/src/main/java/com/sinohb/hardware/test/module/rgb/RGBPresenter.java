package com.sinohb.hardware.test.module.rgb;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;

public interface RGBPresenter {
    interface View extends BaseExecuteView<Controller> {
        void startScreenView();
        void starRealTaskView();
    }

    interface Controller extends BaseDisplayViewPresenter {

        void startRealTaskView();
    }

}
