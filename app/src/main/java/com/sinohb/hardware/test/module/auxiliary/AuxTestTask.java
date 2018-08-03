package com.sinohb.hardware.test.module.auxiliary;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class AuxTestTask extends BaseManualTestTask {
    private int waiteCount = 0;
    public AuxTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_AUX;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_aux_plug), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int auxState;
        ((AuxPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                auxState = ((AuxController) mPresenter.get()).getAuxStatus();
                if (auxState == 1) {
                    ((AuxPresenter.Controller) mPresenter.get()).notifyAuxStatus(auxState);
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    mExecuteState = STATE_TEST_WAIT_OPERATE;
                    waiteCount = 0;
                } else {
                    mSync.wait(1000);
                    waiteCount++;
                    if (waiteCount>=60){
                        mExecuteState = STATE_TEST_UNPASS;
                        waiteCount = 0;
                        LogTools.p(TAG,"aux 1分钟没有检测到插入不通过");
                    }
                }
            }
        }
    }

    public void notifyAuxInsert() {
        if (mExecuteState!=STATE_RUNNING){
            LogTools.p(TAG,"notifyAuxInsert mExecuteState:"+mExecuteState);
            return;
        }
        stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
        mExecuteState = STATE_TEST_WAIT_OPERATE;
    }

}
