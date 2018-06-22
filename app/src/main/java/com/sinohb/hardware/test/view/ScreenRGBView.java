package com.sinohb.hardware.test.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.screen.ScreenController;
import com.sinohb.hardware.test.module.screen.ScreenPresenter;


public class ScreenRGBView extends AppCompatActivity implements ScreenPresenter.View {
    private ScreenPresenter.Controller mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        new ScreenController(this);

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
    public void setPresenter(ScreenPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }


//    @Override
//    public Context getViewContext() {
//        return this;
//    }
}
