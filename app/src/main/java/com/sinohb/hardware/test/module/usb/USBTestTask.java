package com.sinohb.hardware.test.module.usb;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.hardware.test.utils.FileUtils;
import com.sinohb.logger.LogTools;

import java.io.File;
import java.io.IOException;

public class USBTestTask extends BaseAutoTestTask {
    private static final String TAG = USBTestTask.class.getSimpleName();
    private String usbPath;
    private static final long WAITE_TIME = 1000L;
    private int readCount = 0;

    public USBTestTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    protected void initStepEntity() {
        super.initStepEntity();
        StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().getResources().
                getString(R.string.label_storage_path), Constants.TestItemState.STATE_TESTING);
        StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().getResources().
                getString(R.string.label_storage_read), Constants.TestItemState.STATE_TESTING);
        stepEntities.add(stepEntity1);
        stepEntities.add(stepEntity2);
    }

    public void setUsbPath(String usbPath) {
        synchronized (mSync) {
            this.usbPath = usbPath;
            mSync.notify();
        }
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        while (mExecuteState == STATE_RUNNING) {
            synchronized (mSync) {
                if (usbPath == null || usbPath.trim().length() == 0) {
                    LogTools.p(TAG, "路径为空---");
                    ((USBPresenter.Controller) mPresenter).setDirPath();
                    Thread.sleep(100);
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_FAIL);
                    continue;
                }
                File file = new File(usbPath);
                if (file.exists()) {//读取一文件
                    ((USBPresenter.Controller) mPresenter).notifyStatus(1);
                    stepEntities.get(0).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    stepEntities.get(0).setStepTitle(HardwareTestApplication.getContext().getResources().getString(R.string.label_storage_path) + usbPath);
                    try {
                        String content = FileUtils.getFileContent(usbPath + "/test.txt");
                        LogTools.p(TAG, "文件内容：" + content);
                        isPass = 1;
                        mExecuteState = STATE_FINISH;
                        stepEntities.get(1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                    } catch (IOException e) {
                        readCount++;
                        LogTools.p(TAG, e, "文件读取出错 path:" + usbPath);
                        if (readCount >= 3) {
                            readCount = 0;
                            mExecuteState = STATE_TEST_UNPASS;
                            isPass = 0;
                            LogTools.p(TAG, e, "文件读取出3次失败，测试不通过");
                            stepEntities.get(1).setTestState(Constants.TestItemState.STATE_FAIL);
                        }
                        try {
                            mSync.wait(WAITE_TIME);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    ((USBPresenter.Controller) mPresenter).notifyStatus(0);
                    mSync.wait(WAITE_TIME);
                }
            }
        }
    }
}
