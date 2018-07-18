package com.sinohb.hardware.test.app.fragment;

import android.os.Message;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.effect.EffectController;
import com.sinohb.hardware.test.module.effect.EffectPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class EffectsFragment extends BaseManualFragment implements EffectPresenter.View{
    public static EffectsFragment newInstance() {

        return new EffectsFragment();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new EffectController(this);
            mHandler = new EffectHandler(this);
        }
    }
    public EffectsFragment(){
        init();
    }
    @Override
    public void notifyPlayNormal() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_EFFECT_PLAY_NONE);
    }

    @Override
    public void notifyPlayEffect() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_EFFECT_PLAY);
    }
    private static class EffectHandler extends ManualHandler {

        private EffectHandler(BaseManualFragment controller) {
            super(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            EffectsFragment fragment = (EffectsFragment) controllerWeakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_EFFECT_PLAY_NONE:
                    fragment.setOperateHintText(R.string.label_test_sound_effect_play_none);
                    break;
                case Constants.HandlerMsg.MSG_EFFECT_PLAY:
                    fragment.setOperateHintText(R.string.label_test_sound_effect_play);
                    break;
            }
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setOperateHintText(R.string.label_test_sound_effect_hint);
        }
    }
}
