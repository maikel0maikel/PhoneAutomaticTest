package com.sinohb.hardware.test.module.screen;

import android.os.Handler;
import android.os.Message;

import com.sinohb.hardware.test.task.ThreadPool;

import java.lang.ref.WeakReference;
import java.util.concurrent.FutureTask;

public class ScreenController implements ScreenPresenter.Controller{

    private ScreenPresenter.View mView;
    private ScreenTask screenTask;
    private ScreenHandler mHandler;
    public ScreenController(ScreenPresenter.View view){
        mView = view;
        mView.setPresenter(this);
        mHandler = new ScreenHandler(this);
    }

    @Override
    public void displayR() {
        mHandler.sendEmptyMessage(ScreenHandler.MSG_RED);
    }

    @Override
    public void displayG() {
        mHandler.sendEmptyMessage(ScreenHandler.MSG_GREEN);
    }

    @Override
    public void displayB() {
        mHandler.sendEmptyMessage(ScreenHandler.MSG_BLUE);
    }

    @Override
    public void complete() {

    }

    @Override
    public void start() {
        screenTask = new ScreenTask(this);
        FutureTask futureTask = new FutureTask(screenTask);
        ThreadPool.getPool().execute(futureTask);
    }

    @Override
    public void pause() {
        screenTask.pause();
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    static class ScreenHandler extends Handler{
        static final int MSG_RED = 1;
        static final int MSG_GREEN = 2;
        static final int MSG_BLUE = 3;
        private WeakReference<ScreenController> controllerWeakReference = null;
        ScreenHandler(ScreenController controller){
            controllerWeakReference = new WeakReference<>(controller);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference ==null){
                return;
            }
            ScreenController controller = controllerWeakReference.get();
            if (controller == null){
                return;
            }
            switch (msg.what){
                case MSG_RED:
                    controller.mView.displayR();
                    break;
                case MSG_GREEN:
                    controller.mView.displayG();
                    break;
                case MSG_BLUE:
                    controller.mView.displayB();
                    break;
            }
        }
    }
}
