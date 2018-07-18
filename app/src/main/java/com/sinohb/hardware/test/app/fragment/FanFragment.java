package com.sinohb.hardware.test.app.fragment;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.fan.FanController;
import com.sinohb.hardware.test.module.fan.FanPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class FanFragment extends BaseManualFragment implements FanPresenter.View {

    private Button turnOnBtn;
    private Button turnOffBtn;
    private TextView hintTv;

    public static FanFragment newInstance() {

        return new FanFragment();
    }
    public FanFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new FanController(this);
            mHandler = new FanHandler(this);
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setStubVisibility(test_state_stub, View.GONE);
            setStubVisibility(manual_stub, View.GONE);
            setStubVisibility(operate_hint_stub, View.GONE);
            inflateOtherStub();
            setHintText(R.string.label_test_fan_hint);
        } else if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE && other_stub != null) {
            setStubVisibility(other_stub, View.GONE);
        } else if (state == BaseTestTask.STATE_RUNNING) {
            setStubVisibility(operate_hint_stub, View.GONE);
            setBtnEnable(true);
        }
    }

    @Override
    protected void inflateOtherStub() {
        if (other_stub == null) {
            return;
        }
        if (other_stub.getParent() != null) {
            otherView = other_stub.inflate();
            View view = mainActivity.getLayoutInflater().inflate(R.layout.fragment_fan, null);
            ((FrameLayout) otherView).addView(view);
            turnOnBtn = initButton(view, R.id.turn_on_fan_btn);
            turnOffBtn = initButton(view, R.id.turn_off_fan_btn);
            hintTv = (TextView) view.findViewById(R.id.hint_tv);
            setBtnEnable(false);
        }
        other_stub.setVisibility(View.VISIBLE);
    }

    private void setBtnEnable(boolean enable) {
        if (turnOffBtn != null) {
            turnOffBtn.setEnabled(enable);
        }
        if (turnOnBtn != null) {
            turnOnBtn.setEnabled(enable);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.turn_on_fan_btn:
                ((FanPresenter.Controller) mPresenter).turnOnFan();
                v.setEnabled(false);
                setHintText(R.string.label_fan_turn_on_hint);
                break;
            case R.id.turn_off_fan_btn:
                ((FanPresenter.Controller) mPresenter).turnOffFan();
                v.setEnabled(false);
                setHintText(R.string.label_fan_turn_off_hint);
                break;
        }
    }

    private void setHintText(int resId) {
        if (hintTv != null) {
            hintTv.setText(resId);
        }
    }

    @Override
    public void notifyTest() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_FAN_NOTIFY_TEST);
        }
    }

    @Override
    public void notifyTurnOn() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_FAN_TURN_ON);
        }
    }

    @Override
    public void notifyTurnOff() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_FAN_TURN_OFF);
        }
    }

    private static class FanHandler extends ManualHandler {

        public FanHandler(BaseManualFragment controller) {
            super(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            FanFragment fragment = (FanFragment) controllerWeakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_FAN_TURN_ON:
                    if (fragment.turnOnBtn != null) {
                        fragment.turnOnBtn.setEnabled(true);
                    }
                    break;
                case Constants.HandlerMsg.MSG_FAN_TURN_OFF:
                    if (fragment.turnOffBtn != null) {
                        fragment.turnOffBtn.setEnabled(true);
                    }
                    break;
                case Constants.HandlerMsg.MSG_FAN_NOTIFY_TEST:
                    fragment.setBtnEnable(false);
                    fragment.setHintText(R.string.label_fan_notify_test_hint);
                    break;
            }
        }
    }
}
