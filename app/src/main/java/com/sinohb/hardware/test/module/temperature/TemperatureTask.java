package com.sinohb.hardware.test.module.temperature;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.logger.LogTools;

public class TemperatureTask extends BaseAutoTestTask {
    private int normalTempCount = 0;
    private int heightTempCount = 0;
    private int lowTempCount = 0;

    public TemperatureTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_TEMPERATURE;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity = new StepEntity(1, String.format(HardwareTestApplication.getContext().getResources().
                getString(R.string.label_temperature),0), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        super.executeRunningState();
        int temp = 0;
        while (mExecuteState == STATE_RUNNING) {
            temp = ((TemperaturePresenter.Controller) mPresenter.get()).getTemperature();
            if (temp >= 6 && temp < 13) {
                normalTempCount++;
                heightTempCount = 0;
                lowTempCount = 0;
            } else if (temp > 15) {
                heightTempCount++;
                normalTempCount = 0;
                lowTempCount = 0;
            } else if (temp < 6) {
                lowTempCount++;
                heightTempCount = 0;
                normalTempCount = 0;
            }
            if (normalTempCount >= 3) {
                mExecuteState = STATE_FINISH;
                normalTempCount = 0;
                stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                stepEntities.get(0).setStepTitle(String.format(HardwareTestApplication.getContext().getResources().
                        getString(R.string.label_temperature),temp));
            } else if (heightTempCount >= 3 || lowTempCount >= 3) {
                mExecuteState = STATE_TEST_UNPASS;
                heightTempCount = 0;
                stepEntities.get(0).setTestState(Constants.TestItemState.STATE_FAIL);
                stepEntities.get(0).setStepTitle(String.format(HardwareTestApplication.getContext().getResources().
                        getString(R.string.label_temperature),temp));
            }
            LogTools.p(TAG, "温度值 temp=" + temp);
            Thread.sleep(1000);
        }
    }

    @Override
    protected void startTest() {
        super.startTest();
        normalTempCount = 0;
        heightTempCount = 0;
        lowTempCount = 0;
    }
}
