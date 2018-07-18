package com.sinohb.hardware.test.module.rgb;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseManualTestTask;

public class RGBTask extends BaseManualTestTask {
    public RGBTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((BaseDisplayViewPresenter) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                mSync.wait();
            }
        }
    }

}
