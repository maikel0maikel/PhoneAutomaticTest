package com.sinohb.hardware.test.module.bluetooth;


import android.os.Environment;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.ConfigEntity;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseManualTestTask;
import com.sinohb.hardware.test.utils.FileUtils;
import com.sinohb.hardware.test.utils.JsonUtils;
import com.sinohb.logger.LogTools;

import java.io.IOException;


public class BluetoothTestTask extends BaseManualTestTask {
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
    private static final int STEP_CONNECT = 18;
    private static final int STEP_CONNECT_SUCCESS = 19;
    private static final int STEP_CONNECT_FAIL = 20;

    private static final int[] STEP_TITLES = {R.string.label_bt_open, R.string.label_bt_close,
            R.string.label_bt_reopen, R.string.label_bt_discovery,
            R.string.label_bt_stop_discovery, R.string.label_bt_connect};

    private String mBtMac;

    public BluetoothTestTask(BluetoothPresenter.Controller presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_BLUETOOTH;
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

    private boolean deviceNotSupport(int bt) {
        if (Constants.DEVICE_NOT_SUPPORT == bt) {
            LogTools.p(TAG, "蓝牙测试测试结果设备不支持【测试不通过】");
            mExecuteState = STATE_TEST_UNPASS;
            return true;
        }
        return false;
    }

    public void notifyBtOpenState(int state) {
        LogTools.p(TAG, "notifyBtOpenState state=" + state + ",mTestStep=" + mTestStep);
        if (state == BluetoothConstants.OpenState.STATE_TURNING_ON || state == BluetoothConstants.OpenState.STATE_TURNING_OFF) {
            return;
        }
        synchronized (mSync) {
            switch (state) {
                case BluetoothConstants.OpenState.STATE_TURNED_ON:
                    if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FINISH;
                    } else if (mTestStep == STEP_REOPEN) {
                        mTestStep = STEP_REOPEN_FINISH;
                    } else if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FAILURE;
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FAILURE;
                    }
                    break;
                case BluetoothConstants.OpenState.STATE_TURNED_OFF:
                    if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                    } else if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FAILURE;
                    } else if (mTestStep == STEP_REOPEN) {
                        mTestStep = STEP_RE_OPEN_FAILURE;
                    }
                    break;
            }
            mSync.notify();
        }
    }

    public void notifyBtStartDiscovery() {
        LogTools.p(TAG, "notifyBtStartDiscovery mTestStep=" + mTestStep);
        if (mTestStep == STEP_DISCOVERY) {
            synchronized (mSync) {
                mTestStep = STEP_DISCOVERY_OK;
                mSync.notify();
            }
        }
    }

    public void notifyBtStopDiscovery() {
        LogTools.p(TAG, "notifyBtStopDiscovery mTestStep=" + mTestStep);
        if (mTestStep == STEP_STOP_DISCOVERY) {
            synchronized (mSync) {
                mTestStep = STEP_STOP_DISCOVERY_OK;
                mSync.notify();
            }
        } else if (mTestStep == STEP_DISCOVERY) {
            synchronized (mSync) {
                mTestStep = STEP_DISCOVERY_OK;
                LogTools.p(TAG, "STEP_DISCOVERY notifyBtStopDiscovery 扫描完成 重新扫描");
                ((BluetoothPresenter.Controller) mPresenter.get()).startDiscovery();
                mSync.notify();
            }
        }
    }

    public void notifyDeviceFound(String mac) {
        if (mac != null && mac.equals(mBtMac)) {
            notifyBtStartDiscovery();
            LogTools.p(TAG, "notifyDeviceFound equals notifyBtStartDiscovery");
        }
    }

    public void notifyConnectState(int connectState) {
        LogTools.p(TAG, "notifyConnectState mTestStep=" + mTestStep + ",connectState=" + connectState);
        switch (connectState) {
            case BluetoothConstants.ConnectState.STATE_CONNECTED:
                if (mTestStep == STEP_CONNECT) {
                    synchronized (mSync) {
                        mTestStep = STEP_CONNECT_SUCCESS;
                        mSync.notify();
                    }
                }
                break;
        }
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        ((BluetoothPresenter.Controller) mPresenter.get()).notifyExecuteState(STATE_RUNNING);
        int bt;
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_OPEN:
                        LogTools.p(TAG, "蓝牙测试打开");
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).openBt();
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
                        if (stepEntities.size() == STEP_TITLES.length) {
                            StepEntity stepEntity = new StepEntity(0, HardwareTestApplication.getContext().getResources().
                                    getString(R.string.label_bt_reset), Constants.TestItemState.STATE_TESTING);
                            stepEntities.add(0, stepEntity);
                        }
                        LogTools.p(TAG, "STEP_RESET 关闭蓝牙重置 stepCount:" + stepEntities.size());
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).closeBt();
                        if (deviceNotSupport(bt)) {
                            break;
                        } else if (bt == Constants.DEVICE_NORMAL) {
                            stepOk(STEP_OPEN, 0);
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
                        stepOk(STEP_OPEN, 0);
                        LogTools.p(TAG, "蓝牙重置成功");
                        break;
                    case STEP_RESET_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙重置失败，测试结果【测试不通过】");
                        break;
                    case STEP_CLOSE:
                        LogTools.p(TAG, "蓝牙测试关闭");
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).closeBt();
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
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).openBt();
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
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).startDiscovery();
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        LogTools.p(TAG, "蓝牙测试扫描");
                        mSync.wait(30 * 1000);
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
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).stopDisvcovery();
                        LogTools.p(TAG, "蓝牙测试停止扫描");
                        if (deviceNotSupport(bt)) {
                            break;
                        }
                        stepWait(STEP_STOP_DISCOVERY, "蓝牙停止扫描失败，超时--");
                        break;
                    case STEP_STOP_DISCOVERY_OK:
                        stepSuccess(STEP_CONNECT, 5, 4);
                        //stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        LogTools.p(TAG, "蓝牙测试停止扫描，测试结果【测试通过】");
                        break;
                    case STEP_CONNECT:
                        if (mBtMac == null || mBtMac.length() == 0) {
                            LogTools.p(TAG, "配置文件中没有配置蓝牙mac");
                            mTestStep = STEP_CONNECT_FAIL;
                            break;
                        }
                        StepEntity entity = stepEntities.get(stepEntities.size() - 1);
                        entity.setStepTitle(String.format(entity.getStepTitle(),mBtMac));
                        bt = ((BluetoothPresenter.Controller) mPresenter.get()).connect(mBtMac);
                        LogTools.p(TAG, "连接蓝牙mBtMac=" + mBtMac);
                        if (deviceNotSupport(bt)) {
                            break;
                        } else if (bt == BluetoothConstants.ConnectState.STATE_DISCONNECTED) {
                            mTestStep = STEP_CONNECT_FAIL;
                            LogTools.p(TAG, "connect fail mac=" + mBtMac);
                            break;
                        } else if (bt == Constants.DEVICE_CONNECTED) {
                            mTestStep = STEP_CONNECT_SUCCESS;
                            LogTools.p(TAG, "蓝牙设备已经连接");
                            break;
                        }
                        stepWait(STEP_CONNECT, "蓝牙连接失败，超时--");
                        break;
                    case STEP_CONNECT_SUCCESS:
                        mExecuteState = STATE_TEST_WAIT_OPERATE;
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        LogTools.p(TAG, "蓝牙连接成功，等待操作");
                        ((BluetoothPresenter.Controller) mPresenter.get()).notifyConnected();
                        break;
                    case STEP_CONNECT_FAIL:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "蓝牙连接失败，测试不通过");
                        stepEntities.get(stepEntities.size() - 1).setTestState(Constants.TestItemState.STATE_FAIL);
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

    private void stepOk(int step, int pos) {
        mTestStep = step;
        stepEntities.get(pos).setTestState(Constants.TestItemState.STATE_SUCCESS);
    }

    @Override
    protected void startTest() {
        mTestStep = STEP_OPEN;
        if (!stepEntities.isEmpty()) {
            stepEntities.clear();
        }
        try {
            String json = FileUtils.getFileContent(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Config.txt");
            ConfigEntity configEntity = JsonUtils.parse(json, ConfigEntity.class);
            if (configEntity != null && configEntity.bluetooth != null) {
                mBtMac = configEntity.bluetooth.mac.replace(":", "");
            }
            LogTools.p(TAG, "startTest mBtMac=" + mBtMac + ",json=" + json);
        } catch (IOException e) {
            LogTools.p(TAG, "读取配置文件失败");
        } catch (Exception e) {
            LogTools.p(TAG, e, "json解析失败!");
        }
        if (mBtMac == null || mBtMac.length() == 0) {
            STEP_TITLES[STEP_TITLES.length - 1] = R.string.label_bt_connect_emtpy_config;
        }else {
            STEP_TITLES[STEP_TITLES.length - 1] = R.string.label_bt_connect;
        }
        addStepEntity();
    }

}
