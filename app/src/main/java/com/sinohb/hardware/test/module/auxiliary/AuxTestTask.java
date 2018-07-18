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
        ((AuxPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                auxState = ((AuxController) mPresenter).getAuxStatus();
                if (auxState == 1) {
                    ((AuxPresenter.Controller) mPresenter).notifyAuxStatus(auxState);
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    mExecuteState = STATE_TEST_WAIT_OPERATE;
                } else {
                    mSync.wait(1000);
                }
            }
        }
    }

    public void notifyAuxInsert() {
        if (mExecuteState!=STATE_RUNNING){
            LogTools.p(TAG,"notifyAuxInsert mExecuteState:"+mExecuteState);
            return;
        }
        mExecuteState = STATE_TEST_WAIT_OPERATE;
    }

}
