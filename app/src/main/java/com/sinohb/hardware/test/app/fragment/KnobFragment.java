package com.sinohb.hardware.test.app.fragment;



import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.knob.KnobController;
import com.sinohb.hardware.test.task.BaseTestTask;

public class KnobFragment extends KeyFragment{
    public static KnobFragment newInstance() {

        return new KnobFragment();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new KnobController(this);
            mHandler = new KeyHandler(this);
        }
    }
    public KnobFragment(){
        init();
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE){
            setOperateHintText(R.string.label_test_knob_wait_hint);
        }
    }

    @Override
    protected void displayHintView(int keyCode, int errorCode) {
        int resId = 0;
        switch (keyCode) {
            case Constants.HandlerMsg.MSG_KNOB_COUNTERCLOCKWISE:
                resId = R.string.label_test_knob_counterclockwise_hint;
                break;
            case Constants.HandlerMsg.MSG_KNOB_CLOCKWISE:
                resId = R.string.label_test_knob_clockwise_hint;
                break;
        }
        setHintText(errorCode,resId);
    }
}
