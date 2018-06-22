package com.sinohb.hardware.test.module.key;

import android.os.Handler;
import android.os.Message;

import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;

public class KeyController implements KeyPresenter.Controller {
    private static final String TAG = "KeyController";
    private KeyPresenter.View mView;
    private KeyHandler mHandler;

    public KeyController(KeyPresenter.View view) {
        this.mView = view;
        mHandler = new KeyHandler(this);
        mView.setPresenter(this);
    }

    @Override
    public void pressKeyMenu() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_KEY_PRESS_MUNU);
    }

    @Override
    public void pressKeyUp() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_KEY_PRESS_UP);
    }

    @Override
    public void pressKeyDown() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_KEY_PRESS_DOWN);
    }

    @Override
    public void pressKeyEnter() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_KEY_PRESS_ENTER);
    }

    @Override
    public void pressKeyBack() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_KEY_PRESS_BACK);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void destroy() {

    }

    static class KeyHandler extends Handler {
        private WeakReference<KeyController> weakReference = null;

        KeyHandler(KeyController controller) {
            weakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                LogTools.p(TAG, "weakReference is null");
                return;
            }
            KeyController controller = weakReference.get();
            if (controller == null) {
                LogTools.p(TAG, "controller is null");
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_KEY_PRESS_MUNU:
                    controller.mView.showMenuView();
                    break;
                case Constants.HandlerMsg.MSG_KEY_PRESS_UP:
                    controller.mView.showUpView();
                    break;
                case Constants.HandlerMsg.MSG_KEY_PRESS_DOWN:
                    controller.mView.showDownView();
                    break;
                case Constants.HandlerMsg.MSG_KEY_PRESS_ENTER:
                    controller.mView.showEnterView();
                    break;
                case Constants.HandlerMsg.MSG_KEY_PRESS_BACK:
                    controller.mView.showBackView();
                    break;
            }
        }
    }

}
