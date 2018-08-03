package com.sinohb.hardware.test.app.fragment;

import android.os.Bundle;

import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseFragment;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.module.volume.VolumeController;
import com.sinohb.hardware.test.module.volume.VolumePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class VolumeAdjustFragment extends BaseFragment implements VolumePresenter.View {
    public static VolumeAdjustFragment newInstance() {

        return new VolumeAdjustFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new VolumeController(this);

    }




    @Override
    public BaseExecutePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void freshExecuteUI(int executeState) {

    }

    @Override
    public void complete(BaseTestTask task) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        mPresenter = (BaseExecutePresenter) presenter;
    }
}
