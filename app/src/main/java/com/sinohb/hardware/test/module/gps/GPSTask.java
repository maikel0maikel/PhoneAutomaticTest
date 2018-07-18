package com.sinohb.hardware.test.module.gps;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class GPSTask extends BaseTestTask {
    private static final String TAG = "GPSTask";
    private static final int STEP_OPEN = 1;
    private static final int STEP_START_LOCATE = 2;
    private static final int STEP_CLOSE = 3;
    private static final int STEP_RESET = 4;
    private static final int STEP_RESET_FINISHED = 5;
    private static final int STEP_RESET_CLOSE = 6;
    private static final int STEP_RESET_CLOSE_FINISHED = 7;
    public GPSTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Integer call() throws Exception {
        int result = -10000;
        GPSPresenter.Controller controller = (GPSPresenter.Controller) mPresenter;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_OPEN;
                    LogTools.p(TAG, "gps开始测试");
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            switch (mTestStep) {
                                case STEP_OPEN:
                                    LogTools.p(TAG,"gps测试打开");
                                    result = controller.openGPS();
                                    if (result == Constants.DEVICE_STATE_ERROR) {
                                        controller.closeGPS();
                                        mTestStep = STEP_RESET;
                                        LogTools.p(TAG, "gps状态为打开重置mTestStep:"+mTestStep);
                                        mSync.wait();
                                        LogTools.p(TAG,"继续执行------");
                                        break;
                                    } else if (result == Constants.DEVICE_NOT_SUPPORT) {
                                        LogTools.p(TAG, "gps测试打开失败，设备不支持，测试不通过");
                                        return 0;
                                    }
                                    mSync.wait();
                                    mTestStep = STEP_START_LOCATE;
                                    LogTools.p(TAG,"gps测试打开通过");
                                    break;
                                case STEP_RESET_FINISHED:
                                    mTestStep = STEP_OPEN;
                                    LogTools.p(TAG,"重置成功");
                                    break;
                                case STEP_START_LOCATE:
                                    LogTools.p(TAG,"gps测试定位");
                                    result = controller.startLocate();
                                    if (result == Constants.DEVICE_NOT_SUPPORT) {
                                        LogTools.p(TAG, "gps测试定位失败，设备不支持，测试不通过");
                                        return 0;
                                    }
                                    mSync.wait();
                                    mTestStep = STEP_CLOSE;
                                    LogTools.p(TAG,"gps测试定位通过");
                                    break;
                                case STEP_CLOSE:
                                    LogTools.p(TAG,"gps测试关闭");
                                    result = controller.closeGPS();
                                    if (result == Constants.DEVICE_NOT_SUPPORT) {
                                        LogTools.p(TAG, "gps测试关闭失败，设备不支持，测试不通过");
                                        return 0;
                                    } else if (result == Constants.DEVICE_STATE_ERROR) {
                                        controller.openGPS();
                                        mTestStep = STEP_RESET_CLOSE;
                                        mSync.wait();
                                        break;
                                    }
                                    mSync.wait();
                                    mExecuteState = STATE_FINISH;
                                    LogTools.p(TAG,"gps测试关闭通过");
                                    break;
                                case STEP_RESET_CLOSE_FINISHED:
                                    mTestStep = STEP_CLOSE;
                                    LogTools.p(TAG,"重置关闭成功");
                                    break;
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "gps测试暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    isFinish = true;
                    break;
            }
        }
        LogTools.p(TAG,"执行完成");
        return isPass;
    }

    public void notifyGPSOpened() {
        synchronized (mSync) {
            if (mTestStep == STEP_RESET_CLOSE) {
                mTestStep = STEP_RESET_CLOSE_FINISHED;
            }
            LogTools.p(TAG,"gps 打开成功 mTestStep:"+mTestStep);
            mSync.notify();
        }
    }

    public void notifyGPSClosed() {
        synchronized (mSync) {
            if (mTestStep == STEP_RESET) {
                mTestStep = STEP_RESET_FINISHED;
            }
            LogTools.p(TAG,"gps 关闭成功 mTestStep:"+mTestStep);
            mSync.notify();
        }

    }

    public void notifyGPSLocateSuccess() {
        synchronized (mSync) {
            if (mTestStep == STEP_START_LOCATE) {
                mTestStep = STEP_CLOSE;
            }
            mSync.notifyAll();
        }
    }
}
