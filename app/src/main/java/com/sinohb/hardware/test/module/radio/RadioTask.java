package com.sinohb.hardware.test.module.radio;

import android.os.Environment;

import com.marsir.radio.RadioExManager;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.ConfigEntity;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.hardware.test.utils.FileUtils;
import com.sinohb.hardware.test.utils.JsonUtils;
import com.sinohb.logger.LogTools;

import java.io.IOException;

public class RadioTask extends BaseManualTestTask {
    private static final int STEP_OPEN = 1;
    private static final int STEP_OPEN_FINISH = 2;
    private static final int STEP_SEARCH_DOWN = 3;
    private static final int STEP_SEARCH_DOWN_FINISH = 4;
    private static final int STEP_PLAY = 5;
    private static final int STEP_CLOSE = 6;
    private static final int STEP_RESET = 7;
    private static final int STEP_RESET_FINISHED = 8;

    private static final int STEP_OPEN_FAIL = 9;
    private static final int STEP_RESET_FAIL = 10;

    private static final int STEP_CLOSE_FAIL = 11;
    private static final int STEP_CLOSE_FINISH = 12;
    private static final int STEP_SEARCH_DOWN_FAIL = 13;

    private static final int STEP_SEARCH_UP = 14;
    private static final int STEP_SEARCH_UP_FINISH = 15;
    private static final int STEP_SEARCH_UP_FAIL = 16;

    // private List<Integer> freqLists = new ArrayList<>();
    private boolean hasSearchTaget = false;

    private static final int[] STEP_TITLES = {R.string.label_radio_open,
            R.string.label_radio_search_down, R.string.label_radio_search_up, R.string.label_radio_close};
    private int mTestFMFrequency = 0;

    public RadioTask(BasePresenter presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_RADIO;
    }

    private void addStepEntity() {
        int i = 1;
        for (int res : STEP_TITLES) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().
                    getResources().getString(res), Constants.TestItemState.STATE_TESTING);
            stepEntities.add(stepEntity);
            i++;
        }
    }

    public void notifyRadioState(int state) {
        LogTools.p(TAG, "notifyRadioState state=" + state + ",mTestStep=" + mTestStep);
        if (state == RadioExManager.STATE_SEARCH_DOWN || state == RadioExManager.STATE_SEARCH_UP) {
            LogTools.p(TAG, "搜索开始----");
            return;
        }
        synchronized (mSync) {
            switch (state) {
                case RadioExManager.STATE_ON:
                    if (mTestStep == STEP_OPEN) {
                        mTestStep = STEP_OPEN_FINISH;
                    } else if (mTestStep == STEP_SEARCH_DOWN) {
                        mTestStep = STEP_SEARCH_DOWN_FINISH;
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FAIL;
                    } else if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FAIL;
                    } else if (mTestStep == STEP_SEARCH_UP) {
                        mTestStep = STEP_SEARCH_UP_FINISH;
                    }
                    break;
                case RadioExManager.STATE_OFF:
                    if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                    } else if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                    } else if (mTestStep == STEP_OPEN) {
                        mTestStep = STEP_OPEN_FAIL;
                    } else if (mTestStep == STEP_SEARCH_DOWN) {
                        mTestStep = STEP_SEARCH_DOWN_FAIL;
                    } else if (mTestStep == STEP_SEARCH_UP) {
                        mTestStep = STEP_SEARCH_UP_FAIL;
                    }
                    break;
//                case RadioExManager.SEARCH_STOP:
//                    if (mTestStep == STEP_SEARCH_DOWN) {
//                        mTestStep = STEP_SEARCH_DOWN_FINISH;
//                        mSync.notify();
//                    } else if (mTestStep == STEP_SEARCH_UP) {
//                        mTestStep = STEP_SEARCH_UP_FINISH;
//                        mSync.notify();
//                    }
//                    break;
            }
            mSync.notify();
        }
    }
