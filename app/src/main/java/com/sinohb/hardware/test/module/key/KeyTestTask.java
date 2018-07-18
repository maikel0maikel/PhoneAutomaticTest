package com.sinohb.hardware.test.module.key;

import android.view.KeyEvent;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

public class KeyTestTask extends BaseManualTestTask {
    private static final int STEP_PRESS_KEY_HOME = 0;
    private static final int SETP_PRESS_KEY_UP = 1;
    private static final int SETP_PRESS_KEY_DOWN = 2;
    private static final int SETP_PRESS_KEY_ENTER = 3;
    private static final int SETP_PRESS_KEY_BACK = 4;
    private static final int SETP_PRESS_KEY_ERROR = 5;
    private static final int STEP_KEY_FINISH = 6;
    private int errorCount = 0;
    private int keyErrorCount = 0;
    public KeyTestTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_KEY;
    }


    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(SETP_PRESS_KEY_UP, HardwareTestApplication.getContext().getResources().getString(R.string.label_key_up), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(SETP_PRESS_KEY_DOWN, HardwareTestApplication.getContext().getResources().getString(R.string.label_key_down), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity3 = new StepEntity(SETP_PRESS_KEY_ENTER, HardwareTestApplication.getContext().getResources().getString(R.string.label_key_enter), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity4 = new StepEntity(SETP_PRESS_KEY_BACK, HardwareTestApplication.getContext().getResources().getString(R.string.label_key_back), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
        stepEntities.add(stepEntity3);
        stepEntities.add(stepEntity4);

    }

    private boolean breakCondition(int nextStep) {
        if (errorCount >= 3) {
            //mExecuteState = STATE_TEST_UNPASS;
            stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_FAIL);
            mTestStep = nextStep;
            errorCount = 0;
            keyErrorCount++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public void notifyTestKey(int keyCode) {
        synchronized (mSync) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    if (notifyPressKey(STEP_PRESS_KEY_HOME)) {
                        mTestStep = SETP_PRESS_KEY_UP;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (notifyPressKey(SETP_PRESS_KEY_UP)) {
                        mTestStep = SETP_PRESS_KEY_DOWN;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (notifyPressKey(SETP_PRESS_KEY_DOWN)) {
                        mTestStep = SETP_PRESS_KEY_ENTER;
                    }
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    if (notifyPressKey(SETP_PRESS_KEY_ENTER)) {
                        mTestStep = SETP_PRESS_KEY_BACK;
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if (notifyPressKey(SETP_PRESS_KEY_BACK)) {
                        mTestStep = STEP_KEY_FINISH;
                    }
                    break;
                default:
                    mTestStep = SETP_PRESS_KEY_ERROR;
                    break;
            }
            mSync.notify();
        }
    }

    private boolean notifyPressKey(int setpPressKey) {
        boolean isOk = false;
        if (mTestStep == setpPressKey) {
            errorCount = 0;
            isOk = true;
            stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_SUCCESS);
        } else {
            //stepEntities.get(mTestStep-1).setTestState(Constants.TestItemState.STATE_FAIL);
            mTestStep = SETP_PRESS_KEY_ERROR;
        }
        return isOk;
    }

    @Override
    protected void startTest() {
        super.startTest();
        mTestStep = SETP_PRESS_KEY_UP;
        keyErrorCount = 0;
        errorCount = 0;
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((KeyPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_PRESS_KEY_HOME:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KEY_PRESS_HOME, errorCount);
                        LogTools.p(TAG, "菜单键测试");
                        if (breakCondition(SETP_PRESS_KEY_UP)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KEY_UP:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KEY_PRESS_UP, errorCount);
                        LogTools.p(TAG, "向上键测试");
                        if (breakCondition(SETP_PRESS_KEY_DOWN)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KEY_DOWN:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KEY_PRESS_DOWN, errorCount);
                        LogTools.p(TAG, "向下键测试");
                        if (breakCondition(SETP_PRESS_KEY_ENTER)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KEY_ENTER:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KEY_PRESS_ENTER, errorCount);
                        LogTools.p(TAG, "确定键测试");
                        if (breakCondition(SETP_PRESS_KEY_BACK)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KEY_BACK:
                        mPreStep = mTestStep;
                        ((KeyPresenter.Controller) mPresenter).pressKey(Constants.HandlerMsg.MSG_KEY_PRESS_BACK, errorCount);
                        LogTools.p(TAG, "返回键测试");
                        if (breakCondition(STEP_KEY_FINISH)) break;
                        mSync.wait();
                        break;
                    case SETP_PRESS_KEY_ERROR:
                        errorCount++;
                        mTestStep = mPreStep;
                        LogTools.p(TAG, "按错按键---errorCount:" + errorCount);
                        break;
                    case STEP_KEY_FINISH:
                        if (keyErrorCount>0){
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG,"按键测试不通过");
                        }else {
                            mExecuteState = STATE_FINISH;
                            LogTools.p(TAG,"按键测试通过");
                        }
                        break;
                }
            }
        }
    }
}
