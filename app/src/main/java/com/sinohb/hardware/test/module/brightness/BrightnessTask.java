package com.sinohb.hardware.test.module.brightness;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class BrightnessTask extends BaseTestTask {
    private static final String TAG = "BrightnessTask";
    private static final int STEP_L = 1;
    private static final int STEP_M = 2;
    private static final int STEP_H = 3;
    private static final long STEP_TIME = 3000;
    public BrightnessTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        BrightnessPresenter.Controller controller = (BrightnessPresenter.Controller) mPresenter;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_L;
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        executeStep(controller);
                        Thread.sleep(STEP_TIME);
                        mExecuteState = STATE_STEP_FINSH;
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "屏幕亮度调试任务暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    LogTools.p(TAG, "屏幕亮度调试任务完成");
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
        return true;
    }

    private void executeStep(BrightnessPresenter.Controller controller) {
        switch (mTestStep) {
            case STEP_L:
                controller.changeLow();
                LogTools.p(TAG, "亮度低");
                break;
            case STEP_M:
                controller.changeMedium();
                LogTools.p(TAG, "亮度中");
                break;
            case STEP_H:
                controller.changeHigh();
                LogTools.p(TAG, "亮度高");
                break;
        }
    }
}
