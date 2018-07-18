package com.sinohb.hardware.test.module.auxiliary;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface AuxPresenter {

    interface View extends BaseDisplayViewView {
        void notifyAuxStatusView(int state);
    }

    interface Controller extends BaseDisplayViewPresenter {

        int getAuxStatus();

        void notifyAuxStatus(int state);
    }

}
