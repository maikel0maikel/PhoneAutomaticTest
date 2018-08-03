package com.sinohb.hardware.test.app.fragment;

import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.audoex.EffectManagerable;
import com.sinohb.hardware.test.module.effect.EffectController;
import com.sinohb.hardware.test.module.effect.EffectPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class EffectsFragment extends BaseManualFragment implements EffectPresenter.View {
//    private Button normalBtn;
//    private Button classicalBtn;
//    private Button jazzBtn;
//    private Button rockBtn;
//    private Button folkBtn;
//    private Button popBtn;
    private int mCurrentEffect = -1;
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

    public EffectsFragment() {
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
//        if (state == BaseTestTask.STATE_NONE) {
//            setOperateHintText(R.string.label_test_sound_effect_hint);
//        }
        if (state == BaseTestTask.STATE_NONE) {
            mCurrentEffect = -1;
            setStubVisibility(test_state_stub, View.GONE);
            setStubVisibility(operate_hint_stub, View.GONE);
            inflateOtherStub();
            inflateManualStub();
        } else if (state == BaseTestTask.STATE_RUNNING) {
            setStubVisibility(operate_hint_stub, View.GONE);
            inflateManualStub();
        } else if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE) {
            setStubVisibility(operate_hint_stub, View.GONE);
            setStubVisibility(manual_stub, View.GONE);
        }
    }

    @Override
    protected void inflateOtherStub() {
        if (other_stub == null) {
            LogTools.p(TAG, "other_stub is null");
            return;
        }
        if (other_stub.getParent() != null) {
            otherView = other_stub.inflate();
            View view = mainActivity.getLayoutInflater().inflate(R.layout.fragment_effect, null);
            ((FrameLayout) otherView).addView(view);
            initButton(view, R.id.normal_btn);
            initButton(view, R.id.classical_btn);
            initButton(view, R.id.jazz_btn);
            initButton(view, R.id.rock_btn);
            initButton(view, R.id.folk_btn);
            initButton(view, R.id.pop_btn);
        }
        other_stub.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.normal_btn:
                playEffect(EffectManagerable.EQ_MODE_NORMAL);
                break;
            case R.id.classical_btn:
                playEffect(EffectManagerable.EQ_MODE_CLASSIC);
                break;
            case R.id.jazz_btn:
                playEffect(EffectManagerable.EQ_MODE_JAZZ);
                break;
            case R.id.rock_btn:
                playEffect(EffectManagerable.EQ_MODE_ROCK);
                break;
            case R.id.folk_btn:
                playEffect(EffectManagerable.EQ_MODE_FOLK);
                break;
            case R.id.pop_btn:
                playEffect(EffectManagerable.EQ_MODE_POP);
                break;
        }
    }

    private void playEffect(int effect) {
        if (mCurrentEffect == effect){
            LogTools.p(TAG,"playEffect is same mCurrentEffect="+mCurrentEffect);
            return;
        }
        if (mPresenter != null) {
            ((EffectPresenter.Controller) mPresenter).playEffect(effect);
            mCurrentEffect = effect;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mHandler != null) {
            mHandler.removeMessages(Constants.HandlerMsg.MSG_EFFECT_PLAY_NONE);
            mHandler.removeMessages(Constants.HandlerMsg.MSG_EFFECT_PLAY);
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }
}
