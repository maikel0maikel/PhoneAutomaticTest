package com.sinohb.hardware.test.module.key;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface KeyPresenter {
    interface View extends BaseView<Controller> {
        void showMenuView();

        void showUpView();

        void showDownView();

        void showEnterView();

        void showBackView();
    }

    interface Controller extends BasePresenter {

        void pressKeyMenu();

        void pressKeyUp();

        void pressKeyDown();

        void pressKeyEnter();

        void pressKeyBack();

    }
}
