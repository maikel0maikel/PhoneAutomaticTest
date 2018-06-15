package com.sinohb.hardware.test.task;

import com.sinohb.hardware.test.app.BasePresenter;

import java.util.concurrent.Callable;

public abstract class BaseTestTask implements Callable<Boolean> {
    protected static final int STATE_NONE = 0;
    protected static final int STATE_RUNNING = 1;
    protected static final int STATE_PAUSE = 2;
    protected static final int STATE_FINISH = 3;
    protected static final int STATE_STEP_FINSH = 4;
    protected static final long TASK_WAITE_TIME = 1000 * 10;//15s
    protected int mExecuteState = STATE_NONE;
    protected int mTaskId;
    protected String mTaskName;
    protected boolean isFinish;
    protected Object mSync = new Object();
    protected int mTestStep = 0;
    protected int mPreStep = 0;
    protected BasePresenter mPresenter;

    public BaseTestTask(BasePresenter presenter) {
        this.mPresenter = presenter;
    }

    public int getmExecuteState() {
        return mExecuteState;
    }

    public void setmExecuteState(int mExecuteState) {
        this.mExecuteState = mExecuteState;
    }

    public int getmTaskId() {
        return mTaskId;
    }

    public void setmTaskId(int mTaskId) {
        this.mTaskId = mTaskId;
    }

    public String getmTaskName() {
        return mTaskName;
    }

    public void setmTaskName(String mTaskName) {
        this.mTaskName = mTaskName;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public BasePresenter getPresenter() {
        return mPresenter;
    }

    public void setPresenter(BasePresenter presenter) {
        this.mPresenter = presenter;
    }

    public void resume() {
        synchronized (mSync) {
            mExecuteState = STATE_RUNNING;
            mSync.notify();
        }

    }

    public void pause() {
        mExecuteState = STATE_PAUSE;
    }
    public void stopTask() {
        synchronized (mSync) {
            isFinish = true;
            mSync.notify();
        }
    }
}
