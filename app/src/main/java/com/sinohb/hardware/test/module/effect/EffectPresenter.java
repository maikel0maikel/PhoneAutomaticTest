package com.sinohb.hardware.test.module.effect;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;
import com.sinohb.hardware.test.app.BaseDisplayViewView;

public interface EffectPresenter {

    interface View extends BaseDisplayViewView  {
        void notifyPlayNormal();

        void notifyPlayEffect();

    }

    interface Controller extends BaseDisplayViewPresenter{

        int playNormal();

        int playEffect();

        int closeEffect();

    }

}
