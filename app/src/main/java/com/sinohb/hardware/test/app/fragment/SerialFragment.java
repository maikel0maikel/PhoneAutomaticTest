package com.sinohb.hardware.test.app.fragment;

import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.module.serial.SerialController;
import com.sinohb.hardware.test.module.serial.SerialPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class SerialFragment extends BaseAutomaticFragment implements SerialPresenter.View{
    public static SerialFragment newInstance() {

        return new SerialFragment();
    }
    public SerialFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null){
            super.init();
            new SerialController(this);
        }

    }

    @Override
    public void notifyVersion(final String version) {
        if (mainActivity!=null){
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setSerialVersion(version);
                }
            });
        }
    }

    @Override
    public void displayView() {

    }
}
