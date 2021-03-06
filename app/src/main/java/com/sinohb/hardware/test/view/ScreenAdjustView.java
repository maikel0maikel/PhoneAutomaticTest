package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sinohb.hardware.test.entities.Calibration;
import com.sinohb.hardware.test.module.screenadjust.ScreenAdjustController;
import com.sinohb.hardware.test.module.screenadjust.ScreenAdjustPresenter;
import com.sinohb.hardware.test.widget.CalibrationView;


public class ScreenAdjustView extends Activity implements ScreenAdjustPresenter.View {
    private ScreenAdjustPresenter.Controller mPresenter;
    private CalibrationView calibrationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        new ScreenAdjustController(this);
        calibrationView = new CalibrationView(this);
        setContentView(calibrationView);
    }


    @Override
    public void setPresenter(ScreenAdjustPresenter.Controller presenter) {
        mPresenter = presenter;
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void displayAdjustView(int direction, Calibration calibration) {
        calibrationView.drawCalibrationCross(direction, calibration);
    }

    @Override
    public void adjustFailure(int direction) {
        switch (direction) {
            case ScreenAdjustPresenter.Controller.LEFT_TOP:
                Toast.makeText(this, "请按住屏幕左上边的+字光标", Toast.LENGTH_SHORT).show();
                break;
            case ScreenAdjustPresenter.Controller.RIGHT_TOP:
                Toast.makeText(this, "请按住屏幕右上边的+字光标", Toast.LENGTH_SHORT).show();
                break;
            case ScreenAdjustPresenter.Controller.LEFT_BOTTOM:
                Toast.makeText(this, "请按住屏幕左下边的+字光标", Toast.LENGTH_SHORT).show();
                break;
            case ScreenAdjustPresenter.Controller.RIGHT_BOTTOM:
                Toast.makeText(this, "请按住屏幕右下边的+字光标", Toast.LENGTH_SHORT).show();
                break;
            case ScreenAdjustPresenter.Controller.CENTER:
                Toast.makeText(this, "请按住屏幕中间的+字光标", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void complete() {
        Toast.makeText(this, "屏幕校准完成", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tmpx, tmpy;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            tmpx = event.getX();
            tmpy = event.getY();
            mPresenter.adjustTouch(tmpx, tmpy);
        }
        return true;
    }
}
