package com.sinohb.hardware.test.module.knob;

import android.view.KeyEvent;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.module.key.KeyPresenter;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class KnobTestTask extends BaseManualTestTask {
    private static final int SETP_PRESS_KNOB_COUNTERCLOCKWISE = 1;
    private static final int SETP_PRESS_KNOB_CLOCKWISE = 2;
    private static final int SETP_PRESS_KNOB_ERROR = 3;
    private static final int STEP_KNOB_FINISH = 4;
    private int errorCount = 0;
    private int keyPressError = 0;
    public KnobTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_KNOB;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(SETP_PRESS_KNOB_COUNTERCLOCKWISE, HardwareTestApplication.getContext().getResources().getString(R.string.label_knob_counterclockwise), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(SETP_PRESS_KNOB_CLOCKWISE, HardwareTestApplication.getContext().getResources().getString(R.string.label_knob_clockwise), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);

    }

    private boolean breakCondition(int nextStep) {
        if (errorCount >= 3) {
            stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_FAIL);
            mTestStep = nextStep;
            errorCount = 0;
            keyPressError++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void notifyTestKey(int keyCode) {
        if (mExecuteState!=STATE_RUNNING){
            LogTools.e(TAG,"execute state is not correct mExecuteState:"+mExecuteState);
            return;
        }
        synchronized (mSync) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (notifyPressKey(SETP_PRESS_KNOB_COUNTERCLOCKWISE)) {
                        mTestStep = SETP_PRESS_KNOB_CLOCKWISE;
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (notifyPressKey(SETP_PRESS_KNOB_CLOCKWISE)) {
                        mTestStep = STEP_KNOB_FINISH;
                    }
                    break;
                default:
                    mTestStep = SETP_PRESS_KNOB_ERROR;
                    break;
            }
            mSync.notify();
        }
    }

    private boolean notifyPressKey(int setpPressKey) {
        boolean isOk = false;
        if (mTestStep == setpPressKey) {
            errorCount = 0;
            isOk = true;
            stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_SUCCESS);
        } else {
            //stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_FAIL);
            mTestStep = SETP_PRESS_KNOB_ERROR;
        }
        return isOk;
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = SETP_PRESS_KNOB_COUNTERCLOCKWISE;
        errorCount = 0;
        keyPressError = 0;
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        super.executeRunningState();
        ((KeyPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case SETP_PRESS_KNOB_COUNTERCLOCKWISE:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KNOB_COUNTERCLOCKWISE, errorCount);
                        LogTools.p(TAG, "旋钮测试逆时针");
                        if (breakCondition(SETP_PRESS_KNOB_CLOCKWISE)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KNOB_CLOCKWISE:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KNOB_CLOCKWISE, errorCount);
                        LogTools.p(TAG, "旋钮测试顺时针");
                        if (breakCondition(STEP_KNOB_FINISH)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KNOB_ERROR:
                        errorCount++;
                        mTestStep = mPreStep;
                        LogTools.p(TAG, "按错按键---errorCount:" + errorCount);
                        break;
                    case STEP_KNOB_FINISH:
                        if (keyPressError>0){
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG,"按键测试不通过");
                        }else {
                            mExecuteState = STATE_FINISH;
                            LogTools.p(TAG,"按键测试通过");
                        }
                        break;
                }
            }
        }
    }
}
