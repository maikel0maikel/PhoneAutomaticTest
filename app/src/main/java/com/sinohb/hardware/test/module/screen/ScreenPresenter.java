package com.sinohb.hardware.test.module.screen;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface ScreenPresenter {
    interface View extends BaseView<Controller> {
        void displayR();

        void displayG();

        void displayB();
    }

    interface Controller extends BasePresenter{

        void displayR();

        void displayG();

        void displayB();

    }

}
