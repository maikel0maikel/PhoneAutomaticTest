package com.sinohb.hardware.test.app.fragment;

import android.view.View;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.module.bluetooth.BluetoothController;
import com.sinohb.hardware.test.module.bluetooth.BluetoothPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class BluetoothFragment extends BaseManualFragment implements BluetoothPresenter.View {

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

    public BluetoothFragment() {
        init();
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
        if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE) {
            setStubVisibility(operate_hint_stub,View.VISIBLE);
            setOperateHintText(R.string.label_bt_test_success_hint);
            setStubVisibility(test_state_stub, View.GONE);
        }else if (state == BaseTestTask.STATE_FINISH){

        }
    }


}
