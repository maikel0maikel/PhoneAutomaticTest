package com.sinohb.hardware.test.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.sinohb.hardware.test.module.video.VideoController;

public class CameraView extends SurfaceView {
    private Camera.Size mPreviewSize;
    private VideoController videoController;

    public CameraView(Context context, VideoController videoController) {
        super(context);
        this.videoController = videoController;
    }

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPresenter(VideoController videoController) {
        this.videoController = videoController;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int heightSize = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        if (mPreviewSize == null && videoController != null) {
            mPreviewSize = videoController.getFixedSize(Math.max(widthSize, heightSize), Math.min(widthSize, heightSize));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int curOrientation = getContext().getResources().getConfiguration().orientation;
        if (changed) {
            final int width = right - left;
            final int height = bottom - top;
            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
                if (curOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    previewWidth = mPreviewSize.height;
                    previewHeight = mPreviewSize.width;
                }
            }
            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

}
