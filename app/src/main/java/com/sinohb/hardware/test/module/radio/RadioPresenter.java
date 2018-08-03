package com.sinohb.hardware.test.module.radio;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface RadioPresenter {

    interface View extends BaseDisplayViewView {
        void notifyOpenRadio();

        void notifyPlay(int freq);

        void notifySearch(int type,int freq);

        void notifyCloseRadio();

        void notifyStopFreq(int type,int freq);
    }

    interface Controller extends BaseDisplayViewPresenter {

        int openRadio();

        int play(int freq);

        int search(int type,int freq);

        int closeRadio();

        int getCurrentFreq();

        void notifyStopFreq(int type,int freq);
    }
}
