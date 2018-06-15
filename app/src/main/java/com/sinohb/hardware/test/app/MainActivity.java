package com.sinohb.hardware.test.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.screen.ScreenController;
import com.sinohb.hardware.test.module.screen.ScreenPresenter;


public class MainActivity extends AppCompatActivity implements ScreenPresenter.View{
    private ScreenPresenter.Controller mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
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
