package com.sinohb.hardware.test.module.effect;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class EffectTask extends BaseManualTestTask{
    private static final int STEP_PLAY_NONE_EFFECT = 1;
    private static final int STEP_PLAY_EFFECT = 2;
    private static final int STEP_CLOSE = 3;

    public EffectTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_EFFECT;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_sound_effect_play_none), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_sound_effect_play), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
    }

    @Override
    protected void startTest() {
        mTestStep = STEP_PLAY_NONE_EFFECT;
        super.startTest();
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((EffectPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            switch (mTestStep) {
                case STEP_PLAY_NONE_EFFECT:
                    LogTools.p(TAG, "无音效播放");
                    ((EffectPresenter.Controller) mPresenter).playNormal();
                    Thread.sleep(5000);
                    mTestStep = STEP_PLAY_EFFECT;
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    break;
                case STEP_PLAY_EFFECT:
                    ((EffectPresenter.Controller) mPresenter).playEffect();
                    LogTools.p(TAG, "播放音效");
                    Thread.sleep(5000);
                    mTestStep = STEP_CLOSE;
                    stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    break;
                case STEP_CLOSE:
                    ((EffectPresenter.Controller) mPresenter).closeEffect();
                    LogTools.p(TAG, "播放音效结束");
                    mExecuteState = STATE_TEST_WAIT_OPERATE;
                    break;
            }
        }
    }
}
