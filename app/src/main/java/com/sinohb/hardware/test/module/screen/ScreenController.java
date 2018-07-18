package com.sinohb.hardware.test.module.screen;

import android.os.Handler;
import android.os.Message;

import com.sinohb.hardware.test.app.BaseView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.task.ThreadPool;

import java.lang.ref.WeakReference;
import java.util.concurrent.FutureTask;

public class ScreenController extends BaseController implements ScreenPresenter.Controller {
    private ScreenHandler mHandler;

    public ScreenController(BaseView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new ScreenTask(this);
        mHandler = new ScreenHandler(this);
    }

    @Override
    public void start() {
        if (task != null && (task.getmExecuteState() == BaseTestTask.STATE_NONE||task.isFinish())) {
            task.setFinish(false);
            task.setmExecuteState(BaseTestTask.STATE_NONE);
            FutureTask<Boolean> futureTask = new FutureTask(task);
            ThreadPool.getPool().executeSingleTask(futureTask);
        }
    }

    @Override
    public void displayR() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_RED);
    }

    @Override
    public void displayG() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_GREEN);
    }

    @Override
    public void displayB() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BLUE);
    }

    @Override
    public void complete() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_COMPLETE);
    }


    static class ScreenHandler extends Handler {

        private WeakReference<ScreenController> controllerWeakReference  ;

        ScreenHandler(ScreenController controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            ScreenController controller = controllerWeakReference.get();
            if (controller == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_RED:
                    ((ScreenPresenter.View) controller.mView).displayR();
                    break;
                case Constants.HandlerMsg.MSG_GREEN:
                    ((ScreenPresenter.View) controller.mView).displayG();
                    break;
                case Constants.HandlerMsg.MSG_BLUE:
                    ((ScreenPresenter.View) controller.mView).displayB();
                    break;
                case Constants.HandlerMsg.MSG_COMPLETE:
                    controller.mView.complete(controller.task);
                    break;
            }
        }
    }
}
