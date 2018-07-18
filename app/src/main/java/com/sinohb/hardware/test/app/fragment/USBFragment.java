package com.sinohb.hardware.test.app.fragment;


import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.module.usb.USBController;
import com.sinohb.hardware.test.module.usb.USBPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class USBFragment extends BaseAutomaticFragment implements USBPresenter.View {
    public static USBFragment newInstance() {

        return new USBFragment();
    }

    public USBFragment() {
        init();
    }

    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            super.init();
            new USBController(this);
        }
    }


    @Override
    public void notifyStoregeStatus(int state) {
        if (mainActivity != null) {
            mainActivity.setUSBStatusImg(state);
        }
    }
}
