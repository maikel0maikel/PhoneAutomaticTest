package com.sinohb.hardware.test.module.rearview;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class RearViewTestTask extends BaseManualTestTask {
    private static final int REAR_START_COMMAND_SEND = 1;
    private static final int REAR_STOP = 2;
    private static final int REAR_START_OK = 3;
    private static final int REAR_COMMAND_SEND_FAILURE = 4;
    private static final int REAR_STOP_OK = 5;
    private int rearStartCount = 0;
    private int rearStopCount = 0;

    public RearViewTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_REAR_VIEW;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_rear_start), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_rear_stop), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((RearViewPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case REAR_START_COMMAND_SEND:
                        ((RearViewPresenter.Controller) mPresenter.get()).notifyRearViewStart();
                        mPreStep = mTestStep;
                        ((RearViewPresenter.Controller) mPresenter.get()).startRearView();
                        LogTools.p(TAG, "倒车后视启动");
                        rearStartCount++;
                        if (rearStartCount >= 3) {
                            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_FAIL);
                            mExecuteState = STATE_TEST_UNPASS;
                            rearStartCount = 0;
                            break;
                        }
                        mSync.wait(1000 * 10);
                        if (mTestStep == REAR_START_COMMAND_SEND) {
                            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_FAIL);
                            mTestStep = REAR_STOP;
                        }
                        break;
                    case REAR_STOP:
                        ((RearViewPresenter.Controller) mPresenter.get()).notifyRearViewStop();
                        mPreStep = mTestStep;
                        ((RearViewPresenter.Controller) mPresenter.get()).stopRearView();
                        LogTools.p(TAG, "倒车后视停止");
                        rearStopCount++;
                        if (rearStopCount >= 3) {
                            stepEntities.get(1).setTestState(Constants.TestItemState.STATE_FAIL);
                            mExecuteState = STATE_TEST_UNPASS;
                            rearStopCount = 0;
                            break;
                        }
                        mSync.wait(1000 * 10);
                        if (mTestStep == REAR_STOP) {
                            stepEntities.get(1).setTestState(Constants.TestItemState.STATE_FAIL);
                            mExecuteState = STATE_TEST_UNPASS;
                        }
                        break;
                    case REAR_START_OK:
                        LogTools.p(TAG, "倒车后视启动成功");
                        Thread.sleep(3000);
                        mTestStep = REAR_STOP;
                        break;
                    case REAR_COMMAND_SEND_FAILURE:
                        LogTools.e(TAG, "指令发送失败");
                        mTestStep = mPreStep;
                        break;
                    case REAR_STOP_OK:
                        mExecuteState = STATE_TEST_WAIT_OPERATE;
                        /**防止退不出多发一次**/
                        ((RearViewPresenter.Controller) mPresenter.get()).stopRearView();
                        LogTools.p(TAG, "停止倒车后视成功");
                        break;
                }

            }
        }
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = REAR_START_COMMAND_SEND;
        rearStartCount = 0;
        rearStopCount = 0;
    }

    public void notifyRearResult() {
        synchronized (mSync) {
            LogTools.p(TAG, "notifyRearResult mExecuteState=" + mExecuteState);
            if (mTestStep == REAR_START_COMMAND_SEND) {
                if (mExecuteState == STATE_RUNNING) {
                    mTestStep = REAR_START_OK;
                }
                stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                mSync.notify();
            }
        }
    }

    public void notifyStop() {
        synchronized (mSync) {
            if (mTestStep == REAR_STOP) {
                if (mExecuteState == STATE_RUNNING) {
                    mTestStep = REAR_STOP_OK;
                }
                stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                mSync.notify();
            }
        }
    }

    public void sendFailure() {
        if (mExecuteState != STATE_RUNNING) {
            LogTools.p(TAG, "mExecuteState:" + mExecuteState);
            return;
        }
        synchronized (mSync) {
            mTestStep = REAR_COMMAND_SEND_FAILURE;
            mSync.notify();
        }
    }

    @Override
    protected void unpass() {
        LogTools.p(TAG, "unpass mTestStep = " + mTestStep);
        if (mTestStep != REAR_STOP_OK && mPresenter.get() != null) {
            ((RearViewPresenter.Controller) mPresenter.get()).stopRearView();
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mTestStep != REAR_STOP_OK && mPresenter.get() != null) {
            ((RearViewPresenter.Controller) mPresenter.get()).stopRearView();
        }
    }
}
