package com.sinohb.hardware.test.module.screenadjust;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.sinohb.hardware.test.entities.Calibration;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;
import java.util.concurrent.FutureTask;

public class ScreenAdjustController implements ScreenAdjustPresenter.Controller {
    private static final String TAG = "ScreenAdjustController";
    private ScreenAdjustPresenter.View mView;
    private Calibration cal;
    static final int SAMPLE_COUNTS = 5;
    static final int EDGE_GAP = 50;
    private int X_RES;
    private int Y_RES;
    private AdjustHandler mHandler;
    private ScreenAdjustTask adjustTask;
    private int mJustDirection;

    public ScreenAdjustController(ScreenAdjustPresenter.View view) {
        mView = view;
        mView.setPresenter(this);
        DisplayMetrics metrics = ((Context) mView).getResources().getDisplayMetrics();
        X_RES = metrics.widthPixels;
        Y_RES = metrics.heightPixels;
        mHandler = new AdjustHandler(this);
        initScreenPoints();
    }

    private void initScreenPoints() {
        cal = new Calibration();
        cal.xfb[LEFT_TOP] = EDGE_GAP;                // TopLeft
        cal.yfb[LEFT_TOP] = EDGE_GAP;

        cal.xfb[RIGHT_TOP] = X_RES - EDGE_GAP;        // TopRight
        cal.yfb[RIGHT_TOP] = EDGE_GAP;

        cal.xfb[RIGHT_BOTTOM] = X_RES - EDGE_GAP;    // BottomRight
        cal.yfb[RIGHT_BOTTOM] = Y_RES - EDGE_GAP;

        cal.xfb[LEFT_BOTTOM] = EDGE_GAP;            // BottomLeft
        cal.yfb[LEFT_BOTTOM] = Y_RES - EDGE_GAP;

        cal.xfb[CENTER] = X_RES / 2;                // Center
        cal.yfb[CENTER] = Y_RES / 2;
    }

