package com.sinohb.hardware.test.module.serial;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.logger.LogTools;

public class SerialTask extends BaseAutoTestTask {
    private static final int STEP_COMMAND_SEND = 1;
    private static final int STEP_COMMAND_SEND_OK = 2;
    private static final int STEP_COMMAND_SEND_FAILURE = 3;
    public SerialTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_SERIAL;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_serial_version), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_COMMAND_SEND:
                        ((SerialPresenter.Controller) mPresenter).getVersion();
                        LogTools.p(TAG,"发送获取记录仪版本指令");
                        mSync.wait();
                        break;
                    case STEP_COMMAND_SEND_OK:
                        LogTools.p(TAG,"指令发送成功");
                        mSync.wait();
                        break;
                    case STEP_COMMAND_SEND_FAILURE:
                        LogTools.p(TAG,"指令发送失败");
                        mTestStep = STEP_COMMAND_SEND;
                        break;
                }

            }
        }
    }

    public void notifyResult() {
        synchronized (mSync) {
            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
            mExecuteState = STATE_FINISH;
            mSync.notify();
        }
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = STEP_COMMAND_SEND;
    }

    public void sendOk(){
        synchronized (mSync){
            mTestStep = STEP_COMMAND_SEND_OK;
            mSync.notify();
        }
    }
    public void sendFailure(){
        synchronized (mSync){
            mTestStep = STEP_COMMAND_SEND_FAILURE;
            mSync.notify();
        }
    }
}
