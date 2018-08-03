package com.sinohb.hardware.test.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.screen.ScreenController;
import com.sinohb.hardware.test.module.screen.ScreenPresenter;
import com.sinohb.hardware.test.task.BaseTestTask;


public class ScreenRGBActivity extends Activity implements ScreenPresenter.View {
    private ScreenPresenter.Controller mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        new ScreenController(this);
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void displayR() {
        getWindow().setBackgroundDrawableResource(R.drawable.red_drawable);
    }

    @Override
    public void displayG() {
        getWindow().setBackgroundDrawableResource(R.drawable.green_drawable);
    }

    @Override
    public void displayB() {
        getWindow().setBackgroundDrawableResource(R.drawable.blue_drawable);
    }

    @Override
    public void complete(BaseTestTask task) {
        Intent intent = new Intent();
        if (task!=null) {
            intent.putExtra(Constants.TASK_EXTRA_KEY, task.getStepEntities());
        }
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setPresenter(ScreenPresenter.Controller presenter) {
        mPresenter = presenter;
    }


//    @Override
//    public Context getViewContext() {
//        return this;
//    }
}
