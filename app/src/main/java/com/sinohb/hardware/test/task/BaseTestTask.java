package com.sinohb.hardware.test.task;

import android.support.annotation.NonNull;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.entities.StepEntity;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class BaseTestTask implements Callable<Integer>,Comparable<BaseTestTask> {
    protected static final String TAG = BaseTestTask.class.getSimpleName();
    public static final int AUTOMATIC = 1000;
    public static final int MANUAL = 1001;
    public static final int STATE_NONE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_FINISH = 3;
    public static final int STATE_STEP_FINSH = 4;
    public static final int STATE_TEST_UNPASS = 5;
    public static final int STATE_TEST_WAIT_OPERATE = 6;
    protected static final long TASK_WAITE_TIME = 1000 * 10;//15s
    protected int mExecuteState = STATE_NONE;
    protected int mTaskId;
    protected String mTaskName;
    protected boolean isFinish;
    protected final Object mSync = new Object();
    protected int mTestStep = 0;
    protected int mPreStep = 0;
    protected BasePresenter mPresenter;
    protected int isPass = 0;

    protected int descriptionSrc;

    private boolean isManaul;

    protected ArrayList<StepEntity> stepEntities = new ArrayList<>();

    public BaseTestTask(BasePresenter presenter) {
        this.mPresenter = presenter;
        initStepEntity();
    }

    public int getmExecuteState() {
        return mExecuteState;
    }

    public void setmExecuteState(int mExecuteState) {
        synchronized (mSync){
            this.mExecuteState = mExecuteState;
            mSync.notify();
        }

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
        testFail();
    }

    public int isPass() {
        return isPass;
    }

    public void testOk() {
        synchronized (mSync) {
            mExecuteState = STATE_FINISH;
            mSync.notify();
        }
    }

    public void testFail() {
        synchronized (mSync) {
            mExecuteState = STATE_TEST_UNPASS;
            mSync.notify();
        }
    }
    protected void executeRunningState() throws InterruptedException {

    }

    protected void startTest(){
        for (StepEntity entity:stepEntities){
            entity.setTestState(Constants.TestItemState.STATE_TESTING);
        }
    }

    public ArrayList<StepEntity> getStepEntities() {
        return stepEntities;
    }

    public void setStepEntities(ArrayList<StepEntity> stepEntities) {
        this.stepEntities = stepEntities;
    }

    public int getDescriptionSrc() {
        return descriptionSrc;
    }
    protected void initStepEntity(){

    }

    public boolean isManaul() {
        return isManaul;
    }

    public void setManaul(boolean manaul) {
        isManaul = manaul;
    }

    @Override
    public int compareTo(@NonNull BaseTestTask another) {
        if (this.mTaskId>another.mTaskId){
            return 1;
        }else if (this.mTaskId == another.mTaskId){
            return 0;
        }else {
            return  -1;
        }
    }

    protected void unpass(){

    }
}
