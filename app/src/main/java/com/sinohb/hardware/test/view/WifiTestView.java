package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.wifi.WifiController;
import com.sinohb.hardware.test.module.wifi.WifiPresenter;

public class WifiTestView extends Activity implements WifiPresenter.View{
    private WifiPresenter.Controller mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new WifiController(this);
    }

    @Override
    public void setPresenter(WifiPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
