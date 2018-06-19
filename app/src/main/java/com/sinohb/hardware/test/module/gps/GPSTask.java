package com.sinohb.hardware.test.module.gps;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.GPSConstants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class GPSTask extends BaseTestTask {
    private static final String TAG = "GPSTask";
    private static final int STEP_OPEN = 1;
    private static final int STEP_START_LOCATE = 2;
    private static final int STEP_CLOSE = 3;
    public GPSTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
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
                                    result = controller.openGPS();
                                    if (result == GPSConstants.DEVICE_STATE_ERROR) {
                                        controller.closeGPS();
                                        mSync.wait();
                                    } else if (result == GPSConstants.DEVICE_NOT_SUPPORT) {
                                        LogTools.p(TAG, "gps测试打开失败，设备不支持，测试不通过");
                                        return false;
                                    }
                                    mSync.wait();
                                    break;
                                case STEP_START_LOCATE:
                                    result = controller.startLocate();
                                    if (result == GPSConstants.DEVICE_NOT_SUPPORT) {
                                        LogTools.p(TAG, "gps测试定位失败，设备不支持，测试不通过");
                                        return false;
                                    }
                                    mSync.wait();
                                    break;
                                case STEP_CLOSE:
                                    result = controller.closeGPS();
                                    if (result == GPSConstants.DEVICE_NOT_SUPPORT){
                                        LogTools.p(TAG, "gps测试关闭失败，设备不支持，测试不通过");
                                        return false;
                                    }else if (result == GPSConstants.DEVICE_STATE_ERROR){
                                        controller.openGPS();
                                        mSync.wait();
                                    }
                                    mSync.wait();
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
        return true;
    }

    public void notifyGPSOpened() {
        synchronized (mSync) {
            if (mTestStep == STEP_OPEN){
                mTestStep = STEP_START_LOCATE;
            }
            mSync.notify();
        }
    }

    public void notifyGPSClosed() {
        synchronized (mSync) {
            if (mTestStep == STEP_CLOSE){
                mExecuteState = STATE_FINISH;
            }
            mSync.notify();
        }
    }
    public void notifyGPSLocateSuccess(){
        synchronized (mSync){
            if (mTestStep == STEP_START_LOCATE){
                mTestStep = STEP_CLOSE;
            }
            mSync.notify();
        }
    }
}
