package com.sinohb.hardware.test.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.adapter.TestDetailAdapter;
import com.sinohb.hardware.test.app.activity.MainActivity;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;

public class BaseFragment extends Fragment implements View.OnClickListener {
    protected static final String TAG = BaseFragment.class.getClass().getSimpleName();
    protected View mRootView;
    protected BaseExecutePresenter mPresenter;
    protected int testType;
    protected ViewStub operate_hint_stub;
    protected ViewStub test_state_stub;
    protected ViewStub retry_stub;
    protected ViewStub manual_stub;
    protected ViewStub other_stub;
    protected ViewStub detail_stub;
    protected TextView testStateView;
    protected TextView opreteHintTextView;
    protected MainActivity mainActivity;
    protected int mPosition;
    protected View otherView;
    protected Button retryBtn;
    private ListView detailLv;
    private TextView detailTv;
    protected Button passBtn;
    protected Button unpassBtn;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mainActivity == null)
            mainActivity = (MainActivity) activity;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_base, container, false);
        }
        initStub();
        BaseTestTask task = getPresenter().getTask();
        if (task != null) {
            int state = task.getmExecuteState();
            freshUi(state);
            if (task.isFinish()&&retryBtn!=null) {
                retryBtn.setEnabled(true);
            }
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void initStub() {
        operate_hint_stub = (ViewStub) mRootView.findViewById(R.id.operate_hint_stub);
        test_state_stub = (ViewStub) mRootView.findViewById(R.id.test_state_stub);
        retry_stub = (ViewStub) mRootView.findViewById(R.id.retry_stub);
        manual_stub = (ViewStub) mRootView.findViewById(R.id.manual_stub);
        other_stub = (ViewStub) mRootView.findViewById(R.id.other_stub);
        detail_stub = (ViewStub) mRootView.findViewById(R.id.detail_stub);
    }

    public BaseExecutePresenter getPresenter() {
        return mPresenter;
    }

    public int getTestType() {
        return testType;
    }

    protected void freshUi(int state) {
        if (mainActivity != null) {
            mainActivity.notifyTaskTestState(mPosition);
        }
        LogTools.p(TAG, "freshUi :" + state);
        if (mRootView == null) {
            LogTools.p(TAG, "mRootView is null so not to display view");
            return;
        }
        switch (state) {
            case BaseTestTask.STATE_NONE:
                setStateView();
                setTestStateHintText(R.string.lable_testing_waite);
                if (mainActivity!=null){
                    mainActivity.setBtnEnable();
                }
                break;
            case BaseTestTask.STATE_PAUSE:
                setStateView();
                setTestStateHintText(R.string.lable_testing_pause);
                break;
            case BaseTestTask.STATE_RUNNING:
                setStateView();
                setTestStateHintText(R.string.lable_testing);
                if (mainActivity!=null){
                    mainActivity.notifyStopBtn();
                }
                break;
            case BaseTestTask.STATE_TEST_WAIT_OPERATE:
                inflateManualStub();
                setOperateHintText(R.string.label_manual_test_finish);
                setStubVisibility(retry_stub, View.GONE);
                setStubVisibility(operate_hint_stub, View.VISIBLE);
                break;
            case BaseTestTask.STATE_FINISH:
                testFinish();
                if (testType == BaseTestTask.MANUAL) {
                    setOperateHintText(R.string.label_test_pass_btn_hint);
                } else {
                    setOperateHintText(R.string.label_test_finish);
                }
                setStubVisibility(other_stub, View.GONE);
                break;
            case BaseTestTask.STATE_TEST_UNPASS:
                testFinish();
                if (testType == BaseTestTask.MANUAL) {
                    setOperateHintText(R.string.label_test_un_pass_btn_hint);
                } else {
                    setOperateHintText(R.string.label_test_fail);
                }
                setStubVisibility(other_stub, View.GONE);
                break;
        }
    }

    private void setStateView() {
        if (testType == BaseTestTask.MANUAL) {
            inflateOperateHintView();
            setStubVisibility(manual_stub, View.GONE);
            setStubVisibility(operate_hint_stub, View.VISIBLE);
        } else {
            inflateTestStateView();
            setStubVisibility(operate_hint_stub, View.GONE);
        }
        setStubVisibility(retry_stub, View.GONE);
        setStubVisibility(detail_stub, View.GONE);

    }

    protected void setOperateHintText(int label_test_fail) {
        if (opreteHintTextView != null) {
            opreteHintTextView.setText(label_test_fail);
        }
    }

    protected void setOperateHintText(CharSequence label_test_fail) {
        if (opreteHintTextView != null) {
            opreteHintTextView.setText(label_test_fail);
        }
    }

    protected void setTestStateHintText(int resid) {
        if (testStateView != null) {
            testStateView.setText(resid);
        }
    }

    protected void setStubVisibility(ViewStub stub, int visiable) {
        if (stub != null) {
            stub.setVisibility(visiable);
        }
    }

    private void testFinish() {
        inflateRetryStub();
        inflateOperateHintView();
        setStubVisibility(test_state_stub, View.GONE);
        setStubVisibility(operate_hint_stub, View.VISIBLE);
        setStubVisibility(retry_stub, View.VISIBLE);
        setStubVisibility(manual_stub, View.GONE);
        if (retryBtn != null) {
            retryBtn.setEnabled(false);
        }
    }

    protected void inflateOperateHintView() {
        if (operate_hint_stub != null) {
            if (operate_hint_stub.getParent() != null) {
                View opreteView = operate_hint_stub.inflate();
                opreteHintTextView = (TextView) opreteView.findViewById(R.id.operate_hint_tv);
            }
            operate_hint_stub.setVisibility(View.VISIBLE);
        }
    }

    protected void inflateTestStateView() {
        if (test_state_stub != null) {
            if (test_state_stub.getParent() != null) {
                View view = test_state_stub.inflate();
                testStateView = (TextView) view.findViewById(R.id.test_state_tv);
            }
            test_state_stub.setVisibility(View.VISIBLE);
        }
    }

    protected void inflateManualStub() {
        if (manual_stub != null) {
            if (manual_stub.getParent() != null) {
                View manualView = manual_stub.inflate();
                passBtn = initButton(manualView, R.id.test_pass_btn);
                unpassBtn = initButton(manualView, R.id.test_un_pass_btn);
            }
            manual_stub.setVisibility(View.VISIBLE);
        }

    }

    protected void inflateRetryStub() {
        if (retry_stub != null) {
            if (retry_stub.getParent() != null) {
                View retryView = retry_stub.inflate();
                retryBtn = initButton(retryView, R.id.retry_btn);
                initButton(retryView, R.id.see_detail_btn);
            }
            retry_stub.setVisibility(View.VISIBLE);
        }
    }

    private void inflateDetailStub() {
        if (detail_stub != null) {
            if (detail_stub.getParent() != null) {
                View detail = detail_stub.inflate();
                detailLv = (ListView) detail.findViewById(R.id.test_detail_lv);
                detailTv = (TextView) detail.findViewById(R.id.test_detail_des_tv);
                initButton(detail, R.id.close_detail_iv);
            }
            detail_stub.setVisibility(View.VISIBLE);
        }

        BaseTestTask task = mPresenter.getTask();
        if (task != null) {
            if (task.getDescriptionSrc() > 0 && detailTv != null) {
                detailTv.setText(task.getDescriptionSrc());
            }
            ArrayList<StepEntity> list = task.getStepEntities();
            if (detailLv != null) {
                TestDetailAdapter adapter = (TestDetailAdapter) detailLv.getAdapter();
                if (adapter != null) {
                    adapter.setStepEntities(list);
                } else {
                    adapter = new TestDetailAdapter(list);
                }
                detailLv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    protected void inflateOtherStub() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_btn:
                v.setEnabled(false);
                if (mPresenter != null) {
                    mPresenter.start();
                }
                break;
            case R.id.see_detail_btn:
                seeDetail();
                break;
            case R.id.test_pass_btn:
                inflateRetryStub();
                pass();
                manual_stub.setVisibility(View.GONE);
                setOperateHintText(R.string.label_test_pass_btn_hint);
                break;
            case R.id.test_un_pass_btn:
                inflateRetryStub();
                setOperateHintText(R.string.label_test_un_pass_btn_hint);
                unpass();
                manual_stub.setVisibility(View.GONE);
                break;
            case R.id.close_detail_iv:
                setStubVisibility(detail_stub, View.GONE);
                setStubVisibility(operate_hint_stub, View.VISIBLE);
                setStubVisibility(retry_stub, View.VISIBLE);
                break;
        }
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    protected void pass() {

    }

    protected void unpass() {

    }

    protected <T extends View> T initButton(View rootView, int resId) {
        T t = (T) rootView.findViewById(resId);
        t.setOnClickListener(this);
        return t;

    }

    protected void init() {
    }

    protected void seeDetail() {
        inflateDetailStub();
        setStubVisibility(operate_hint_stub, View.GONE);
        setStubVisibility(retry_stub, View.GONE);
    }
}
