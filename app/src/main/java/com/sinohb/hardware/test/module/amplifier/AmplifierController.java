package com.sinohb.hardware.test.module.amplifier;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.entities.AmplifierEntity;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.audoex.EffectManagerable;
import com.sinohb.hardware.test.module.audoex.EffectTestManager;

public class AmplifierController extends BaseDisplayViewController implements AmplifierPresenter.Controller {
    private EffectManagerable managerable;
    private AmplifierEntity[] amplifies;
    private static final int AMPLIFIER_PX = -50;
    private static final int AMPLIFIER_PY = 50;

    public AmplifierController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        initAmplifier();
        task = new AmplifierTask(this);
        managerable = new EffectTestManager();
    }

    private void initAmplifier() {
        amplifies = new AmplifierEntity[4];
        amplifies[AmplifierPresenter.LEFT_FRONT] = new AmplifierEntity(AMPLIFIER_PX, AMPLIFIER_PX);
        amplifies[AmplifierPresenter.LEFT_REAR] = new AmplifierEntity(AMPLIFIER_PX, AMPLIFIER_PY);
        amplifies[AmplifierPresenter.RIGHT_FRONT] = new AmplifierEntity(AMPLIFIER_PY, AMPLIFIER_PX);
        amplifies[AmplifierPresenter.RIGHT_REAR] = new AmplifierEntity(AMPLIFIER_PY, AMPLIFIER_PY);
    }

    @Override
    public void playAmplifier(int direction) {
        if (managerable != null) {
            managerable.playAmplifier(amplifies[direction]);
        }
        if (task != null) {
            ((AmplifierTask) task).tstRunning(direction);
        }
    }

    @Override
    public void notifyTestAll() {
        if (mView != null) {
            ((AmplifierPresenter.View) mView).notifyTestAll();
        }
    }

    @Override
    public void notifyAmplifierPosition(int position) {
        if (mView != null) {
            ((AmplifierPresenter.View) mView).notifyAmplifierPosition(position);
        }
    }

    @Override
    public void complete() {
        super.complete();
        if (managerable != null) {
            managerable.destroy();
        }
    }
    //
//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
