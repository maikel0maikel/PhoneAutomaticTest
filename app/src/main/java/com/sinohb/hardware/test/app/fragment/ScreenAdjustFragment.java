package com.sinohb.hardware.test.app.fragment;

import android.app.Activity;
import android.content.Intent;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.activity.ScreenAdjustActivity;
import com.sinohb.hardware.test.task.BaseTestTask;


public class ScreenAdjustFragment extends ScreenRGBFragment   {
    public static ScreenAdjustFragment newInstance() {

        return new ScreenAdjustFragment();
    }

    @Override
    protected void startTaskActivity() {
        Intent intent = new Intent(mainActivity, ScreenAdjustActivity.class);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE) {
            setOperateHintText(R.string.label_test_screen_adjust_wait_hint);
        }
    }
}
