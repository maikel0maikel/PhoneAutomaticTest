package com.sinohb.hardware.test.module.bluetooth;


import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class BluetoothTestTask extends BaseTestTask {
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

    public BluetoothTestTask(BluetoothPresenter.Controller presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        boolean isPass = false;
        BluetoothPresenter.Controller controller = (BluetoothPresenter.Controller) mPresenter;
        LogTools.p(TAG, "蓝牙开始测试");
        int bt = -10000;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    LogTools.p(TAG, "蓝牙任务测试开启");
                    mTestStep = STEP_OPEN;
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            switch (mTestStep) {
                                case STEP_OPEN:
                                    LogTools.p(TAG, "蓝牙测试打开");
                                    bt = controller.openBt();
                                    if (bt == BluetoothConstants.DEVICE_RESET) {
                                        LogTools.p(TAG,"蓝牙处于打开状态，关闭重置");
                                        controller.closeBt();
                                        mTestStep = STEP_RESET;
                                        stepWaite(STEP_RESET);
                                        break;
                                    } else if (deviceNotSupport(bt)) return false;
                                    stepWaite(STEP_OPEN);
                                    if (stepFailure(SETP_OPEN_FINISH, "蓝牙测试打开，测试结果【测试不通过】"))
                                        return false;
                                    LogTools.p(TAG, "蓝牙测试打开结束，测试结果【测试通过】");
                                    mTestStep = STEP_CLOSE;
                                    break;
                                case STEP_RESET_FINISHED:
                                    mTestStep = STEP_OPEN;
                                    LogTools.p(TAG,"重置成功");
                                    break;
                                case STEP_CLOSE:
                                    LogTools.p(TAG, "蓝牙测试关闭");
                                    bt = controller.closeBt();
                                    if (deviceNotSupport(bt)) return false;
                                    stepWaite(STEP_CLOSE);
                                    if (stepFailure(STEP_CLOSE_FINISH, "蓝牙测试关闭，测试结果【测试不通过】"))
                                        return false;
                                    LogTools.p(TAG, "蓝牙测试关闭结束，测试结果【测试通过】");
                                    mTestStep = STEP_REOPEN;
                                    break;
                                case STEP_REOPEN:
                                    LogTools.p(TAG, "重新打开蓝牙");
                                    bt = controller.openBt();
                                    if (deviceNotSupport(bt)) return false;
                                    stepWaite(STEP_REOPEN);
                                    if (stepFailure(STEP_REOPEN_FINISH, "蓝牙测试重新打开结束测试，测试结果【测试不通过】"))
                                        return false;
                                    mTestStep = STEP_DISCOVERY;
                                    break;
                                case STEP_DISCOVERY:
                                    bt = controller.startDiscovery();
                                    if (deviceNotSupport(bt)) return false;
                                    LogTools.p(TAG, "蓝牙测试扫描");
                                    stepWaite(STEP_DISCOVERY);
                                    if (stepFailure(STEP_DISCOVERY_OK, "蓝牙测试扫描，测试结果【测试不通过】"))
                                        return false;
                                    mTestStep = STEP_STOP_DISCOVERY;
                                    break;
                                case STEP_STOP_DISCOVERY:
                                    bt = controller.stopDisvcovery();
                                    if (deviceNotSupport(bt)) return false;
                                    LogTools.p(TAG, "蓝牙测试停止扫描");
                                    stepWaite(STEP_STOP_DISCOVERY);
                                    if (stepFailure(STEP_STOP_DISCOVERY_OK, "蓝牙测试停止扫描，测试结果【测试不通过】"))
                                        return false;
                                    mExecuteState = STATE_FINISH;
                                    break;
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "蓝牙测试任务暂停");
                        try {
                            mSync.wait();
                        } catch (InterruptedException e) {
                            LogTools.e(TAG, e);
                        }
                    }
                    break;
                case STATE_FINISH:
                    controller.complete();
                    isFinish = true;
                    break;
            }
        }
        return isPass;
    }

    private boolean stepFailure(int setpOpenFinish, String s) {
        if (mTestStep != setpOpenFinish) {
            isFinish = true;
            LogTools.p(TAG, s);
            return true;
        }
        return false;
    }

    private void stepWaite(int stepOpen) {
        while (mTestStep == stepOpen) {
            try {
                mSync.wait(TASK_WAITE_TIME);
            } catch (InterruptedException e) {
                LogTools.e(TAG, e);
            }
        }
    }

    private boolean deviceNotSupport(int bt) {
        if (BluetoothConstants.DEVICE_NOT_SUPPORT == bt) {
            isFinish = true;
            LogTools.p(TAG, "蓝牙测试测试结果设备不支持【测试不通过】");
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
                    }
                    break;
                case BluetoothConstants.OpenState.STATE_TURNED_OFF:
                    if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
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
            }
        }
    }

    public void stopTask() {
        synchronized (mSync) {
            isFinish = true;
            mSync.notify();
        }
    }
}
