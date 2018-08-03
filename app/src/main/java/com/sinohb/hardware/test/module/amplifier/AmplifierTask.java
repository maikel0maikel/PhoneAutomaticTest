package com.sinohb.hardware.test.module.amplifier;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class AmplifierTask extends BaseManualTestTask {
    private int[] arrays = new int[4];
    private int mHitPosition = -1;

    public AmplifierTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_AMPLIFIER;
    }

    @Override
    protected void initStepEntity() {
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.lable_left_front_btn), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.lable_left_rear_btn), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity3 = new StepEntity(3, HardwareTestApplication.getContext().getResources().getString(R.string.lable_right_front_btn), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity4 = new StepEntity(4, HardwareTestApplication.getContext().getResources().getString(R.string.lable_right_rear_btn), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
        stepEntities.add(stepEntity3);
        stepEntities.add(stepEntity4);
    }


    public void tstRunning(int direction) {
        mHitPosition = direction;
        synchronized (mSync) {
            mExecuteState = STATE_RUNNING;
            arrays[direction] = direction + 1;
            stepEntities.get(direction).setTestState(1);
            mSync.notify();
        }
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        LogTools.p(TAG, "功放测试任务进行中");
        ((AmplifierPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        int count = 0;
        for (int i : arrays) {
            if (i > 0) {
                count++;
            }
        }
        if (count == 4) {
            mExecuteState = STATE_TEST_WAIT_OPERATE;
            ((AmplifierPresenter.Controller) mPresenter.get()).notifyTestAll();
        } else {
            synchronized (mSync) {
                mSync.wait();
            }
            Thread.sleep(100);
            ((AmplifierPresenter.Controller) mPresenter.get()).notifyAmplifierPosition(mHitPosition);
        }
    }

    @Override
    protected void startTest() {
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = 0;
        }
        super.startTest();
    }
}
