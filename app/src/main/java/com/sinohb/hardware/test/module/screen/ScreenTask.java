package com.sinohb.hardware.test.module.screen;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class ScreenTask extends BaseTestTask {
    private static final String TAG = "ScreenTask";
    private static final long STEP_TIME = 2000;
    private static final int STEP_R = 1;
    private static final int STEP_G = 2;
    private static final int STEP_B = 3;

    public ScreenTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        boolean pass = true;
        ScreenPresenter.Controller controller = (ScreenPresenter.Controller) mPresenter;
        while (!isFinish) {
            LogTools.p(TAG, "屏幕测试开始");
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_R;
                    //testing(controller);
                    break;
                case STATE_RUNNING:
                    testing(controller);
                    break;
                case STATE_PAUSE:
                    LogTools.p(TAG, "暂停屏幕测试任务");
                    synchronized (mSync) {
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    controller.complete();
                    isFinish = true;
                    break;
                case STATE_STEP_FINSH:
                    if (mTestStep == STEP_R) {
                        mTestStep = STEP_G;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_G) {
                        mTestStep = STEP_B;
                        mExecuteState = STATE_RUNNING;
                    } else if (mTestStep == STEP_B) {
                        mExecuteState = STATE_FINISH;
                    }
                    break;
            }
        }
        LogTools.p(TAG, "屏幕测试结束");
        return pass;
    }

    private void testing(ScreenPresenter.Controller controller) throws InterruptedException {
        while (mExecuteState == STATE_RUNNING) {
            executeStep(controller);
            Thread.sleep(STEP_TIME);
            mExecuteState = STATE_STEP_FINSH;
        }
    }

    private void executeStep(ScreenPresenter.Controller controller) {
        switch (mTestStep) {
            case STEP_R:
                controller.displayR();
                LogTools.p(TAG, "测试显示红色");
                break;
            case STEP_G:
                controller.displayG();
                LogTools.p(TAG, "测试显示绿色");
                break;
            case STEP_B:
                controller.displayB();
                LogTools.p(TAG, "测试显示蓝色");
                break;
        }
    }


}
