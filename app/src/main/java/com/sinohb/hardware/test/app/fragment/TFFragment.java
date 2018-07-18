package com.sinohb.hardware.test.app.fragment;

import com.sinohb.hardware.test.module.tf.TFPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class TFFragment extends USBFragment  {
    public static TFFragment newInstance() {

        return new TFFragment();
    }
    public TFFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            mHandler = new BaseAutomaticHandler(this);
            new TFPresenter(this);
        }
    }

    @Override
    public void notifyStoregeStatus(int state) {
        if (mainActivity!=null){
            mainActivity.setTFStatusImg(state);
        }
    }
}
