package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.gps.GPSController;
import com.sinohb.hardware.test.module.gps.GPSPresenter;
import com.sinohb.logger.LogTools;

public class GpsView extends Activity implements GPSPresenter.View{
    private GPSPresenter.Controller mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GPSController(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogTools.i("MotionEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setPresenter(GPSPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }




}
