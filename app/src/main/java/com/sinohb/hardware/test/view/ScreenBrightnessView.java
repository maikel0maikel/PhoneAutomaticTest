package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.brightness.BrightnessController;
import com.sinohb.hardware.test.module.brightness.BrightnessPresenter;

public class ScreenBrightnessView extends Activity implements BrightnessPresenter.View{
    private BrightnessPresenter.Controller mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BrightnessController(this);
    }

    @Override
    public void setPresenter(BrightnessPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }
}
