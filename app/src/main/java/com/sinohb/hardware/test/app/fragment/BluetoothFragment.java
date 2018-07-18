package com.sinohb.hardware.test.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.module.bluetooth.BluetoothController;
import com.sinohb.hardware.test.module.bluetooth.BluetoothPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class BluetoothFragment extends BaseAutomaticFragment implements BluetoothPresenter.View {

    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            super.init();
            new BluetoothController(this);
        }
    }
    public static BluetoothFragment newInstance() {

        return new BluetoothFragment();
    }
    public BluetoothFragment(){
        init();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogTools.p(TAG,"onCreate");
    }


    @Override
    public void notifyOpenOrCloseState(int btOnOrOffState) {
        if (mainActivity != null) {
            mainActivity.freshBtState(btOnOrOffState);
        }
    }

    @Override
    public void notifyBoundState(int btBoundState) {

    }

    @Override
    public void notifyConnectState(int btConnectState) {

    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_FINISH) {
            setOperateHintText(R.string.label_bt_test_success_hint);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogTools.p(TAG,"onDestroyView");
    }

}
