package com.sinohb.hardware.test.task;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.logger.LogTools;

public class BaseAutoTestTask extends BaseTestTask{

    public BaseAutoTestTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Integer call() throws Exception {
        BaseExecutePresenter controller = (BaseExecutePresenter) mPresenter;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    isPass = 0;
                    controller.notifyExecuteState(STATE_NONE);
                    Thread.sleep(500);
                    mExecuteState = STATE_RUNNING;
                    startTest();
                    LogTools.p(TAG, "测试开始");
                    break;
                case STATE_RUNNING:
                    controller.notifyExecuteState(STATE_RUNNING);
                    executeRunningState();
                    break;
                case STATE_PAUSE:
                    LogTools.p(TAG, "暂停测试任务");
                    controller.notifyExecuteState(STATE_PAUSE);
                    synchronized (mSync) {
                        mSync.wait();
                    }
                    break;
                case STATE_TEST_WAIT_OPERATE:
                    executeWaitState();
                    break;
                case STATE_FINISH:
                    //controller.complete();
                    descriptionSrc = R.string.label_auto_test_detail_success_hint;
                    isFinish = true;
                    isPass = 1;
                    LogTools.p(TAG, "测试任务完成");
                    controller.notifyExecuteState(STATE_FINISH);
                    break;
                case STATE_TEST_UNPASS:
                    descriptionSrc = R.string.label_auto_test_detail_fail_hint;
                    isFinish = true;
                    LogTools.p(TAG, "测试不通过");
                    isPass = 0;
                    controller.notifyExecuteState(STATE_TEST_UNPASS);
                    unpass();
                    break;
            }
        }
        LogTools.p(TAG, "结束测试任务");
        controller.complete();
        return isPass;
    }

    protected void executeWaitState() throws InterruptedException {

    }

}
