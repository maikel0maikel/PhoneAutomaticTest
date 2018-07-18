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

    private List<Integer> freqLists = new ArrayList<>();

    private static final int[] STEP_TITLES = {R.string.label_radio_open,
            R.string.label_radio_search, R.string.label_radio_play, R.string.label_radio_close};

    public RadioTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_RADIO;
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
                    } else {
//                        mExecuteState = STATE_TEST_UNPASS;
//                        mSync.notify();
                    }
                    break;
                case RadioExManager.STATE_OFF:
                    if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                        mSync.notify();
                    } else if (mTestStep == SETP_CLOSE) {
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mExecuteState = STATE_TEST_WAIT_OPERATE;
                        mSync.notify();
                    }
                case RadioExManager.SEARCH_STOP:
                    if (mTestStep == SETP_SEARCH) {
                        mTestStep = SETP_PLAY;
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
        stepEntities.clear();
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int operateState = 0;
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
                            StepEntity stepEntity1 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_open), Constants.TestItemState.STATE_FAIL);
                            stepEntities.add(stepEntity1);
                            addStepEntity(stepEntities.size());
                            break;
                        } else if (operateState == Constants.DEVICE_RESET) {
                            LogTools.p(TAG, "收音机处于打开状态，关闭后重新打开");
                            mTestStep = STEP_RESET;
                            StepEntity stepEntity = new StepEntity(1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_reset), Constants.TestItemState.STATE_TESTING);
                            stepEntities.add(stepEntity);
                            break;
                        }
                        StepEntity stepEntity1 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_open), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity1);
                        mSync.wait();
                        break;
                    case SETP_OPEN_FINISH:
                        LogTools.p(TAG, "收音机打开成功");
                        mTestStep = SETP_SEARCH;
                        ((RadioPresenter.Controller) mPresenter).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        break;
                    case STEP_RESET:
                        LogTools.p(TAG, "收音机重置");
                        operateState = ((RadioPresenter.Controller) mPresenter).closeRadio();
                        if (operateState == Constants.DEVICE_NOT_SUPPORT) {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "收音机关闭失败，设备不支持");
                            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(0);
                            break;
                        } else if (operateState == Constants.DEVICE_NORMAL) {
                            mTestStep = STEP_OPEN;
                            stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                            break;
                        }
                        mSync.wait();
                        break;
                    case SETP_SEARCH:
                        LogTools.p(TAG, "收音机测试搜索电台");
                        Thread.sleep(1000);
                        StepEntity stepEntity2 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_search), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity2);
                        operateState = ((RadioPresenter.Controller) mPresenter).search();
                        if (operateState == Constants.DEVICE_STATE_ERROR) {
                            mTestStep = STEP_OPEN;
                            stepEntities.clear();
                            break;
                        }
                        mSync.wait();
                        break;
                    case STEP_RESET_FINISHED:
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        mTestStep = STEP_OPEN;
                        Thread.sleep(500);
                        LogTools.p(TAG, "重置成功");
                        break;
                    case SETP_SEARCH_FINISH:
                        LogTools.p(TAG, "搜索完成进入播放");
                        mTestStep = SETP_PLAY;
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        break;
                    case SETP_PLAY:
                        LogTools.p(TAG, "收音机测试播放");
                        StepEntity stepEntity3 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_play), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity3);
                        if (freqLists.isEmpty()) {
                            ((RadioPresenter.Controller) mPresenter).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
                            Thread.sleep(1500);
                            stepEntity3.setTestState(Constants.TestItemState.STATE_FAIL);
                        } else {
                            while (!freqLists.isEmpty()) {
                                int freq = freqLists.remove(0);
                                LogTools.p(TAG, "收音机播放电台：" + freq);
                                operateState = ((RadioPresenter.Controller) mPresenter).play(freq);
                                if (operateState == Constants.DEVICE_STATE_ERROR) {

                                }
                                Thread.sleep(5000);
                            }
                            stepEntity3.setTestState(Constants.TestItemState.STATE_SUCCESS);
                        }
                        mTestStep = SETP_CLOSE;
                        break;
                    case SETP_CLOSE:
                        LogTools.p(TAG, "收音机测试关闭");
                        StepEntity stepEntity4 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_radio_close), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity4);
                        operateState = ((RadioPresenter.Controller) mPresenter).closeRadio();
                        if (operateState == Constants.DEVICE_NORMAL) {
                            LogTools.p(TAG, "收音机测试结束");
                            mExecuteState = STATE_FINISH;
                            stepEntity4.setTestState(Constants.TestItemState.STATE_SUCCESS);
                            break;
                        }
                        mSync.wait();
                        break;

                }
            }
            Thread.sleep(100);
        }
    }

    protected void addStepEntity(int pos) {
        for (int i = pos; i < STEP_TITLES.length; i++) {
            StepEntity stepEntity1 = new StepEntity(i, HardwareTestApplication.getContext().getResources().getString(STEP_TITLES[i]), Constants.TestItemState.STATE_FAIL);
            stepEntities.add(stepEntity1);
        }
    }

    @Override
    protected void unpass() {
        super.unpass();
        addStepEntity(0);
    }
}
