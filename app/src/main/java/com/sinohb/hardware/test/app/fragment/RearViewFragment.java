package com.sinohb.hardware.test.app.fragment;

import android.view.View;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.module.rearview.RearViewController;
import com.sinohb.hardware.test.module.rearview.RearViewPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class RearViewFragment extends BaseManualFragment implements RearViewPresenter.View{
    public static RearViewFragment newInstance() {

        return new RearViewFragment();
    }
    public RearViewFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new RearViewController(this);
            super.init();
        }
    }
    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE||state == BaseTestTask.STATE_RUNNING) {
            setStubVisibility(operate_hint_stub, View.VISIBLE);
            setOperateHintText(R.string.label_test_rear_view_hint);
        }
    }

    @Override
    public void notifyRearViewStart() {
        if (mHandler!=null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setOperateHintText(R.string.label_rear_start);
                }
            });
        }
    }

    @Override
    public void notifyRearViewStop() {
        if (mHandler!=null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    setOperateHintText(R.string.label_rear_stop);
                }
            });
        }
    }
}
