package com.sinohb.hardware.test.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.module.brightness.BrightnessController;
import com.sinohb.hardware.test.module.brightness.BrightnessPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class ScreenBrightnessFragment extends BaseManualFragment /** implements BrightnessPresenter.View**/
{
    private Button lBtn;
    private Button mBtn;
    private Button hBtn;

    public static ScreenBrightnessFragment newInstance() {

        return new ScreenBrightnessFragment();
    }

    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            //mHandler = new BrightnessHandler(this);
            super.init();
            new BrightnessController(this);
        }
    }

    public ScreenBrightnessFragment() {
        init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setStubVisibility(test_state_stub, View.GONE);
            setStubVisibility(operate_hint_stub, View.GONE);
            inflateOtherStub();
            inflateManualStub();
            setBtnEnable(false);
        } else if (state == BaseTestTask.STATE_RUNNING) {
            setStubVisibility(operate_hint_stub, View.GONE);
            inflateManualStub();
            setBtnEnable(true);
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
            View view = mainActivity.getLayoutInflater().inflate(R.layout.fragment_brightness, null);
            ((FrameLayout) otherView).addView(view);
            lBtn = initButton(view, R.id.bright_l_btn);
            mBtn = initButton(view, R.id.bright_m_btn);
            hBtn = initButton(view, R.id.bright_h_btn);
        }
        other_stub.setVisibility(View.VISIBLE);
    }

    private void setBtnEnable(boolean enable){
        if (lBtn!=null){
            lBtn.setEnabled(enable);
        }
        if (mBtn!=null){
            mBtn.setEnabled(enable);
        }
        if (hBtn!=null){
            hBtn.setEnabled(enable);
        }
        if (passBtn!=null){
            passBtn.setEnabled(enable);
        }
        if (unpassBtn!=null){
            unpassBtn.setEnabled(enable);
        }
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bright_l_btn:
                if (mPresenter != null) {
                    ((BrightnessPresenter.Controller) mPresenter).changeLow();
                }
                break;
            case R.id.bright_m_btn:
                if (mPresenter != null) {
                    ((BrightnessPresenter.Controller) mPresenter).changeMedium();
                }
                break;
            case R.id.bright_h_btn:
                if (mPresenter != null) {
                    ((BrightnessPresenter.Controller) mPresenter).changeHigh();
                }
                break;
        }
    }

//    @Override
//    public void changeLow() {
//        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BRIGHTNESS_L_HINT);
//    }
//
//    @Override
//    public void changeMedium() {
//        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BRIGHTNESS_M_HINT);
//    }
//
//    @Override
//    public void changeHigh() {
//        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BRIGHTNESS_H_HINT);
//    }
//
//    private static class BrightnessHandler extends ManualHandler {
//
//        private BrightnessHandler(BaseManualFragment controller) {
//            super(controller);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (controllerWeakReference == null) {
//                return;
//            }
//            ScreenBrightnessFragment fragment = (ScreenBrightnessFragment) controllerWeakReference.get();
//            if (fragment == null) {
//                return;
//            }
//            switch (msg.what) {
//                case Constants.HandlerMsg.MSG_BRIGHTNESS_L_HINT:
//                    fragment.setOperateHintText(R.string.label_test_screen_bringhtness_l_hint);
//                    break;
//                case Constants.HandlerMsg.MSG_BRIGHTNESS_M_HINT:
//                    fragment.setOperateHintText(R.string.label_test_screen_bringhtness_m_hint);
//                    break;
//                case Constants.HandlerMsg.MSG_BRIGHTNESS_H_HINT:
//                    fragment.setOperateHintText(R.string.label_test_screen_bringhtness_h_hint);
//                    break;
//            }
//        }
//    }

    @Override
    protected void pass() {
        super.pass();
        setStubVisibility(other_stub, View.GONE);
    }

    @Override
    protected void unpass() {
        super.unpass();
        setStubVisibility(other_stub, View.GONE);
    }
}
