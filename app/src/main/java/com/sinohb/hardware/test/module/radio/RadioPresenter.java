package com.sinohb.hardware.test.module.radio;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface RadioPresenter {

    interface View extends BaseDisplayViewView  {
        void notifyOpenRadio();

        void notifyPlay(int freq);

        void notifySearch();

        void notifyCloseRadio();
    }

    interface Controller extends BaseDisplayViewPresenter{

        int openRadio();

        int play(int freq);

        int search();

        int closeRadio();

    }
}
