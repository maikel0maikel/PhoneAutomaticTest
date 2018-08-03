package com.sinohb.hardware.test.module;

import com.sinohb.hardware.test.app.BaseExecutePresenter;
import com.sinohb.hardware.test.app.BaseExecuteView;


public abstract class BaseExecuteController extends BaseController implements BaseExecutePresenter {

    public BaseExecuteController(BaseExecuteView view) {
        super(view);
    }


    @Override
    public void notifyExecuteState(int executeState) {
        if (mView != null) {
            ((BaseExecuteView)mView ).freshExecuteUI(executeState);
        }
    }

    @Override
    public void seeDetail() {

    }

}
