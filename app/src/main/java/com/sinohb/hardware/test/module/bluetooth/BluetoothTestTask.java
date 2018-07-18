package com.sinohb.hardware.test.module.bluetooth;


import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.logger.LogTools;


public class BluetoothTestTask extends BaseAutoTestTask {
    private static final String TAG = "BluetoothTestTask";
    private static final int STEP_OPEN = 1;
    private static final int SETP_OPEN_FINISH = 2;
    private static final int STEP_CLOSE = 3;
    private static final int STEP_CLOSE_FINISH = 4;
    private static final int STEP_REOPEN = 5;
    private static final int STEP_REOPEN_FINISH = 6;
    private static final int STEP_DISCOVERY = 7;
    private static final int STEP_DISCOVERY_OK = 8;
    private static final int STEP_STOP_DISCOVERY = 9;
    private static final int STEP_STOP_DISCOVERY_OK = 10;
    private static final int STEP_RESET = 11;
    private static final int STEP_RESET_FINISHED = 12;
    private static final int SETP_OPEN_FAILURE = 14;
    private static final int STEP_CLOSE_FAILURE = 15;
    private static final int STEP_RE_OPEN_FAILURE = 16;
    private static final int STEP_RESET_FAILURE = 17;
    private static final int[] STEP_TITLES = {R.string.label_bt_open, R.string.label_bt_close,
            R.string.label_bt_reopen, R.string.label_bt_discovery, R.string.label_bt_stop_discovery};

    public BluetoothTestTask(BluetoothPresenter.Controller presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_BLUETOOTH;
    }

