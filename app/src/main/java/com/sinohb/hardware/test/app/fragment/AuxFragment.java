package com.sinohb.hardware.test.app.fragment;


import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseFragment;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.module.auxiliary.AuxController;
import com.sinohb.hardware.test.module.auxiliary.AuxPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;

public class AuxFragment extends BaseManualFragment implements AuxPresenter.View{

    @Override
    protected void init() {
        testType = BaseTestTask.AUTOMATIC;
        if (mPresenter == null) {
            super.init();
            new AuxController(this);
        }
    }

    public static AuxFragment newInstance() {

        return new AuxFragment();
    }
    public AuxFragment(){
        init();
    }
    @Override
    public void notifyAuxStatusView(int state) {
        if (mainActivity!=null){
           mainActivity.setAuxStatusImg(state);
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE ) {
            //inflateOperateHintView();
            setOperateHintText(R.string.label_test_aux_hint);
            //test_state_stub.setVisibility(View.GONE);
        }else if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE){
            setOperateHintText(R.string.label_manual_test_aux_finish);
        }
    }
}
