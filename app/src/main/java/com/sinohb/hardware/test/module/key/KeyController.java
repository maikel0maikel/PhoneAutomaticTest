package com.sinohb.hardware.test.module.key;


import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.module.BaseDisplayViewController;

public class KeyController extends BaseDisplayViewController implements KeyPresenter.Controller {

    public KeyController(BaseDisplayViewView view) {
        super(view);
        init();
    }


    @Override
    protected void init() {
        task = new KeyTestTask(this);
    }


    @Override
    public void pressKey(int keyCode, int errorCount) {
        if (mView != null) {
            ((KeyPresenter.View) mView).pressKeyHint(keyCode, errorCount);
        }
    }

    @Override
    public void notifyPressKey(int keyCode) {
        if (task != null) {
            ((KeyTestTask) task).notifyTestKey(keyCode);
        }
    }

//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
