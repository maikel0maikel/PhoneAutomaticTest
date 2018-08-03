package com.sinohb.hardware.test.module.volume;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class VolumeTask extends BaseTestTask {
    private static final String TAG = "ScreenTask";
    private static final long STEP_TIME = 3000;
    private static final int STEP_L = 1;
    private static final int STEP_M = 2;
    private static final int STEP_H = 3;

    public VolumeTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Integer call() throws Exception {
        VolumePresenter.Controller controller = (VolumePresenter.Controller) mPresenter.get();
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_L;
                    LogTools.p(TAG, "音量测试开始");
                    Thread.sleep(STEP_TIME);
                    LogTools.p(TAG,"先播放3s");
                    //testing(controller);
                    break;
                case STATE_RUNNING:
                    testing(controller);
                    break;
                case STATE_PAUSE:
                    LogTools.p(TAG, "暂停音量测试任务");
                    synchronized (mSync) {
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    controller.complete();
                    isFinish = true;
                    break;
                case STATE_STEP_FINSH:
                    if (mTestStep == STEP_L) {
                        mTestStep = STEP_M;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_M) {
                        mTestStep = STEP_H;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_H) {
                        mExecuteState = STATE_FINISH;
                    }
                    break;
            }
        }
        LogTools.p(TAG, "音量测试结束");
        return isPass;
    }

    private void testing(VolumePresenter.Controller controller) throws InterruptedException {
        while (mExecuteState == STATE_RUNNING) {
            executeStep(controller);
            Thread.sleep(STEP_TIME);
            mExecuteState = STATE_STEP_FINSH;
        }
    }

    private void executeStep(VolumePresenter.Controller controller) {
        switch (mTestStep) {
            case STEP_L:
                controller.adjustLow();
                LogTools.p(TAG, "测试低音");
                break;
            case STEP_M:
                controller.adjustMedium();
                LogTools.p(TAG, "测试中音");
                break;
            case STEP_H:
                controller.adjustHight();
                LogTools.p(TAG, "测试高音");
                break;
        }
    }


}
