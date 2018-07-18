package com.sinohb.hardware.test.app;


import com.sinohb.hardware.test.task.BaseTestTask;

public interface BasePresenter {
    void start();

    void pause();

    void stop();

    BaseTestTask getTask();

    void complete();

    void destroy();


}