    protected void addStepEntity(int pos) {
        for (int i = pos;i<STEP_TITLES.length;i++) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().getResources().getString(STEP_TITLES[i]), Constants.TestItemState.STATE_FAIL);
            stepEntities.add(stepEntity);
        }
    }

    private boolean deviceNotSupport(int bt) {
        if (Constants.DEVICE_NOT_SUPPORT == bt) {
            LogTools.p(TAG, "蓝牙测试测试结果设备不支持【测试不通过】");
            mExecuteState = STATE_TEST_UNPASS;
            return true;
        }
        return false;
    }

    public void notifyBtOpenState(int state) {
        synchronized (mSync) {
            switch (state) {
                case BluetoothConstants.OpenState.STATE_TURNED_ON:
                    if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_REOPEN) {
                        mTestStep = STEP_REOPEN_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FAILURE;
                        mSync.notify();
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FAILURE;
                        mSync.notify();
                    }
                    break;
                case BluetoothConstants.OpenState.STATE_TURNED_OFF:
                    if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                        mSync.notify();
                    } else if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FAILURE;
                        mSync.notify();
                    } else if (mTestStep == STEP_REOPEN) {
                        mTestStep = STEP_RE_OPEN_FAILURE;
                        mSync.notify();
                    }
                    break;
            }
        }
    }

    public void notifyBtStartDiscovery() {
        synchronized (mSync) {
            if (mTestStep == STEP_DISCOVERY) {
                mTestStep = STEP_DISCOVERY_OK;
                mSync.notify();
            }
        }
    }

    public void notifyBtStopDiscovery() {
        synchronized (mSync) {
            if (mTestStep == STEP_STOP_DISCOVERY) {
                mTestStep = STEP_STOP_DISCOVERY_OK;
                mSync.notify();
            } else if (mTestStep == STEP_DISCOVERY) {
                mTestStep = STEP_DISCOVERY_OK;
                LogTools.p(TAG, "STEP_DISCOVERY notifyBtStopDiscovery 扫描完成 重新扫描");
                ((BluetoothPresenter.Controller) mPresenter).startDiscovery();
                mSync.notify();
            }
        }
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int bt;
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_OPEN:
                        StepEntity stepEntity1 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_open), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity1);
                        LogTools.p(TAG, "蓝牙测试打开");
                        bt = ((BluetoothPresenter.Controller) mPresenter).openBt();
                        if (bt == Constants.DEVICE_RESET) {
                            mTestStep = STEP_RESET;
                            LogTools.p(TAG, "蓝牙处于打开状态，关闭重置");
                            stepEntities.clear();
                            break;
                        } else if (deviceNotSupport(bt)) {
                            stepEntity1.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(stepEntities.size());
                            break;
                        }
                        mSync.wait();
                        break;
                    case STEP_RESET:
                        LogTools.p(TAG, "STEP_RESET 关闭蓝牙重置");
                        StepEntity stepEntity = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_reset), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity);
                        bt = ((BluetoothPresenter.Controller) mPresenter).closeBt();
                        if (deviceNotSupport(bt)) {
                            stepEntity.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(0);
                            break;
                        }
                        mSync.wait();
                        break;
                    case SETP_OPEN_FINISH:
                        stepOk(STEP_CLOSE);
                        LogTools.p(TAG, "蓝牙测试打开结束，测试结果【测试通过】");
                        break;
                    case SETP_OPEN_FAILURE:
                        stepFail();
                        LogTools.p(TAG, "蓝牙测试打开结束，测试结果【测试不通过】");
                        break;
                    case STEP_RESET_FINISHED:
                        stepOk(STEP_OPEN);
                        LogTools.p(TAG, "蓝牙重置成功");
                        break;
                    case STEP_RESET_FAILURE:
                        stepFail();
                        LogTools.p(TAG, "蓝牙重置失败，测试结果【测试不通过】");
                        break;
                    case STEP_CLOSE:
                        LogTools.p(TAG, "蓝牙测试关闭");
                        StepEntity stepEntity2 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_close), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity2);
                        bt = ((BluetoothPresenter.Controller) mPresenter).closeBt();
                        if (deviceNotSupport(bt)) {
                            stepEntity2.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(stepEntities.size());
                            break;
                        }
                        mSync.wait();
                        break;
                    case STEP_CLOSE_FINISH:
                        stepOk(STEP_REOPEN);
                        LogTools.p(TAG, "蓝牙测试关闭结束，测试结果【测试通过】");
                        break;
                    case STEP_CLOSE_FAILURE:
                        stepFail();
                        LogTools.p(TAG, "蓝牙测试关闭结束，测试结果【测试不通过】");
                        break;
                    case STEP_REOPEN:
                        LogTools.p(TAG, "重新打开蓝牙");
                        StepEntity stepEntity3 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_reopen), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity3);
                        bt = ((BluetoothPresenter.Controller) mPresenter).openBt();
                        if (deviceNotSupport(bt)) {
                            stepEntity3.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(stepEntities.size());
                            break;
                        }
                        mSync.wait();
                        break;
                    case STEP_REOPEN_FINISH:
                        stepOk(STEP_DISCOVERY);
                        LogTools.p(TAG, "蓝牙测试重新打开，测试结果【测试通过】");
                        break;
                    case STEP_RE_OPEN_FAILURE:
                        stepFail();
                        LogTools.p(TAG, "蓝牙测试重新打开，测试结果【测试不通过】");
                        break;
                    case STEP_DISCOVERY:
                        StepEntity stepEntity4 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_discovery), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity4);
                        bt = ((BluetoothPresenter.Controller) mPresenter).startDiscovery();
                        if (deviceNotSupport(bt)) {
                            stepEntity4.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(stepEntities.size());
                            break;
                        }
                        LogTools.p(TAG, "蓝牙测试扫描");
                        mSync.wait();
                        break;
                    case STEP_DISCOVERY_OK:
                        stepOk(STEP_STOP_DISCOVERY);
                        LogTools.p(TAG, "蓝牙测试扫描，测试结果【测试通过】");
                        break;
                    case STEP_STOP_DISCOVERY:
                        StepEntity stepEntity5 = new StepEntity(stepEntities.size() + 1, HardwareTestApplication.getContext().getResources().getString(R.string.label_bt_stop_discovery), Constants.TestItemState.STATE_TESTING);
                        stepEntities.add(stepEntity5);
                        bt = ((BluetoothPresenter.Controller) mPresenter).stopDisvcovery();
                        LogTools.p(TAG, "蓝牙测试停止扫描");
                        if (deviceNotSupport(bt)) {
                            stepEntity5.setTestState(Constants.TestItemState.STATE_FAIL);
                            addStepEntity(stepEntities.size());
                            break;
                        }
                        mSync.wait();
                        LogTools.p(TAG, "蓝牙测试停止扫描，测试结果【测试通过】");
                        break;
                    case STEP_STOP_DISCOVERY_OK:
                        mExecuteState = STATE_FINISH;
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        LogTools.e(TAG, "所有步骤执行结束");
                        break;
                }
                Thread.sleep(500);
            }
//            Thread.sleep(500);
        }
    }

    private void stepFail() {
        mExecuteState = STATE_TEST_UNPASS;
        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_FAIL);
        addStepEntity(stepEntities.size());
    }

    private void stepOk(int stepClose) {
        mTestStep = stepClose;
        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
    }

    @Override
    protected void startTest() {
        stepEntities.clear();
        mTestStep = STEP_OPEN;
    }

    @Override
    protected void unpass() {
        super.unpass();
        if (stepEntities!=null&&stepEntities.isEmpty()){
            addStepEntity(0);
        }
    }
}
