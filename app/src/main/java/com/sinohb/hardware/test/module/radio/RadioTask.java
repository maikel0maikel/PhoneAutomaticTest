package com.sinohb.hardware.test.module.radio;

import com.marsir.radio.RadioExManager;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;
import java.util.List;

public class RadioTask extends BaseManualTestTask {
    private static final int STEP_OPEN = 1;
    private static final int SETP_OPEN_FINISH = 2;
    private static final int SETP_SEARCH = 3;
    private static final int SETP_SEARCH_FINISH = 4;
    private static final int SETP_PLAY = 5;
    private static final int SETP_CLOSE = 6;
    private static final int STEP_RESET = 7;
    private static final int STEP_RESET_FINISHED = 8;

    private static final int STEP_OPEN_FAIL = 9;
    private static final int STEP_RESET_FAIL = 10;

    private static final int STEP_CLOSE_FAIL = 11;
    private static final int STEP_CLOSE_FINISH = 12;
    private static final int STEP_SEARCH_FAIL = 13;

    private List<Integer> freqLists = new ArrayList<>();

    private static final int[] STEP_TITLES = {R.string.label_radio_open,
            R.string.label_radio_search, R.string.label_radio_play, R.string.label_radio_close};

    public RadioTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_RADIO;
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        int i = 1;
        for (int res : STEP_TITLES) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().
                    getResources().getString(res), Constants.TestItemState.STATE_TESTING);
            stepEntities.add(stepEntity);
            i++;
        }
    }

    public void notifyRadioState(int state) {
        synchronized (mSync) {
            switch (state) {
                case RadioExManager.STATE_ON:
                    if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FINISH;
                        mSync.notify();
                    } else if (mTestStep == SETP_SEARCH) {
                        mTestStep = SETP_SEARCH_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FAIL;
                        mSync.notify();
                    } else if (mTestStep == SETP_CLOSE) {
                        mTestStep = STEP_CLOSE_FAIL;
                        mSync.notify();
                    }
                    break;
                case RadioExManager.STATE_OFF:
                    if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                        mSync.notify();
                    } else if (mTestStep == SETP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_OPEN) {
                        mTestStep = STEP_OPEN_FAIL;
                        mSync.notify();
                    } else if (mTestStep == SETP_SEARCH) {
                        mTestStep = SETP_SEARCH_FINISH;
                        mSync.notify();
                    }
                    break;
                case RadioExManager.SEARCH_STOP:
                    if (mTestStep == SETP_SEARCH) {
                        mTestStep = SETP_SEARCH_FINISH;
                        mSync.notify();
                    }
                    break;
            }
        }
    }

    public void notifyRadioFreq(int freq) {
        if (freq > 0 && !freqLists.contains(freq)) {
            freqLists.add(freq);
        }
    }

    @Override
    protected void startTest() {
        mTestStep = STEP_OPEN;
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int operateState;
        ((RadioPresenter.Controller) mPresenter).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            LogTools.p(TAG, "mTestStep:" + mTestStep);
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_OPEN:
                        LogTools.p(TAG, "收音机测试打开");
                        operateState = ((RadioPresenter.Controller) mPresenter).openRadio();
                        if (operateState == Constants.DEVICE_NOT_SUPPORT) {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "收音机打开失败，设备不支持");
                            break;
                        } else if (operateState == Constants.DEVICE_RESET) {
                            LogTools.p(TAG, "收音机处于打开状态，关闭后重新打开");
                            mTestStep = STEP_RESET;
                            break;
                        }
                        stepWait(STEP_OPEN, "收音机打开失败，超时");
                        break;
                    case SETP_OPEN_FINISH:
                        LogTools.p(TAG, "收音机打开成功");
                        mTestStep = SETP_SEARCH;
                        ((RadioPresenter.Controller) mPresenter).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
                        stepSuccess(1, 0);
                        break;
                    case STEP_OPEN_FAIL:
                        LogTools.p(TAG, "收音机打开失败");
                        mExecuteState = STATE_TEST_UNPASS;
                        break;
                    case STEP_RESET:
                        if (stepEntities.size() == STEP_TITLES.length) {
                            StepEntity stepEntity = new StepEntity(0, HardwareTestApplication.getContext().getResources().
                                    getString(R.string.label_radio_reset), Constants.TestItemState.STATE_TESTING);
                            stepEntities.add(0, stepEntity);
                        }
                        LogTools.p(TAG, "收音机重置");
                        operateState = ((RadioPresenter.Controller) mPresenter).closeRadio();
                        if (operateState == Constants.DEVICE_NOT_SUPPORT) {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "收音机重置失败，设备不支持");
                            break;
                        } else if (operateState == Constants.DEVICE_NORMAL) {
                            mTestStep = STEP_OPEN;
                            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                            break;
                        }
                        stepWait(STEP_RESET, "收音机重置失败，超时");
                        break;
                    case STEP_RESET_FINISHED:
                        stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mTestStep = STEP_OPEN;
                        Thread.sleep(500);
                        LogTools.p(TAG, "收音机重置成功");
                        break;
                    case STEP_RESET_FAIL:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "收音机重置失败");
                        break;
                    case SETP_SEARCH:
                        LogTools.p(TAG, "收音机测试搜索电台");
                        Thread.sleep(1000);
                        operateState = ((RadioPresenter.Controller) mPresenter).search();
                        if (operateState == Constants.DEVICE_STATE_ERROR) {
                            mExecuteState = STATE_TEST_UNPASS;
                            break;
                        }
                        mSync.wait(40 * 1000);
                        if (mTestStep == SETP_SEARCH) {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "收音机搜索失败，超时");
                        }
                        break;
                    case SETP_SEARCH_FINISH:
                        LogTools.p(TAG, "搜索完成进入播放");
                        mTestStep = SETP_PLAY;
                        stepSuccess(2, 1);
                        break;
                    case STEP_SEARCH_FAIL:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "收音机搜索失败");
                        break;
                    case SETP_PLAY:
                        LogTools.p(TAG, "收音机测试播放");
                        if (freqLists.isEmpty()) {
                            ((RadioPresenter.Controller) mPresenter).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
                            Thread.sleep(1500);
                        } else {
                            while (!freqLists.isEmpty()) {
                                int freq = freqLists.remove(0);
                                LogTools.p(TAG, "收音机播放电台：" + freq);
                                operateState = ((RadioPresenter.Controller) mPresenter).play(freq);
                                if (operateState == Constants.DEVICE_STATE_ERROR) {

                                }
                                Thread.sleep(5000);
                            }
                            stepSuccess(3, 2);
                        }
                        mTestStep = SETP_CLOSE;
                        break;
                    case SETP_CLOSE:
                        LogTools.p(TAG, "收音机测试关闭");
                        operateState = ((RadioPresenter.Controller) mPresenter).closeRadio();
                        if (operateState == Constants.DEVICE_NORMAL) {
                            LogTools.p(TAG, "收音机测试结束");
                            mExecuteState = STATE_FINISH;
                            break;
                        }
                        stepWait(SETP_CLOSE, "收音机关闭失败，超时");
                        break;
                    case STEP_CLOSE_FAIL:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "收音机关闭失败");
                        break;
                    case STEP_CLOSE_FINISH:
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mExecuteState = STATE_TEST_WAIT_OPERATE;
                        break;
                }
            }
            Thread.sleep(100);
        }
    }

    private void stepSuccess(int i, int i2) {
        if (stepEntities.size() > STEP_TITLES.length) {
            stepEntities.get(i).setTestState(Constants.TestItemState.STATE_SUCCESS);
        } else {
            stepEntities.get(i2).setTestState(Constants.TestItemState.STATE_SUCCESS);
        }
    }

    private void stepWait(int step, String s) throws InterruptedException {
        mSync.wait(TASK_WAITE_TIME);
        if (mTestStep == step) {
            mExecuteState = STATE_TEST_UNPASS;
            LogTools.p(TAG, s);
        }
    }

}
