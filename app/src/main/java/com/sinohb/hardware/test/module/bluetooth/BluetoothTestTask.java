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

    private void addStepEntity() {
        int i = 1;
        for (int res:STEP_TITLES) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().
                    getResources().getString(res), Constants.TestItemState.STATE_TESTING);
            stepEntities.add(stepEntity);
            i++;
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
                        LogTools.p(TAG, "蓝牙测试打开");
                        bt = ((BluetoothPresenter.Controller) mPresenter).openBt();
                        if (bt == Constants.DEVICE_RESET) {
                            mTestStep = STEP_RESET;
                            LogTools.p(TAG, "蓝牙处于打开状态，关闭重置");
                            break;
                        } else if (deviceNotSupport(bt)) {
                            break;
                        }
                        stepWait(STEP_OPEN, "蓝牙打开失败，超时--");
                        break;
                    case STEP_RESET:
                        if (stepEntities.size() == STEP_TITLES.length){
                            StepEntity stepEntity = new StepEntity(0, HardwareTestApplication.getContext().getResources().
                                    getString(R.string.label_bt_reset), Constants.TestItemState.STATE_TESTING);
                            stepEntities.add(0,stepEntity);
                        }
                        LogTools.p(TAG, "STEP_RESET 关闭蓝牙重置 stepCount:"+stepEntities.size());
                        bt = ((BluetoothPresenter.Controller) mPresenter).closeBt();
                        if (deviceNotSupport(bt)) {
                            break;
                        }else if(bt == Constants.DEVICE_NORMAL){
                            stepOk(STEP_OPEN,0);
                            break;
                        }
                        stepWait(STEP_RESET, "蓝牙重置失败，超时--");
                        break;
                    case SETP_OPEN_FINISH:
                        stepSuccess(STEP_CLOSE, 1, 0);
                        LogTools.p(TAG, "蓝牙测试打开结束，测试结果【测试通过】");
                        break;
                    case SETP_OPEN_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙测试打开结束，测试结果【测试不通过】");
                        break;
                    case STEP_RESET_FINISHED:
                        stepOk(STEP_OPEN,0);
                        LogTools.p(TAG, "蓝牙重置成功");
                        break;
                    case STEP_RESET_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙重置失败，测试结果【测试不通过】");
                        break;
                    case STEP_CLOSE:
                        LogTools.p(TAG, "蓝牙测试关闭");
                        bt = ((BluetoothPresenter.Controller) mPresenter).closeBt();
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        stepWait(STEP_CLOSE, "蓝牙关闭失败，超时--");
                        break;
                    case STEP_CLOSE_FINISH:
                        stepSuccess(STEP_REOPEN, 2, 1);
                        LogTools.p(TAG, "蓝牙测试关闭结束，测试结果【测试通过】");
                        break;
                    case STEP_CLOSE_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙测试关闭结束，测试结果【测试不通过】");
                        break;
                    case STEP_REOPEN:
                        LogTools.p(TAG, "重新打开蓝牙");
                        bt = ((BluetoothPresenter.Controller) mPresenter).openBt();
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        stepWait(STEP_REOPEN, "蓝牙重新打开失败，超时--");
                        break;
                    case STEP_REOPEN_FINISH:
                        stepSuccess(STEP_DISCOVERY, 3, 2);
                        LogTools.p(TAG, "蓝牙测试重新打开，测试结果【测试通过】");
                        break;
                    case STEP_RE_OPEN_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙测试重新打开，测试结果【测试不通过】");
                        break;
                    case STEP_DISCOVERY:
                        bt = ((BluetoothPresenter.Controller) mPresenter).startDiscovery();
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        LogTools.p(TAG, "蓝牙测试扫描");
                        mSync.wait(30*1000);
                        if (mTestStep == STEP_DISCOVERY) {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "蓝牙扫描失败，超时");
                        }
                        break;
                    case STEP_DISCOVERY_OK:
                        stepSuccess(STEP_STOP_DISCOVERY, 4, 3);
                        LogTools.p(TAG, "蓝牙测试扫描，测试结果【测试通过】");
                        break;
                    case STEP_STOP_DISCOVERY:
                        bt = ((BluetoothPresenter.Controller) mPresenter).stopDisvcovery();
                        LogTools.p(TAG, "蓝牙测试停止扫描");
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        stepWait(STEP_STOP_DISCOVERY, "蓝牙停止扫描失败，超时--");
                        LogTools.p(TAG, "蓝牙测试停止扫描，测试结果【测试通过】");
                        break;
                    case STEP_STOP_DISCOVERY_OK:
                        mExecuteState = STATE_FINISH;
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        LogTools.e(TAG, "所有步骤执行结束");
                        break;
                }
            }
            Thread.sleep(500);
        }
    }

    private void stepWait(int stepOpen, String s) throws InterruptedException {
        mSync.wait(TASK_WAITE_TIME);
        if (mTestStep == stepOpen) {
            mExecuteState = STATE_TEST_UNPASS;
            LogTools.p(TAG, s);
        }
    }

    private void stepSuccess(int nextStep, int i, int i2) {
        if (stepEntities.size() > STEP_TITLES.length) {
            stepOk(nextStep, i);
        } else {
            stepOk(nextStep, i2);
        }
    }

    private void stepOk(int step,int pos) {
        mTestStep = step ;
        stepEntities.get(pos).setTestState(Constants.TestItemState.STATE_SUCCESS);
    }

    @Override
    protected void startTest() {
        mTestStep = STEP_OPEN;
        if (!stepEntities.isEmpty()){
            stepEntities.clear();
        }
        addStepEntity();
    }

}
