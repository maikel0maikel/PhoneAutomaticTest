package com.sinohb.hardware.test.module.key;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class KeyTestTask extends BaseTestTask{
    private static final String TAG = "KeyTestTask";
    private static final int STEP_PRESS_KEY_MENU = 1;
    private static final int SETP_PRESS_KEY_UP = 2;
    private static final int SETP_PRESS_KEY_DOWN = 3;
    private static final int SETP_PRESS_KEY_ENTER = 4;
    private static final int SETP_PRESS_KEY_BACK = 5;
    public KeyTestTask(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() throws Exception {
        LogTools.p(TAG,"按键测试");
        KeyPresenter.Controller controller = (KeyPresenter.Controller) mPresenter;
        while (!isFinish) {
            switch (mExecuteState) {
                case STATE_NONE:
                    mExecuteState = STATE_RUNNING;
                    mTestStep = STEP_PRESS_KEY_MENU;
                    break;
                case STATE_RUNNING:
                    while (mExecuteState == STATE_RUNNING) {
                        synchronized (mSync) {
                            switch (mTestStep) {
                                case STEP_PRESS_KEY_MENU:
                                    controller.pressKeyMenu();
                                    LogTools.p(TAG,"菜单键测试");
                                    mSync.wait();
                                    break;
                                case SETP_PRESS_KEY_UP:
                                    controller.pressKeyUp();
                                    LogTools.p(TAG,"向上键测试");
                                    mSync.wait();
                                    break;
                                case SETP_PRESS_KEY_DOWN:
                                    controller.pressKeyDown();
                                    LogTools.p(TAG,"向下键测试");
                                    mSync.wait();
                                    break;
                                case SETP_PRESS_KEY_ENTER:
                                    controller.pressKeyEnter();
                                    LogTools.p(TAG,"确定键测试");
                                    mSync.wait();
                                    break;
                                case SETP_PRESS_KEY_BACK:
                                    controller.pressKeyBack();
                                    LogTools.p(TAG,"返回键测试");
                                    mSync.wait();
                                    break;
                            }
                        }
                    }
                    break;
                case STATE_PAUSE:
                    synchronized (mSync) {
                        LogTools.p(TAG, "按键测试暂停");
                        mSync.wait();
                    }
                    break;
                case STATE_FINISH:
                    controller.complete();
                    LogTools.p(TAG,"按键测试完成");
                    isFinish = true;
                    break;
            }
        }
        LogTools.p(TAG,"按键测试任务结束");
        return null;
    }
}
