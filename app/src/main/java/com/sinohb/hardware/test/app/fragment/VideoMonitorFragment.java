package com.sinohb.hardware.test.app.fragment;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseManualFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.video.VideoController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class VideoMonitorFragment extends BaseManualFragment implements SurfaceHolder.Callback {
    private SurfaceView mSurfaceView;
    public static VideoMonitorFragment newInstance() {

        return new VideoMonitorFragment();
    }
    public VideoMonitorFragment(){
        init();
    }
    @Override
    protected void init() {
        testType = BaseTestTask.MANUAL;
        if (mPresenter == null) {
            new VideoController(this);
            super.init();
        }
    }

    @Override
    protected void freshUi(int state) {
        super.freshUi(state);
        if (state == BaseTestTask.STATE_NONE && mRootView != null) {
           // if (test_state_stub != null) {
           //     test_state_stub.setVisibility(View.GONE);
           // }
            if (manual_stub != null) {
                manual_stub.setVisibility(View.GONE);
            }
            //inflateOperateHintView();
            setOperateHintText(R.string.label_test_video_wait_hint);
            inflateOtherStub();
        } else if (state == BaseTestTask.STATE_FINISH) {
            if (otherView != null) {
                ((FrameLayout) otherView).removeAllViews();
            }
        } else if (state == BaseTestTask.STATE_RUNNING) {
            if (operate_hint_stub != null) {
                operate_hint_stub.setVisibility(View.GONE);
            }
            if (other_stub != null) {
                other_stub.setVisibility(View.VISIBLE);
            }
            if (mSurfaceView != null) {
                mSurfaceView.getHolder().addCallback(VideoMonitorFragment.this);
            }
        }else if (state == BaseTestTask.STATE_TEST_WAIT_OPERATE && other_stub != null) {
            other_stub.setVisibility(View.GONE);
            if (manual_stub != null) {
                manual_stub.setVisibility(View.VISIBLE);
            }
            if (operate_hint_stub != null) {
                operate_hint_stub.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void inflateOtherStub() {
        if (other_stub == null) {
            return;
        }
        if (other_stub.getParent() != null) {
            otherView = other_stub.inflate();
        }
        if (otherView != null) {
            View view = mainActivity.getLayoutInflater().inflate(R.layout.fragment_video, null);
            ((FrameLayout) otherView).addView(view);
            mSurfaceView = (SurfaceView) view.findViewById(R.id.video_sv);
        }
        other_stub.setVisibility(View.GONE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mSurfaceView != null) {
            mSurfaceView.post(new Runnable() {
                @Override
                public void run() {
                    int w = mSurfaceView.getWidth();
                    int h = mSurfaceView.getHeight();
                    LogTools.p(TAG, "mSurfaceView w = " + w + ",h=" + h);
                    int openResult = ((VideoController) mPresenter).openCamera(w, h);
                    if (openResult == Constants.DEVICE_SUPPORTED) {
                        ((VideoController) mPresenter).startPreview(mSurfaceView.getHolder());
                        ((VideoController) mPresenter).notifyPreview();
                    }
                }
            });
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogTools.p(TAG, "[surfaceChanged] method call ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(this);
        }
        ((VideoController) mPresenter).releaseCamera();
        LogTools.p(TAG, "[surfaceDestroyed] method call ");
    }
}
