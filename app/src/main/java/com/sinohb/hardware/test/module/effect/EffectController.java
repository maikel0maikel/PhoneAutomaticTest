package com.sinohb.hardware.test.module.effect;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.audoex.EffectManagerable;
import com.sinohb.hardware.test.module.audoex.EffectTestManager;
import com.sinohb.hardware.test.task.BaseTestTask;

public class EffectController extends BaseDisplayViewController implements EffectPresenter.Controller {
    private EffectManagerable effectManagerable;

    public EffectController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new EffectTask(this);
        effectManagerable = new EffectTestManager();
    }

    @Override
    public int playNormal() {
        if (mView != null) {
            ((EffectPresenter.View) mView).notifyPlayNormal();
        }
        return effectManagerable == null ? Constants.DEVICE_NOT_SUPPORT : effectManagerable.playNormal();
    }

    @Override
    public int playEffect() {
        if (mView != null) {
            ((EffectPresenter.View) mView).notifyPlayEffect();
        }
        return effectManagerable == null ? Constants.DEVICE_NOT_SUPPORT : effectManagerable.playEffect();
    }

    @Override
    public int closeEffect() {
        return effectManagerable == null ? Constants.DEVICE_NOT_SUPPORT : effectManagerable.closeEffect();
    }

    @Override
    public void complete() {
        super.complete();
        effectManagerable.destroy();
    }
    //    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
