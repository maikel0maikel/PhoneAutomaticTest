package com.sinohb.hardware.test.module.screenadjust;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class ScreenAdjustTask extends BaseTestTask {
    private static final String TAG = "ScreenAdjustTask";
    private static final int SETP_ADJUST_LEFT_TOP = 0;
    private static final int SETP_ADJUST_RGIHT_TOP = 1;
    private static final int SETP_ADJUST_LEFT_BOTTOM = 2;
    private static final int SETP_ADJUST_RGIHT_BOTTOM = 3;
    private static final int SETP_ADJUST_CENTER = 4;

    public ScreenAdjustTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_TOUCH;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_adjust_left_top), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_adjust_right_top), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity3 = new StepEntity(3, HardwareTestApplication.getContext().getResources().getString(R.string.label_adjust_left_bottom), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity4 = new StepEntity(4, HardwareTestApplication.getContext().getResources().getString(R.string.label_adjust_right_bottom), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity5 = new StepEntity(5, HardwareTestApplication.getContext().getResources().getString(R.string.label_adjust_center), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
        stepEntities.add(stepEntity3);
        stepEntities.add(stepEntity4);
        stepEntities.add(stepEntity5);
    }

    @Override
    public Integer call() throws Exception {
        ScreenAdjustPresenter.Controller controller = (ScreenAdjustPresenter.Controller) mPresenter.get();
        LogTools.p(TAG, "屏幕校准开始");
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = SETP_ADJUST_LEFT_TOP;
                    for (StepEntity entity:stepEntities){
                        entity.setTestState(Constants.TestItemState.STATE_TESTING);
                    }
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            switch (mTestStep) {
                                case SETP_ADJUST_LEFT_TOP:
                                    controller.adjustLeftTop();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_RGIHT_TOP:
                                    controller.adjustRightTop();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_LEFT_BOTTOM:
                                    controller.adjustLeftBottom();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_RGIHT_BOTTOM:
                                    controller.adjustRightBottom();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_CENTER:
                                    controller.adjustCenter();
                                    mSync.wait();
                                    break;
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "屏幕校准暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    LogTools.p(TAG,"屏幕校准完成");
                    isFinish = true;
                    break;
            }
        }
        controller.complete();
        return isPass;
    }

    public void adjustOk(int direction) {
        LogTools.p(TAG,"校准成功 direction ="+direction);
        synchronized (mSync) {
            stepEntities.get(direction).setTestState(Constants.TestItemState.STATE_SUCCESS);
            if (direction==SETP_ADJUST_CENTER){
                mExecuteState = STATE_FINISH;
            }else {
                mTestStep = direction + 1;
            }
            mSync.notify();
        }
    }

    public void adjustFailure(int direction) {
        LogTools.p(TAG,"校准失败 direction ="+direction);
        synchronized (mSync) {
            stepEntities.get(direction).setTestState(Constants.TestItemState.STATE_FAIL);
            mTestStep = direction;
            mSync.notify();
        }
    }
}
