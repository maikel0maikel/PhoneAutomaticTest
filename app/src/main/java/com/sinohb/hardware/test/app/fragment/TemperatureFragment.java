package com.sinohb.hardware.test.app.fragment;

import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.module.temperature.TemperatureController;
import com.sinohb.hardware.test.module.temperature.TemperaturePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class TemperatureFragment extends BaseAutomaticFragment implements TemperaturePresenter.View{
    public static TemperatureFragment newInstance() {

        return new TemperatureFragment();
    }
    public TemperatureFragment(){
        init();
    }

    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            super.init();
            new TemperatureController(this);
        }
    }

    @Override
    public void notifyTemperature(int temperature) {

    }
}
