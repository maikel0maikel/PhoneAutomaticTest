package com.sinohb.hardware.test.module.key;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;

public interface KeyPresenter {
    interface View extends BaseDisplayViewView {
        void pressKeyHint(int keyCode,int errorCount);
    }

    interface Controller extends BaseDisplayViewPresenter {
        void pressKey(int keyCode,int error);
        void notifyPressKey(int keyCode);
    }
}
