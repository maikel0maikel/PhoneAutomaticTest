package com.sinohb.hardware.test.module.gps;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.logger.LogTools;

public class GPSTestTask extends BaseAutoTestTask {
    private static final int STEP_CHECK = 1;
    private static final int STEP_LOCATE = 2;
    private static final int STEP_LOCATE_FAIL = 3;
    private static final int STEP_LOCATE_SUCCESS = 4;
    private static final int STEP_STOP_LOCATE = 5;

    public GPSTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_GPS;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_gps_check), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_gps_locate), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        boolean isChange = false;
        while (mExecuteState == STATE_RUNNING) {
            switch (mTestStep){
                case STEP_CHECK:
                    boolean isGPSOpen = ((GPSPresenter.Controller) mPresenter).isEnable();
                    if (isGPSOpen){
                        mTestStep = STEP_LOCATE;
                        LogTools.p(TAG, "gps 已经打开----");
                        isChange = notifyGpsState(isChange, isGPSOpen, 1);
                        stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    }else {
                        isChange = notifyGpsState(isChange, isGPSOpen, 0);
                        Thread.sleep(1000);
                    }
                    break;
                case STEP_LOCATE:
                    LogTools.p(TAG,"gps开始定位");
                    synchronized (mSync) {
                        ((GPSPresenter.Controller) mPresenter).startLocateInMain();
                        mSync.wait();
                    }
                    break;
                case STEP_LOCATE_SUCCESS:
                    LogTools.p(TAG,"gps定位成功");
                    mTestStep = STEP_STOP_LOCATE;
                    stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    break;
                case STEP_STOP_LOCATE:
                    LogTools.p(TAG,"gps停止定位");
                    ((GPSPresenter.Controller) mPresenter).stopLocateInMain();
                    mExecuteState = STATE_FINISH;
                    break;
                case STEP_LOCATE_FAIL:
                    LogTools.p(TAG,"gps定位失败");
                    stepEntities.get(1).setTestState(Constants.TestItemState.STATE_FAIL);
                    mExecuteState = STATE_TEST_UNPASS;
                    break;
            }
        }
    }

    private boolean notifyGpsState(boolean isChange, boolean isGPSOpen, int i) {
        if (isChange != isGPSOpen) {
            isChange = isGPSOpen;
            ((GPSPresenter.Controller) mPresenter).notifyGPSState(i);
        }
        return isChange;
    }

    public void notifyLocateSuccess(){
        synchronized (mSync){
            mTestStep = STEP_LOCATE_SUCCESS;
            mSync.notify();
        }
    }
    public void notifyLocateFail(){
        synchronized (mSync){
            mTestStep = STEP_LOCATE_FAIL;
            mSync.notify();
        }
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = STEP_CHECK;
    }
}
