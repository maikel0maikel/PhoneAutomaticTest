package com.sinohb.hardware.test.module.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.sinohb.hardware.test.app.BaseDisplayViewPresenter;

public interface VideoPresenter extends BaseDisplayViewPresenter{

    int openCamera(int w, int h);

    int releaseCamera();

    int startPreview(SurfaceHolder holder);

    Camera.Size getFixedSize(int w, int h);

    void notifyPreview();
}
