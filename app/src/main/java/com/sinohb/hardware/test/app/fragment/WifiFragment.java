package com.sinohb.hardware.test.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sinohb.hardware.test.app.BaseAutomaticFragment;
import com.sinohb.hardware.test.module.wifi.WifiController;
import com.sinohb.hardware.test.module.wifi.WifiPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class WifiFragment extends BaseAutomaticFragment implements WifiPresenter.View {
    public static WifiFragment newInstance() {

        return new WifiFragment();
    }
    public WifiFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            super.init();
            new WifiController(this);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogTools.p(TAG, "onCreate");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogTools.p(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void notifyOpenOrCloseState(int state) {
        if (mainActivity != null) {
            mainActivity.freshWifiState(state);
        }
    }
}
