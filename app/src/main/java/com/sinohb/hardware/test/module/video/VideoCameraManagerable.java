package com.sinohb.hardware.test.module.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

public interface VideoCameraManagerable {

    int open();

    int open(int w, int h);

    int startPreviewDisplay(SurfaceHolder holder);

    int release();

    Camera.Size getFixedSize(int w, int h);
}
