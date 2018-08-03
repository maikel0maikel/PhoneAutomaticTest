package com.sinohb.hardware.test.app;

import android.os.Handler;
import android.os.Message;

import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;

public class BaseAutomaticFragment extends BaseFragment implements BaseExecuteView {

    protected BaseAutomaticHandler mHandler;

    @Override
    protected void init() {
        mHandler = new BaseAutomaticHandler(this);
    }


    @Override
    public void freshExecuteUI(int executeState) {
        if (mHandler != null) {
            mHandler.obtainMessage(Constants.HandlerMsg.MSG_BASE_AUTOMIC_EXCECUTE_STATE, executeState).sendToTarget();
        } else {
            LogTools.p("BaseAutomaticFragment", "handler is null executeState =" + executeState);
        }
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        mPresenter = (BaseExecutePresenter) presenter;
    }

    @Override
    public void complete(BaseTestTask task) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_BASE_AUTOMIC_COMPLETE_VIEW);
        }
        if (mainActivity != null) {
            mainActivity.notifyItemTaskFinish(task, testType);
        }
    }

    @Override
    public void destroy() {
        if (mHandler!=null){
            mHandler.removeMessages( Constants.HandlerMsg.MSG_BASE_AUTOMIC_EXCECUTE_STATE);
            mHandler.removeMessages( Constants.HandlerMsg.MSG_BASE_AUTOMIC_COMPLETE_VIEW);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mPresenter = null;
        LogTools.p(TAG,"BaseAuto onDestroy");
    }

    protected static class BaseAutomaticHandler extends Handler {
        protected WeakReference<BaseAutomaticFragment> weakReference = null;

        public BaseAutomaticHandler(BaseAutomaticFragment controller) {
            weakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                return;
            }
            BaseAutomaticFragment fragment = weakReference.get();
            if (fragment == null) {
                return;
            }
            if (msg.what == Constants.HandlerMsg.MSG_BASE_AUTOMIC_EXCECUTE_STATE) {
                fragment.freshUi((Integer) msg.obj);
            } else if (msg.what == Constants.HandlerMsg.MSG_BASE_AUTOMIC_COMPLETE_VIEW) {
                fragment.mainActivity.notifyTaskTestState(fragment.mPosition);
                if (fragment.retryBtn != null) {
                    fragment.retryBtn.setEnabled(true);
                }
            }
        }
    }
}
