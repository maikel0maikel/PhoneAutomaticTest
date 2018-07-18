package com.sinohb.hardware.test.module.rearview;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface RearViewPresenter {

    interface View extends BaseDisplayViewView{

        void notifyRearViewStart();

        void notifyRearViewStop();
    }

    interface Controller extends BaseDisplayViewPresenter{

        void startRearView();

        void stopRearView();

        void notifyRearViewStart();

        void notifyRearViewStop();
    }

}
