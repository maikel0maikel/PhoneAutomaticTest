package com.sinohb.hardware.test.app.fragment;

import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.amplifier.AmplifierController;
import com.sinohb.hardware.test.module.amplifier.AmplifierPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class AmplifierFragment extends BaseManualFragment implements AmplifierPresenter.View {
    private TextView hintTv;
    private Button left_front_btn;
    private Button left_rear_btn;
    private Button right_front_btn;
    private Button right_rear_btn;

    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            mHandler = new AmplifierHandler(this);
            new AmplifierController(this);
        }
    }

    @Override
    public void notifyPlayAmplifier(int direction) {

    }

    public static AmplifierFragment newInstance() {

        return new AmplifierFragment();
    }

    public AmplifierFragment(){
        init();
    }

    @Override
    public void notifyTestAll() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_AMPLIFIER_TEST_ALL);
        }
    }

    @Override
    public void notifyAmplifierPosition(int position) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_AMPLIFIER_HIT_POS, position).sendToTarget();
        }
    }

    private static class AmplifierHandler extends ManualHandler {

        private AmplifierHandler(BaseManualFragment controller) {
            super(controller);
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            AmplifierFragment fragment = (AmplifierFragment) controllerWeakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_AMPLIFIER_TEST_ALL:
                    fragment.setHintText(R.string.label_amplifier_finish_hint);
                    fragment.setBtnEnable(false);
                    break;
                case Constants.HandlerMsg.MSG_AMPLIFIER_HIT_POS:
                    fragment.enableAmplifierBtn((Integer) msg.obj);
                    break;
            }
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setStubVisibility(test_state_stub,View.GONE);
            setStubVisibility(manual_stub,View.GONE);
            setStubVisibility(operate_hint_stub,View.GONE);
            inflateOtherStub();
        } else if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE && other_stub != null) {
            setStubVisibility(other_stub,View.GONE);
        } else if (state == BaseTestTask.STATE_RUNNING) {
            setStubVisibility(operate_hint_stub,View.GONE);
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
            View view = mainActivity.getLayoutInflater().inflate(R.layout.fragment_amplifier, null);
            ((FrameLayout) otherView).addView(view);
            left_front_btn = initButton(view, R.id.left_front_btn);
            left_rear_btn = initButton(view, R.id.left_rear_btn);
            right_front_btn = initButton(view, R.id.right_front_btn);
            right_rear_btn = initButton(view, R.id.right_rear_btn);
            hintTv = (TextView) view.findViewById(R.id.hint_tv);
        }
        other_stub.setVisibility(View.VISIBLE);
        hintTv.setText(R.string.label_test_sound_amplifier_hint);
        setBtnEnable(false);
    }

    private void setBtnEnable(boolean isEnable) {
        if (left_front_btn != null)
            left_front_btn.setEnabled(isEnable);
        if (left_rear_btn != null)
            left_rear_btn.setEnabled(isEnable);
        if (right_front_btn != null)
            right_front_btn.setEnabled(isEnable);
        if (right_rear_btn != null)
            right_rear_btn.setEnabled(isEnable);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.left_front_btn:
                ((AmplifierPresenter.Controller) mPresenter).playAmplifier(AmplifierPresenter.LEFT_FRONT);
                setHintText(R.string.label_left_front_hint);
                setBtnEnable(false);
                break;
            case R.id.left_rear_btn:
                ((AmplifierPresenter.Controller) mPresenter).playAmplifier(AmplifierPresenter.LEFT_REAR);
                setHintText(R.string.label_left_rear_hint);
                setBtnEnable(false);
                break;
            case R.id.right_front_btn:
                ((AmplifierPresenter.Controller) mPresenter).playAmplifier(AmplifierPresenter.RIGHT_FRONT);
                setHintText(R.string.label_right_front_hint);
                setBtnEnable(false);
                break;
            case R.id.right_rear_btn:
                ((AmplifierPresenter.Controller) mPresenter).playAmplifier(AmplifierPresenter.RIGHT_REAR);
                setHintText(R.string.label_right_rear_hint);
                setBtnEnable(false);
                break;
        }
    }

    private void enableAmplifierBtn(int position) {
        left_front_btn.setEnabled(true);
        left_rear_btn.setEnabled(true);
        right_front_btn.setEnabled(true);
        right_rear_btn.setEnabled(true);
    }

    private void setHintText(int resId) {
        if (hintTv != null) {
            hintTv.setText(resId);
        }
    }
}
