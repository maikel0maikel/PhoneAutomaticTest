package com.sinohb.hardware.test.app;

import com.sinohb.hardware.test.task.BaseTestTask;

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
    void complete(BaseTestTask task);
    void destroy();
}