//
//    public void notifyOpen(int open) {
//        synchronized (mSync) {
//            if (open == Constants.DEVICE_NOT_SUPPORT) {
//                mExecuteState = STATE_TEST_UNPASS;
//                LogTools.p(TAG, "收音机打开失败，设备不支持");
//                mSync.notify();
//            } else if (open == Constants.DEVICE_RESET) {
//                LogTools.p(TAG, "收音机处于打开状态，关闭后重新打开");
//                mTestStep = STEP_RESET;
//            }
//        }
//    }

    public void notifyRadioFreq(int freq) {
//        if (freq > 0 && !freqLists.contains(freq)) {
//            freqLists.add(freq);
//        }
        if (freq == mTestFMFrequency) {
            if (mTestStep == STEP_SEARCH_UP) {
                synchronized (mSync) {
                    mTestStep = STEP_SEARCH_UP_FINISH;
                    hasSearchTaget = true;
                    mSync.notify();
                }
            } else if (mTestStep == STEP_SEARCH_DOWN) {
                synchronized (mSync) {
                    mTestStep = STEP_SEARCH_DOWN_FINISH;
                    hasSearchTaget = true;
                    mSync.notify();
                }

            }
        }
    }

    @Override
    protected void startTest() {
        mTestStep = STEP_OPEN;
        if (!stepEntities.isEmpty()) {
            stepEntities.clear();
        }
        hasSearchTaget = false;
        try {
            String json = FileUtils.getFileContent(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Config.txt");
            ConfigEntity configEntity = JsonUtils.parse(json, ConfigEntity.class);
            if (configEntity != null && configEntity.radio != null) {
                mTestFMFrequency = (int) (configEntity.radio.frequency * 100);
                ((RadioPresenter.Controller) mPresenter.get()).play(mTestFMFrequency);
            }
        } catch (IOException e) {
            LogTools.p(TAG, "读取配置文件失败");
        } catch (Exception e) {
            LogTools.p(TAG, e, "json解析失败!");
        }
        LogTools.p(TAG, "配置电台为 " + mTestFMFrequency);
        if (mTestFMFrequency <= 0) {
            mExecuteState = STATE_TEST_UNPASS;
            STEP_TITLES[1] = R.string.label_radio_search_down_empty_config;
            STEP_TITLES[2] = R.string.label_radio_search_up_empty_config;
        }else {
            STEP_TITLES[1] = R.string.label_radio_search_down;
            STEP_TITLES[2] = R.string.label_radio_search_up;
        }
        addStepEntity();
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int operateState;
        ((RadioPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        while (mExecuteState == STATE_RUNNING) {
            LogTools.p(TAG, "mTestStep:" + mTestStep);
            switch (mTestStep) {
                case STEP_OPEN:
                    LogTools.p(TAG, "收音机测试打开");
                    operateState = ((RadioPresenter.Controller) mPresenter.get()).openRadio();
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
                case STEP_OPEN_FINISH:
                    LogTools.p(TAG, "收音机打开成功");
                    mTestStep = STEP_SEARCH_DOWN;
                    ((RadioPresenter.Controller) mPresenter.get()).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
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
                    operateState = ((RadioPresenter.Controller) mPresenter.get()).closeRadio();
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
                    mTestStep = STEP_OPEN;
                    Thread.sleep(500);
                    LogTools.p(TAG, "收音机重置成功");
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    break;
                case STEP_RESET_FAIL:
                    mExecuteState = STATE_TEST_UNPASS;
                    LogTools.p(TAG, "收音机重置失败");
                    break;
                case STEP_SEARCH_DOWN:
                    Thread.sleep(500);
                    LogTools.p(TAG, "向下搜索电台");
                    searchRadio(RadioManagerable.FORWARD, STEP_SEARCH_DOWN);
                    break;
                case STEP_SEARCH_DOWN_FINISH:
                    Thread.sleep(500);
                    int downFreq = ((RadioPresenter.Controller) mPresenter.get()).getCurrentFreq();
                    LogTools.p(TAG, "向下搜索完成，当前停台downFreq：" + downFreq);
                    if (hasSearchTaget && downFreq == mTestFMFrequency) {
                        stepSuccess(2, 1);
                    } else {
                        stepFail(2, 1, downFreq);
                    }
                    ((RadioPresenter.Controller) mPresenter.get()).notifyStopFreq(RadioManagerable.FORWARD, downFreq);
                    Thread.sleep(1000);
                    mTestStep = STEP_SEARCH_UP;
                    break;
                case STEP_SEARCH_DOWN_FAIL:
                    //mExecuteState = STATE_TEST_UNPASS;
                    mTestStep = STEP_SEARCH_UP;
                    LogTools.p(TAG, "向下搜索电台失败");
                    break;
                case STEP_SEARCH_UP:
                    LogTools.p(TAG, "向上搜索电台");
                    Thread.sleep(500);
                    searchRadio(RadioManagerable.BACKWARD, STEP_SEARCH_UP);
                    break;
                case STEP_SEARCH_UP_FINISH:
                    Thread.sleep(500);
                    int upFreq = ((RadioPresenter.Controller) mPresenter.get()).getCurrentFreq();
                    LogTools.p(TAG, "向上搜索完成，当前停台：" + upFreq);
                    if (hasSearchTaget && upFreq == mTestFMFrequency) {
                        stepSuccess(3, 2);
                    } else {
                        stepFail(3, 2, upFreq);
                    }
                    ((RadioPresenter.Controller) mPresenter.get()).notifyStopFreq(RadioManagerable.BACKWARD, upFreq);
                    Thread.sleep(1000);
                    mTestStep = STEP_CLOSE;
                    break;
                case STEP_SEARCH_UP_FAIL:
                    mExecuteState = STATE_TEST_UNPASS;
                    LogTools.p(TAG, "向上搜索失败");
                    break;
//                case STEP_PLAY:
//                    LogTools.p(TAG, "收音机测试播放");
//                    if (freqLists.isEmpty()) {
//                        ((RadioPresenter.Controller) mPresenter.get()).notifyExecuteState(Constants.HandlerMsg.MSG_RADIO_OPENED_HINT);
//                        Thread.sleep(1500);
//                    } else {
//                        while (!freqLists.isEmpty()) {
//                            int freq = freqLists.remove(0);
//                            LogTools.p(TAG, "收音机播放电台：" + freq);
//                            operateState = ((RadioPresenter.Controller) mPresenter.get()).play(freq);
//                            if (operateState == Constants.DEVICE_STATE_ERROR) {
//
//                            }
//                            Thread.sleep(5000);
//                        }
//                        stepSuccess(3, 2);
//                    }
//                    mTestStep = STEP_CLOSE;
//                    break;
                case STEP_CLOSE:
                    LogTools.p(TAG, "收音机测试关闭");
                    operateState = ((RadioPresenter.Controller) mPresenter.get()).closeRadio();
                    if (operateState == Constants.DEVICE_NORMAL) {
                        LogTools.p(TAG, "收音机测试结束");
                        mExecuteState = STATE_TEST_WAIT_OPERATE;
                        break;
                    } else if (operateState == Constants.DEVICE_NOT_SUPPORT) {
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "收音机关闭失败，设备不支持");
                        break;
                    }
                    stepWait(STEP_CLOSE, "收音机关闭失败，超时");
                    break;
                case STEP_CLOSE_FAIL:
                    mExecuteState = STATE_TEST_UNPASS;
                    LogTools.p(TAG, "收音机关闭失败");
                    break;
                case STEP_CLOSE_FINISH:
                    LogTools.p(TAG, "收音机关闭成功");
                    stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    mExecuteState = STATE_TEST_WAIT_OPERATE;
                    break;
            }
            Thread.sleep(100);
        }
    }

    private void searchRadio(int backward, int stepSearchUp) throws InterruptedException {
        synchronized (mSync) {
            int operateState;
            operateState = ((RadioPresenter.Controller) mPresenter.get()).search(backward, mTestFMFrequency);
            if (operateState == Constants.DEVICE_STATE_ERROR) {
                mExecuteState = STATE_TEST_UNPASS;
                return;
            }
            mSync.wait(60 * 1000);
            if (mTestStep == stepSearchUp) {
                mExecuteState = STATE_TEST_UNPASS;
                LogTools.p(TAG, "搜索电台失败，超时 mTestStep=" + mTestStep);
            }
        }
    }

    private void stepSuccess(int i, int i2) {
        if (stepEntities.size() > STEP_TITLES.length) {
            stepEntities.get(i).setTestState(Constants.TestItemState.STATE_SUCCESS);
        } else {
            stepEntities.get(i2).setTestState(Constants.TestItemState.STATE_SUCCESS);
        }
        hasSearchTaget = false;
    }

    private void stepFail(int i, int i2, int freq) {
        StepEntity entity;
        if (stepEntities.size() > STEP_TITLES.length) {
            entity = stepEntities.get(i);
        } else {
            entity = stepEntities.get(i2);
        }
        entity.setStepTitle(entity.getStepTitle() + "(" + String.format(HardwareTestApplication.getContext().
                getResources().getString(R.string.label_radio_search_fail_hint), mTestFMFrequency / 100.0f, freq / 100.0f) + ")");
        hasSearchTaget = false;
    }

    private void stepWait(int step, String s) throws InterruptedException {
        synchronized (mSync) {
            mSync.wait(TASK_WAITE_TIME);
            if (mTestStep == step) {
                mExecuteState = STATE_TEST_UNPASS;
                LogTools.p(TAG, s);
            }
        }
    }

}
