package com.sinohb.hardware.test.app;

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
