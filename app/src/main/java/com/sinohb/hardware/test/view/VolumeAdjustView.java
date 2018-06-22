package com.sinohb.hardware.test.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.module.screen.ScreenController;
import com.sinohb.hardware.test.module.screen.ScreenPresenter;
import com.sinohb.hardware.test.module.volume.VolumeController;
import com.sinohb.hardware.test.module.volume.VolumePresenter;


public class VolumeAdjustView extends AppCompatActivity implements VolumePresenter.View {
    private VolumePresenter.Controller mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        new VolumeController(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void setPresenter(VolumePresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }


//    @Override
//    public Context getViewContext() {
//        return this;
//    }
}
