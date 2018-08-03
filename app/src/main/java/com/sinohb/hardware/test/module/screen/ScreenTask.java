package com.sinohb.hardware.test.module.screen;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class ScreenTask extends BaseTestTask {
    private static final String TAG = "ScreenTask";
    private static final long STEP_TIME = 2000;
    private static final int STEP_R = 1;
    private static final int STEP_G = 2;
    private static final int STEP_B = 3;

    public ScreenTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_SCREEN;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(STEP_R, HardwareTestApplication.getContext().getResources().getString( R.string.label_rgb_r), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(STEP_G, HardwareTestApplication.getContext().getResources().getString(R.string.label_rgb_g), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity3 = new StepEntity(STEP_B, HardwareTestApplication.getContext().getResources().getString(R.string.label_rgb_b), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
        stepEntities.add(stepEntity3);
    }

    @Override
    public Integer call() throws Exception {
        ScreenPresenter.Controller controller = (ScreenPresenter.Controller) mPresenter.get();
        while (!isFinish) {
            LogTools.p(TAG, "屏幕测试开始");
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_R;
                    //testing(controller);
                    for (StepEntity entity:stepEntities){
                        entity.setTestState(Constants.TestItemState.STATE_TESTING);
                    }
                    break;
                case STATE_RUNNING:
                    testing(controller);
                    break;
                case STATE_PAUSE:
                    LogTools.p(TAG, "暂停屏幕测试任务");
                    synchronized (mSync) {
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    isFinish = true;
                    break;
                case STATE_STEP_FINSH:
                    if (mTestStep == STEP_R) {
                        stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mTestStep = STEP_G;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_G) {
                        stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mTestStep = STEP_B;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_B) {
                        stepEntities.get(2).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mExecuteState = STATE_FINISH;
                    }
                    break;
            }
        }
        LogTools.p(TAG, "屏幕测试结束");
        controller.complete();
        return isPass;
    }

    private void testing(ScreenPresenter.Controller controller) throws InterruptedException {
        while (mExecuteState == STATE_RUNNING) {
            executeStep(controller);
            Thread.sleep(STEP_TIME);
            mExecuteState = STATE_STEP_FINSH;
        }
    }

    private void executeStep(ScreenPresenter.Controller controller) {
        switch (mTestStep) {
            case STEP_R:
                controller.displayR();
                LogTools.p(TAG, "测试显示红色");
                break;
            case STEP_G:
                controller.displayG();
                LogTools.p(TAG, "测试显示绿色");
                break;
            case STEP_B:
                controller.displayB();
                LogTools.p(TAG, "测试显示蓝色");
                break;
        }
    }


}
