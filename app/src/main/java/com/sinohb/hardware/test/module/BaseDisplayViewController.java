package com.sinohb.hardware.test.module;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public abstract class BaseDisplayViewController extends BaseExecuteController implements BaseDisplayViewPresenter {
    public BaseDisplayViewController(BaseDisplayViewView view) {
        super(view);
    }

    @Override
    public void displayView() {
        if (mView!=null){
            ((BaseDisplayViewView)mView).displayView();
        }
    }

    @Override
    public void testOk() {
        if (task!=null){
            task.testOk();
        }
    }

    @Override
    public void testFail() {
        if (task!=null){
            task.testFail();
        }
    }
}
