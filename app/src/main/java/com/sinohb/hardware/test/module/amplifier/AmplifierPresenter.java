package com.sinohb.hardware.test.module.amplifier;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface AmplifierPresenter {

    int LEFT_FRONT = 0;

    int LEFT_REAR = 1;

    int RIGHT_FRONT = 2;

    int RIGHT_REAR = 3;

    interface View extends BaseDisplayViewView{
        void notifyPlayAmplifier(int direction);
        void notifyTestAll();
        void notifyAmplifierPosition(int position);
    }

    interface Controller extends BaseDisplayViewPresenter{

        void playAmplifier(int direction);

        void notifyTestAll();

        void notifyAmplifierPosition(int position);
    }

}
