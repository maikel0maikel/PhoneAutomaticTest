package com.sinohb.hardware.test.module.usb;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;
import com.sinohb.logger.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class USBTestTask extends BaseTestTask {
    private static final String TAG = "USBTestTask";
    private String usbPath;
    private static final long WAITE_TIME = 1000l;

    public USBTestTask(String path, BasePresenter presenter) {
        this(presenter);
        usbPath = path;
    }

    public USBTestTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        boolean pass = false;

        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    LogTools.p(TAG, "测试开始");
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            File file = new File(usbPath);
                            if (file.exists()) {//读取一文件
                                FileInputStream fis = null;
                                BufferedReader reader = null;
                                try {
                                    fis = new FileInputStream(usbPath + "/test.txt");
                                    reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                                    StringBuilder builder = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        builder.append(line);
                                    }
                                    line = builder.toString();
                                    LogTools.p(TAG, "文件内容：" + line);
                                    builder.setLength(0);
                                    pass = true;
                                } catch (IOException e) {
                                    pass = false;
                                    LogTools.p(TAG,e,"文件读取出错");
                                } finally {
                                    IOUtils.closeQuietly(fis);
                                    IOUtils.closeQuietly(reader);
                                }
                                mExecuteState = STATE_FINISH;
                            } else {
                                LogTools.p(TAG, "检测等待1s");
                                mSync.wait(WAITE_TIME);
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    LogTools.p(TAG, "暂停测试任务");
                    synchronized (mSync) {
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    //controller.complete();
                    isFinish = true;
                    LogTools.p(TAG, "测试任务完成");
                    break;
            }
        }
        LogTools.p(TAG, "结束测试任务");
        return pass;
    }
}
