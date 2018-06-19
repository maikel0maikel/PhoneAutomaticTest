package com.sinohb.hardware.test.module.screenadjust;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class ScreenAdjustTask extends BaseTestTask {
    private static final String TAG = "ScreenAdjustTask";
    private static final int SETP_ADJUST_LEFT_TOP = 0;
    private static final int SETP_ADJUST_RGIHT_TOP = 1;
    private static final int SETP_ADJUST_LEFT_BOTTOM = 2;
    private static final int SETP_ADJUST_RGIHT_BOTTOM = 3;
    private static final int SETP_ADJUST_CENTER = 4;

    public ScreenAdjustTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        ScreenAdjustPresenter.Controller controller = (ScreenAdjustPresenter.Controller) mPresenter;
        LogTools.p(TAG, "屏幕校准开始");
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = SETP_ADJUST_LEFT_TOP;
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            switch (mTestStep) {
                                case SETP_ADJUST_LEFT_TOP:
                                    controller.adjustLeftTop();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_RGIHT_TOP:
                                    controller.adjustRightTop();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_LEFT_BOTTOM:
                                    controller.adjustLeftBottom();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_RGIHT_BOTTOM:
                                    controller.adjustRightBottom();
                                    mSync.wait();
                                    break;
                                case SETP_ADJUST_CENTER:
                                    controller.adjustCenter();
                                    mSync.wait();
                                    break;
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "屏幕校准暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    controller.complete();
                    LogTools.p(TAG,"屏幕校准完成");
                    isFinish = true;
                    break;
            }

        }
        return true;
    }

    public void adjustOk(int direction) {
        LogTools.p(TAG,"校准成功 direction ="+direction);
        synchronized (mSync) {
            if (direction==SETP_ADJUST_CENTER){
                mExecuteState = STATE_FINISH;
            }else {
                mTestStep = direction + 1;
            }

            mSync.notify();
        }
    }

    public void adjustFailure(int direction) {
        LogTools.p(TAG,"校准失败 direction ="+direction);
        synchronized (mSync) {
            mTestStep = direction;
            mSync.notify();
        }
    }
}
