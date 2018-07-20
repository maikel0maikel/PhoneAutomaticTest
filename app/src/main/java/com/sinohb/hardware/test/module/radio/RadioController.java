package com.sinohb.hardware.test.module.radio;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.task.BaseTestTask;

public class RadioController extends BaseDisplayViewController implements RadioPresenter.Controller, RadioManagerable.RadioListener {
    private RadioManagerable radioManager;

    public RadioController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new RadioTask(this);
        radioManager = new RadioTestManager(this);
    }


    @Override
    public int openRadio() {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        ((RadioPresenter.View) mView).notifyOpenRadio();
        return radioManager.openRadio();
    }

    @Override
    public int play(int freq) {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        ((RadioPresenter.View) mView).notifyPlay(freq);
        return radioManager.play(freq);
    }

    @Override
    public int search() {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        ((RadioPresenter.View) mView).notifySearch();
        return radioManager.search();
    }

    @Override
    public int closeRadio() {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        ((RadioPresenter.View) mView).notifyCloseRadio();
        return radioManager.closeRadio();
    }


    @Override
    public void notifyRadioState(int state) {
        if (task != null) {
            ((RadioTask) task).notifyRadioState(state);
        }
    }

    @Override
    public void notifyRadioFreq(int freq) {
        if (task != null) {
            ((RadioTask) task).notifyRadioFreq(freq);
        }
    }

    @Override
    public BaseTestTask getTask() {
        return task;
    }
}