    @Override
    public void adjustLeftTop() {
        mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_LEFT_TOP);
        mJustDirection = LEFT_TOP;
    }

    @Override
    public void adjustRightTop() {
        mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_RIGHT_TOP);
        mJustDirection = RIGHT_TOP;
    }

    @Override
    public void adjustLeftBottom() {
        mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_LEFT_BOTTOM);
        mJustDirection = LEFT_BOTTOM;
    }

    @Override
    public void adjustRightBottom() {
        mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_RIGHT_BOTTOM);
        mJustDirection = RIGHT_BOTTOM;
    }

    @Override
    public void adjustCenter() {
        mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_CENTER);
        mJustDirection = CENTER;
    }

    @Override
    public void adjustTouch(float tmpx, float tmpy) {
        if (Math.abs(cal.xfb[mJustDirection] - tmpx) > 15 &&
                Math.abs(cal.yfb[mJustDirection] - tmpy) > 15) {
            adjustTask.adjustFailure(mJustDirection);
            mView.adjustFailure(mJustDirection);
            return;
        }

        cal.x[mJustDirection] = (int) (tmpx * 4096.0 / (float) X_RES + 0.5);
        cal.y[mJustDirection] = (int) (tmpy * 4096.0 / (float) Y_RES + 0.5);
        adjustTask.adjustOk(mJustDirection);
    }

    @Override
    public void start() {
        startTask();
    }

    private void startTask() {
        adjustTask = new ScreenAdjustTask(this);
        FutureTask futureTask = new FutureTask(adjustTask);
        ThreadPool.getPool().execute(futureTask);
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void complete() {
        boolean ret = performCalibration();
        if (ret) {
            saveCalibrationResult();
            mHandler.sendEmptyMessage(AdjustHandler.MSG_ADJUST_COMPLETE);
        } else {
            mJustDirection = LEFT_TOP;
            LogTools.e(TAG, "Calibration failed");
            startTask();
        }

    }

    private boolean performCalibration() {
        float n, x, y, x2, y2, xy, z, zx, zy;
        float det, a, b, c, e, f, g;
        float scaling = (float) 65536.0;

        n = x = y = x2 = y2 = xy = 0;
        for (int i = 0; i < SAMPLE_COUNTS; i++) {
            n += 1.0;
            x += (float) cal.x[i];
            y += (float) cal.y[i];
            x2 += (float) (cal.x[i] * cal.x[i]);
            y2 += (float) (cal.y[i] * cal.y[i]);
            xy += (float) (cal.x[i] * cal.y[i]);
        }

        det = n * (x2 * y2 - xy * xy) + x * (xy * y - x * y2) + y * (x * xy - y * x2);
        if (det < 0.1 && det > -0.1) {
            LogTools.e(TAG, "determinant is too small, det =" + det);
            return false;
        }

        LogTools.p(TAG, "(n,x,y,x2,y2,xy,det)=("
                + n + ","
                + x + ","
                + y + ","
                + x2 + ","
                + y2 + ","
                + xy + ","
                + det + ")");

        a = (x2 * y2 - xy * xy) / det;
        b = (xy * y - x * y2) / det;
        c = (x * xy - y * x2) / det;
        e = (n * y2 - y * y) / det;
        f = (x * y - n * xy) / det;
        g = (n * x2 - x * x) / det;

        LogTools.p(TAG, "(a,b,c,e,f,g)=("
                + a + ","
                + b + ","
                + c + ","
                + e + ","
                + f + ","
                + g + ")");

        // Get sums for x calibration
        z = zx = zy = 0;
        for (int i = 0; i < SAMPLE_COUNTS; i++) {
            z += (float) cal.xfb[i];
            zx += (float) (cal.xfb[i] * cal.x[i]);
            zy += (float) (cal.xfb[i] * cal.y[i]);
        }
        // Now multiply out to get the calibration for X coordination
        cal.a[0] = (int) ((a * z + b * zx + c * zy) * (scaling));
        cal.a[1] = (int) ((b * z + e * zx + f * zy) * (scaling));
        cal.a[2] = (int) ((c * z + f * zx + g * zy) * (scaling));
        // Get sums for y calibration
        z = zx = zy = 0;
        for (int i = 0; i < SAMPLE_COUNTS; i++) {
            z += (float) cal.yfb[i];
            zx += (float) (cal.yfb[i] * cal.x[i]);
            zy += (float) (cal.yfb[i] * cal.y[i]);
        }
        // Now multiply out to get the calibration for Y coordination
        cal.a[3] = (int) ((a * z + b * zx + c * zy) * (scaling));
        cal.a[4] = (int) ((b * z + e * zx + f * zy) * (scaling));
        cal.a[5] = (int) ((c * z + f * zx + g * zy) * (scaling));

        cal.a[6] = (int) scaling;

        return true;
    }

    private void saveCalibrationResult() {
        String res = String.format("%d %d %d %d %d %d %d", cal.a[1], cal.a[2], cal.a[0], cal.a[4], cal.a[5], cal.a[3], cal.a[6]);
        LogTools.p(TAG,res);
    }

    @Override
    public void destroy() {

    }

    private static class AdjustHandler extends Handler {
        private WeakReference<ScreenAdjustController> controllerWeakReference = null;
        static final int MSG_ADJUST_LEFT_TOP = 111;
        static final int MSG_ADJUST_RIGHT_TOP = 112;
        static final int MSG_ADJUST_LEFT_BOTTOM = 113;
        static final int MSG_ADJUST_RIGHT_BOTTOM = 114;
        static final int MSG_ADJUST_CENTER = 115;
        static final int MSG_ADJUST_COMPLETE = 116;

        AdjustHandler(ScreenAdjustController controller) {
            controllerWeakReference = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (controllerWeakReference == null) {
                LogTools.e(TAG, "controllerWeakReference is null");
                return;
            }
            ScreenAdjustController controller = controllerWeakReference.get();
            if (controller == null) {
                LogTools.e(TAG, "controller is null");
                return;
            }
            switch (msg.what) {
                case MSG_ADJUST_LEFT_TOP:
                    controller.mView.displayAdjustView(LEFT_TOP, controller.cal);
                    break;
                case MSG_ADJUST_RIGHT_TOP:
                    controller.mView.displayAdjustView(RIGHT_TOP, controller.cal);
                    break;
                case MSG_ADJUST_LEFT_BOTTOM:
                    controller.mView.displayAdjustView(LEFT_BOTTOM, controller.cal);
                    break;
                case MSG_ADJUST_RIGHT_BOTTOM:
                    controller.mView.displayAdjustView(RIGHT_BOTTOM, controller.cal);
                    break;
                case MSG_ADJUST_CENTER:
                    controller.mView.displayAdjustView(CENTER, controller.cal);
                    break;
                case MSG_ADJUST_COMPLETE:
                    controller.mView.complete();
                    break;
            }
        }
    }
}
