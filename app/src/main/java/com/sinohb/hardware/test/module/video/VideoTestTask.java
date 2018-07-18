package com.sinohb.hardware.test.module.video;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class VideoTestTask extends BaseManualTestTask{
    public VideoTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_VIDEO;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_video_preview), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((BaseDisplayViewPresenter)mPresenter).notifyExecuteState(STATE_RUNNING);
        LogTools.p(TAG, "视频监控任务进行中");
        synchronized (mSync){
            mSync.wait();
        }
    }

    public void notifyPreView(){
        if (mExecuteState!=STATE_RUNNING){
            return;
        }
        synchronized (mSync){
            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
            mExecuteState = STATE_STEP_FINSH;
            mSync.notify();
        }
    }

    @Override
    protected void executeStateStepFinish() throws InterruptedException {
        super.executeStateStepFinish();
        synchronized (mSync){
            LogTools.p(TAG,"睡眠5秒");
            mSync.wait(5000);
            if (mExecuteState == STATE_STEP_FINSH){
                mExecuteState = STATE_TEST_WAIT_OPERATE;
            }
        }

    }

    @Override
    protected void startTest() {
        super.startTest();
        ((BaseDisplayViewPresenter)mPresenter).notifyExecuteState(STATE_NONE);
    }
}
