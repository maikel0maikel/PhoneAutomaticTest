package com.sinohb.hardware.test.app;

public interface BaseExecuteView<T extends BaseExecutePresenter> extends BaseView{
    void freshExecuteUI(int executeState);
}
