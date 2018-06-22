package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.bluetooth.BluetoothController;
import com.sinohb.hardware.test.module.bluetooth.BluetoothPresenter;

public class BluetoothTestView extends Activity implements BluetoothPresenter.View{
    private BluetoothPresenter.Controller mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new BluetoothController(this);
    }

    @Override
    public void setPresenter(BluetoothPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void notifyOpenOrCloseState(int btOnOrOffState) {

    }

    @Override
    public void notifyBoundState(int btBoundState) {

    }

    @Override
    public void notifyConnectState(int btConnectState) {

    }
}
