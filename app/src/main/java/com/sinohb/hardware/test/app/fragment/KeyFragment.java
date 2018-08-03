package com.sinohb.hardware.test.app.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.app.KeyListener;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.key.KeyController;
import com.sinohb.hardware.test.module.key.KeyPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.utils.SP2PXUtils;
import com.sinohb.logger.LogTools;


public class KeyFragment extends BaseManualFragment implements KeyPresenter.View, KeyListener {
    public static KeyFragment newInstance() {

        return new KeyFragment();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new KeyController(this);
            mHandler = new KeyHandler(this);
        }
    }
    public KeyFragment(){
        init();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainActivity!=null){
            mainActivity.addKeyListener(this);
        }
    }

    @Override
    public void pressKeyHint(int keyCode,int errorCount) {
        mHandler.obtainMessage(Constants.HandlerMsg.MSG_KEY_PRESEE_KEYCODE,keyCode,errorCount).sendToTarget();
    }


    static class KeyHandler extends ManualHandler {

        KeyHandler(BaseManualFragment controller) {
            super(controller);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                LogTools.p(TAG, "weakReference is null");
                return;
            }
            KeyFragment fragment = (KeyFragment) controllerWeakReference.get();
            if (fragment == null) {
                LogTools.p(TAG, "fragment is null");
                return;
            }
            if (msg.what == Constants.HandlerMsg.MSG_KEY_PRESEE_KEYCODE){
                fragment.displayHintView(msg.arg1,msg.arg2);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mHandler!=null){
            mHandler.removeMessages(Constants.HandlerMsg.MSG_KEY_PRESEE_KEYCODE);
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    protected void displayHintView(int keyCode, int errorCode) {
        int resId = 0;
        switch (keyCode) {
            case Constants.HandlerMsg.MSG_KEY_PRESS_HOME:
                resId =  R.string.lable_test_key_home_hint;
                break;
            case Constants.HandlerMsg.MSG_KEY_PRESS_UP:
                resId =  R.string.lable_test_key_up_hint;
                break;
            case Constants.HandlerMsg.MSG_KEY_PRESS_DOWN:
                resId =  R.string.lable_test_key_down_hint;
                break;
            case Constants.HandlerMsg.MSG_KEY_PRESS_ENTER:
                resId =  R.string.lable_test_key_enter_hint;
                break;
            case Constants.HandlerMsg.MSG_KEY_PRESS_BACK:
                resId =  R.string.lable_test_key_back_hint;
                break;
        }
        setHintText(errorCode, resId);
    }

    protected void setHintText(int errorCode, int resId) {
        if (resId>0){
            if (errorCode>0){
                String str = getString(resId);
                String erroString = String.format(getString(R.string.lable_test_key_error_hint),errorCode);
                SpannableString spannableString = new SpannableString(str+erroString);
                spannableString.setSpan(new ForegroundColorSpan(Color.RED),str.length(),spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new AbsoluteSizeSpan(SP2PXUtils.sp2px(18)),str.length(),spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                setOperateHintText(spannableString);
            }else {
                setOperateHintText(resId);
            }
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE  ) {
            setOperateHintText(R.string.label_test_key_wait_hint);
        }else if (state == BaseTestTask.STATE_FINISH){
            setOperateHintText(R.string.label_test_finish);
        }else if (state == BaseTestTask.STATE_TEST_UNPASS){
            setOperateHintText(R.string.label_test_fail);
        }
        if (manual_stub!=null){
            manual_stub.setVisibility(View.GONE);
        }
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            ((KeyPresenter.Controller)mPresenter).notifyPressKey(keyCode);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainActivity!=null){
            mainActivity.removeKeyListener(this);
        }
    }

}
