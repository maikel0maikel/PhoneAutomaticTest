package com.sinohb.hardware.test.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.app.activity.ScreenRGBActivity;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.module.rgb.RGBController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;


public class ScreenRGBFragment extends BaseManualFragment {
    private boolean isRequestStartTask = false;
    public static ScreenRGBFragment newInstance() {

        return new ScreenRGBFragment();
    }
    public ScreenRGBFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new RGBController(this);
            super.init();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startRealTaskView();
    }


    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setOperateHintText(R.string.label_rgb_test_waite_hint);
        } else if (state == BaseTestTask.STATE_RUNNING) {
            isRequestStartTask = true;
            startRealTaskView();
            LogTools.p(TAG,"启动activity");
        }
    }


    protected void startRealTaskView() {
        if (isAdded() && isRequestStartTask) {
            startTaskActivity();
            isRequestStartTask = false;
        }
    }

    protected void startTaskActivity() {
        LogTools.p(TAG, "startTaskActivity");
        Intent intent = new Intent(mainActivity, ScreenRGBActivity.class);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ScreenRGBActivity.RESULT_OK) {
            mPresenter.notifyExecuteState(BaseTestTask.STATE_TEST_WAIT_OPERATE);
            if (data!=null&&mPresenter.getTask()!=null){
                ArrayList<StepEntity> stepEntities = (ArrayList<StepEntity>) data.getSerializableExtra(Constants.TASK_EXTRA_KEY);
                int id = data.getIntExtra(Constants.TASK_EXTRA_ID,SerialConstants.ITEM_SCREEN);
                mPresenter.getTask().setStepEntities(stepEntities);
                mPresenter.getTask().setmTaskId(id);
                LogTools.p(TAG,"onActivityResult id=:"+id);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
