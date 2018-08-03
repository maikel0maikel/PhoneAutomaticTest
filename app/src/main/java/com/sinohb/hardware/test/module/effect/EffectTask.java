package com.sinohb.hardware.test.module.effect;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.module.audoex.EffectManagerable;
import com.sinohb.hardware.test.task.BaseManualTestTask;

public class EffectTask extends BaseManualTestTask {
    //    private static final int STEP_PLAY_NONE_EFFECT = 1;
//    private static final int STEP_PLAY_EFFECT = 2;
//    private static final int STEP_CLOSE = 3;
    private static final int[] STEP_TITLES = {R.string.label_effect_none, R.string.label_effect_classical,
            R.string.label_effect_jazz, R.string.label_effect_rock,
            R.string.label_effect_folk, R.string.label_effect_pop};

    public EffectTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_EFFECT;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
//        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_sound_effect_play_none), Constants.TestItemState.STATE_TESTING);
//        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().getString(R.string.label_test_sound_effect_play), Constants.TestItemState.STATE_TESTING);
//        stepEntities.add(stepEntity1);
//        stepEntities.add(stepEntity2);
        int i = 1;
        for (int res : STEP_TITLES) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().
                    getResources().getString(res), Constants.TestItemState.STATE_TESTING);
            stepEntities.add(stepEntity);
            i++;
        }
    }

//    @Override
//    protected void startTest() {
//        mTestStep = STEP_PLAY_NONE_EFFECT;
//        super.startTest();
//    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((EffectPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
//            switch (mTestStep) {
//                case STEP_PLAY_NONE_EFFECT:
//                    LogTools.p(TAG, "无音效播放");
//                    ((EffectPresenter.Controller) mPresenter.get()).playNormal();
//                    Thread.sleep(5000);
//                    mTestStep = STEP_PLAY_EFFECT;
//                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
//                    break;
//                case STEP_PLAY_EFFECT:
//                    ((EffectPresenter.Controller) mPresenter.get()).playEffect();
//                    LogTools.p(TAG, "播放音效");
//                    Thread.sleep(5000);
//                    mTestStep = STEP_CLOSE;
//                    stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
//                    break;
//                case STEP_CLOSE:
//                    ((EffectPresenter.Controller) mPresenter.get()).closeEffect();
//                    LogTools.p(TAG, "播放音效结束");
//                    mExecuteState = STATE_TEST_WAIT_OPERATE;
//                    break;
//            }
            /**
             * 修改为界面点击
             */
            synchronized (mSync) {
                mSync.wait();
            }
        }
    }

    public void notifyPlayEffect(int effect) {
        switch (effect) {
            case EffectManagerable.EQ_MODE_NORMAL:
                stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case EffectManagerable.EQ_MODE_CLASSIC:
                stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case EffectManagerable.EQ_MODE_JAZZ:
                stepEntities.get(2).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case EffectManagerable.EQ_MODE_POP:
                stepEntities.get(3).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case EffectManagerable.EQ_MODE_FOLK:
                stepEntities.get(4).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;
            case EffectManagerable.EQ_MODE_ROCK:
                stepEntities.get(5).setTestState(Constants.TestItemState.STATE_SUCCESS);
                break;

        }
    }
}
