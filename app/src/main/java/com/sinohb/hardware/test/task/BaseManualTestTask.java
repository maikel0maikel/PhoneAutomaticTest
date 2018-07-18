package com.sinohb.hardware.test.task;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.logger.LogTools;

public class BaseManualTestTask extends BaseTestTask{
    private static final long STEP_TIME = 500;
    private static final long HINT_TIME = 200;
    public BaseManualTestTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Integer call() throws Exception {
        BaseDisplayViewPresenter controller = (BaseDisplayViewPresenter) mPresenter;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    isPass = 0;
                    LogTools.p(TAG, "测试任务开始");
                    controller.displayView();
                    controller.notifyExecuteState(STATE_NONE);
                    Thread.sleep(STEP_TIME);
                    mExecuteState = STATE_RUNNING;
                    startTest();
                    break;
                case STATE_RUNNING:
                    LogTools.p(TAG, "测试任务进行中");
                    executeRunningState();
                    break;
                case STATE_PAUSE:
                    controller.notifyExecuteState(STATE_PAUSE);
                    synchronized (mSync) {
                        LogTools.p(TAG, "测试任务暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_STEP_FINSH:
                    executeStateStepFinish();
                    break;
                case STATE_FINISH:
                    descriptionSrc = R.string.label_manual_test_detail_success_hint;
                    LogTools.p(TAG, "测试任务完成");
                    isFinish = true;
                    isPass = 1;
                    controller.notifyExecuteState(STATE_FINISH);
                    Thread.sleep(HINT_TIME);
                    break;
                case STATE_TEST_WAIT_OPERATE:
                    LogTools.p(TAG,"等待用户操作......");
                    Thread.sleep(HINT_TIME);
                    controller.notifyExecuteState(STATE_TEST_WAIT_OPERATE);
                    synchronized (mSync){
                        mSync.wait();
                    }
                    break;
                case STATE_TEST_UNPASS:
                    descriptionSrc = R.string.label_manual_test_detail_fail_hint;
                    LogTools.p(TAG,"测试不通过--");
                    isFinish = true;
                    isPass = 0;
                    controller.notifyExecuteState(STATE_TEST_UNPASS);
                    Thread.sleep(HINT_TIME);
                    unpass();
                    break;
            }
        }
        controller.complete();
        return isPass;
    }
    protected void executeStateStepFinish() throws InterruptedException {

    }

}
