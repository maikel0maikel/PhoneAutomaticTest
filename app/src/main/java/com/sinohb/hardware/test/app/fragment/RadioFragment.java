package com.sinohb.hardware.test.app.fragment;

import android.os.Message;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.radio.RadioController;
import com.sinohb.hardware.test.module.radio.RadioPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class RadioFragment extends BaseManualFragment implements RadioPresenter.View {
    public static RadioFragment newInstance() {

        return new RadioFragment();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            mHandler = new RadioHandler(this);
            new RadioController(this);
        }
    }
    public RadioFragment(){
        init();
    }
    @Override
    public void notifyOpenRadio() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_RADIO_OPEN);
    }

    @Override
    public void notifyPlay(int freq) {
        mHandler.obtainMessage(Constants.HandlerMsg.MSG_RADIO_PLAY, freq).sendToTarget();
    }

    @Override
    public void notifySearch() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_RADIO_SEARCH);
    }

    @Override
    public void notifyCloseRadio() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_RADIO_CLOSE);
    }


    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setOperateHintText(R.string.label_test_radio_hint);
        }
    }


    private static class RadioHandler extends ManualHandler {
        private RadioHandler(BaseManualFragment controller) {
            super(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            RadioFragment fragment = (RadioFragment) controllerWeakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_RADIO_OPEN:
                    fragment.setOperateHintText(R.string.label_test_radio_open_hint);
                    break;
                case Constants.HandlerMsg.MSG_RADIO_PLAY:
                    int frq = (int) msg.obj;
                    float radioFreq = frq / 100.0f;
                    fragment.setOperateHintText(String.format(fragment.getResources().getString(R.string.label_test_radio_play_hint),radioFreq));
                    break;
                case Constants.HandlerMsg.MSG_RADIO_SEARCH:
                    fragment.setOperateHintText(R.string.label_test_radio_search_hint);
                    break;
                case Constants.HandlerMsg.MSG_RADIO_CLOSE:
                    fragment.setOperateHintText(R.string.label_test_radio_close_hint);
                    break;
                case Constants.HandlerMsg.MSG_RADIO_SEARCH_NONE:
                    fragment.setOperateHintText(R.string.label_test_radio_search_none_hint);
                    break;
            }
        }
    }
}
