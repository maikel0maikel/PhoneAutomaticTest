package com.sinohb.hardware.test.module.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;

public class VideoController extends BaseDisplayViewController implements VideoPresenter {
    private VideoCameraManagerable cameraManager;

    public VideoController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new VideoTestTask(this);
        cameraManager = new VideoCameraManager();
    }

    @Override
    public int openCamera(int w, int h) {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.open(w, h);
    }

    @Override
    public int releaseCamera() {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.release();
    }

    @Override
    public int startPreview(SurfaceHolder holder) {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.startPreviewDisplay(holder);
    }

    @Override
    public Camera.Size getFixedSize(int w, int h) {
        return cameraManager == null ? null : cameraManager.getFixedSize(w, h);
    }

    @Override
    public void notifyPreview() {
        if (task != null) {
            ((VideoTestTask)task).notifyPreView();
        }
    }



    @Override
    public void destroy() {
        cameraManager.release();
    }

//    @Override
//    public BaseTestTask getTask() {
//        return task;
//    }
}
