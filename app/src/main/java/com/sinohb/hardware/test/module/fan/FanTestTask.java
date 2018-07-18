package com.sinohb.hardware.test.module.fan;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class FanTestTask extends BaseManualTestTask {
    private boolean hasTestTurnOn = false;
    private boolean hasTestTurnOff = false;
    private static final int STEP_TURN_ON = 1;
    private static final int STEP_TURN_ON_PRESS = 2;
    private static final int STEP_TURN_OFF = 3;
    private static final int STEP_TURN_OFF_PRESS = 4;

    public FanTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_FAN;
    }

    @Override
    protected void initStepEntity() {
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_fan_turn_on), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_fan_turn_off), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
    }


    @Override
    protected void executeRunningState() throws InterruptedException {
        LogTools.p(TAG, "风扇测试任务进行中");
        ((FanPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_TURN_ON:
                        LogTools.p(TAG, "等待打开风扇");
                        mSync.wait();
                        break;
                    case STEP_TURN_ON_PRESS:
                        hasTestTurnOn = true;
                        stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        if (hasTestTurnOff){
                            ((FanPresenter.Controller) mPresenter).notifyTest();
                            mExecuteState = STATE_TEST_WAIT_OPERATE;
                        }else {
                            mTestStep = STEP_TURN_OFF;
                        }
                        LogTools.p(TAG,"风扇打开按钮已经点击");
                        break;
                    case STEP_TURN_OFF:
                        LogTools.p(TAG,"等待关闭风扇");
                        mSync.wait();
                        break;
                    case STEP_TURN_OFF_PRESS:
                        stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        hasTestTurnOff = true;
                        if (hasTestTurnOn){
                            ((FanPresenter.Controller) mPresenter).notifyTest();
                            mExecuteState = STATE_TEST_WAIT_OPERATE;
                        }else {
                            mTestStep = STEP_TURN_ON;
                        }
                        break;
                }
            }
        }
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = STEP_TURN_ON;
        hasTestTurnOn = false;
        hasTestTurnOff = false;
    }

    public void notifyTestTurnOn() {
        synchronized (mSync) {
            mTestStep = STEP_TURN_ON_PRESS;
            mSync.notify();
        }
    }
    public void notifyTestTurnOff() {
        synchronized (mSync) {
            mTestStep = STEP_TURN_OFF_PRESS;
            mSync.notify();
        }
    }
}
