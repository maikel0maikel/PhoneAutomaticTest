package com.sinohb.hardware.test.module.temperature;

import com.sinohb.hardware.test.app.BaseExecutePresenter;

public interface TemperaturePresenter   {

    interface View{
        void notifyTemperature(int temperature);
    }

    interface Controller extends BaseExecutePresenter{
        int getTemperature();
    }



}
