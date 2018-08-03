package com.sinohb.hardware.test.module.brightness;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class BrightnessTask extends BaseManualTestTask {
    public static final int STEP_L = 1;
    public static final int STEP_M = 2;
    public static final int STEP_H = 3;
    public BrightnessTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_BRIGHTNESS;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_screen_bringhtness_l_hint), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_screen_bringhtness_m_hint), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity3 = new StepEntity(3, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_screen_bringhtness_h_hint), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
        stepEntities.add(stepEntity3);
    }

//    private void executeStep(BrightnessPresenter.Controller controller) {
//        switch (mTestStep) {
//            case STEP_L:
//                controller.changeLow();
//                LogTools.p(TAG, "亮度低");
//                break;
//            case STEP_M:
//                controller.changeMedium();
//                LogTools.p(TAG, "亮度中");
//                break;
//            case STEP_H:
//                controller.changeHigh();
//                LogTools.p(TAG, "亮度高");
//                break;
//        }
//    }

//    @Override
//    protected void startTest() {
//        super.startTest();
//        mTestStep = STEP_L;
//    }

//    @Override
//    protected void executeStateStepFinish() throws InterruptedException {
//        super.executeStateStepFinish();
//        stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_SUCCESS);
//        if (mTestStep == STEP_L) {
//            mTestStep = STEP_M;
//            mExecuteState = STATE_RUNNING;
//        } else if (mTestStep == STEP_M) {
//            mTestStep = STEP_H;
//            mExecuteState = STATE_RUNNING;
//        } else if (mTestStep == STEP_H) {
//            mExecuteState = STATE_TEST_WAIT_OPERATE;
//        }
//    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((BrightnessPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
//            executeStep((BrightnessPresenter.Controller) mPresenter);
//            Thread.sleep(3000);
//            mExecuteState = STATE_STEP_FINSH;
            /**
             * 修改为界面点击
             */
            synchronized (mSync){
                mSync.wait();
            }

        }
        LogTools.p(TAG,"已经测试完低、中、高亮度");
    }
    public void setBrightness(int step){
        switch (step){
            case STEP_L:
                stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case STEP_M:
                stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case STEP_H:
                stepEntities.get(2).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
        }
    }
}
