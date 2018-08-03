package com.sinohb.hardware.test.app;

import android.os.Handler;
import android.os.Message;

import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;


public class BaseManualFragment extends BaseFragment implements BaseDisplayViewView {
    protected ManualHandler mHandler;

    @Override
    protected void init() {
        mHandler = new ManualHandler(this);
    }


    @Override
    public void freshExecuteUI(int executeState) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_BASE_MANUAL_EXCECUTE_STATE, executeState).sendToTarget();
        }
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        mPresenter = (BaseExecutePresenter) presenter;
    }

    @Override
    public void complete(BaseTestTask task) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BASE_MANUAL_COMPLETE_VIEW);
        }
        if (mainActivity != null) {
            mainActivity.notifyItemTaskFinish(task,testType);
        }
    }

    @Override
    public void destroy() {
        if (mHandler!=null){
            mHandler.removeMessages(Constants.HandlerMsg.MSG_BASE_MANUAL_EXCECUTE_STATE);
            mHandler.removeMessages(Constants.HandlerMsg.MSG_BASE_MANUAL_DISPLAY_VIEW);
            mHandler.removeMessages(Constants.HandlerMsg.MSG_BASE_MANUAL_COMPLETE_VIEW);
            mHandler.removeCallbacksAndMessages(null);
        }
        mPresenter = null;
        LogTools.p(this.getClass().getSimpleName(),"BaseManualFragment onDestroy");
    }

    @Override
    protected void pass() {
        super.pass();
        ((BaseDisplayViewPresenter) mPresenter).testOk();
    }

    @Override
    protected void unpass() {
        super.unpass();
        ((BaseDisplayViewPresenter) mPresenter).testFail();
    }

    @Override
    public void displayView() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BASE_MANUAL_DISPLAY_VIEW);
        }
    }

    protected static class ManualHandler extends Handler {
        protected WeakReference<BaseManualFragment> controllerWeakReference;

        public ManualHandler(BaseManualFragment controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                return;
            }
            BaseManualFragment fragment = controllerWeakReference.get();
            if (fragment == null) {
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_BASE_MANUAL_EXCECUTE_STATE:
                    fragment.freshUi((Integer) msg.obj);
                    break;
                case Constants.HandlerMsg.MSG_BASE_MANUAL_DISPLAY_VIEW:
                    if (fragment.mainActivity != null)
                        fragment.mainActivity.showFragment(fragment.mPosition);
                    break;
                case Constants.HandlerMsg.MSG_BASE_MANUAL_COMPLETE_VIEW:
                    if (fragment.mainActivity == null) {
                        LogTools.p(TAG, "mainActivity is null");
                        return;
                    }
                    if (fragment.retryBtn!=null) {
                        fragment.retryBtn.setEnabled(true);
                    }
                    fragment.mainActivity.notifyTaskTestState(fragment.mPosition);
                    break;
            }
        }
    }

}
