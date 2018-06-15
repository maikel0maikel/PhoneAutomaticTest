package com.sinohb.hardware.test.module.screenadjust;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;
import com.sinohb.hardware.test.entities.Calibration;

public interface ScreenAdjustPresenter {

    interface View extends BaseView<Controller> {

        void displayAdjustView(int direction, Calibration calibration);

        void adjustFailure(int direction);

        void complete();
    }

    interface Controller extends BasePresenter {

        void adjustLeftTop();

        void adjustRightTop();

        void adjustLeftBottom();

        void adjustRightBottom();

        void adjustCenter();

        void adjustTouch(float x,float  y);

        static final int LEFT_TOP = 0;
        static final int RIGHT_TOP = 1;
        static final int LEFT_BOTTOM = 2;
        static final int RIGHT_BOTTOM = 3;
        static final int CENTER = 4;
    }
}
