package com.sinohb.hardware.test.module.radio;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;

public class RadioController extends BaseDisplayViewController implements RadioPresenter.Controller {
    private RadioManagerable radioManager;

    public RadioController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new RadioTask(this);
        radioManager = new RadioTestManager(new RadioListener(this));
    }


    @Override
    public int openRadio() {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mView != null)
            ((RadioPresenter.View) mView).notifyOpenRadio();
        return radioManager.openRadio();
    }

    @Override
    public int play(int freq) {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mView != null)
            ((RadioPresenter.View) mView).notifyPlay(freq);
        return radioManager.play(freq);
    }

    @Override
    public int search(int type, int freq) {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mView != null)
            ((RadioPresenter.View) mView).notifySearch(type, freq);
        return radioManager.search(type);
    }

    @Override
    public int closeRadio() {
        if (radioManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mView != null)
            ((RadioPresenter.View) mView).notifyCloseRadio();
        return radioManager.closeRadio();
    }

    @Override
    public int getCurrentFreq() {
        return radioManager == null ? 0 : radioManager.getCurrentFreq();
    }

    @Override
    public void notifyStopFreq(int type, int freq) {
        if (mView!=null){
            ((RadioPresenter.View) mView).notifyStopFreq(type,freq);
        }
    }

    @Override
    public void destroy() {
        if (radioManager != null) {
            radioManager.destroy();
        }
        super.destroy();

    }

    static class RadioListener implements RadioManagerable.RadioListener {
        WeakReference<RadioController> weakReference;

        RadioListener(RadioController controller) {
            weakReference = new WeakReference<>(controller);
        }

        @Override
        public void notifyRadioState(int state) {
            if (weakReference == null || weakReference.get() == null) {
                LogTools.e(TAG, "notifyRadioState weakReference is null ");
                return;
            }
            if (weakReference.get().task != null) {
                ((RadioTask) weakReference.get().task).notifyRadioState(state);
            }
        }

        @Override
        public void notifyRadioFreq(int freq) {
            if (weakReference == null || weakReference.get() == null) {
                LogTools.e(TAG, "notifyRadioState weakReference is null ");
                return;
            }
            if (weakReference.get().task != null) {
                ((RadioTask) weakReference.get().task).notifyRadioFreq(freq);
            }
        }
    }

}
