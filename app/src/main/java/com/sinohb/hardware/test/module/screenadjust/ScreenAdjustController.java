package com.sinohb.hardware.test.module.screenadjust;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;

import com.sinohb.hardware.test.app.BaseView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.entities.Calibration;
import com.sinohb.hardware.test.module.BaseController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;
import java.util.concurrent.FutureTask;

public class ScreenAdjustController extends BaseController implements ScreenAdjustPresenter.Controller {
    private static final String TAG = "ScreenAdjustController";
    private Calibration cal;
    static final int SAMPLE_COUNTS = 5;
    static final int EDGE_GAP = 50;
    private int X_RES;
    private int Y_RES;
    private AdjustHandler mHandler;
    private int mJustDirection;

    public ScreenAdjustController(BaseView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        if (mView != null ) {
            DisplayMetrics metrics = ((Context) mView).getResources().getDisplayMetrics();
            X_RES = metrics.widthPixels;
            Y_RES = metrics.heightPixels;
        }
        mHandler = new AdjustHandler(this);
        initScreenPoints();
        task = new ScreenAdjustTask(this);
    }

    @Override
    public void start() {
        if (task != null && (task.getmExecuteState() == BaseTestTask.STATE_NONE || task.isFinish())) {
            task.setFinish(false);
            task.setmExecuteState(BaseTestTask.STATE_NONE);
            FutureTask<Boolean> futureTask = new FutureTask(task);
            ThreadPool.getPool().executeSingleTask(futureTask);
        }
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
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_LEFT_TOP);
        mJustDirection = LEFT_TOP;
    }

    @Override
    public void adjustRightTop() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_RIGHT_TOP);
        mJustDirection = RIGHT_TOP;
    }

    @Override
    public void adjustLeftBottom() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_LEFT_BOTTOM);
        mJustDirection = LEFT_BOTTOM;
    }

    @Override
    public void adjustRightBottom() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_RIGHT_BOTTOM);
        mJustDirection = RIGHT_BOTTOM;
    }

    @Override
    public void adjustCenter() {
        mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_CENTER);
        mJustDirection = CENTER;
    }

    @Override
    public boolean adjustTouch(float tmpx, float tmpy) {
        if (isRealRect(tmpx, tmpy)) return false;

        cal.x[mJustDirection] = (int) (tmpx * 4096.0 / (float) X_RES + 0.5);
        cal.y[mJustDirection] = (int) (tmpy * 4096.0 / (float) Y_RES + 0.5);
        ((ScreenAdjustTask) task).adjustOk(mJustDirection);

        return true;
    }

    @Override
    public boolean isRealRect(float tmpx, float tmpy) {
        if (Math.abs(cal.xfb[mJustDirection] - tmpx) > 25 ||
                Math.abs(cal.yfb[mJustDirection] - tmpy) > 25) {
            ((ScreenAdjustTask) task).adjustFailure(mJustDirection);
            if (mView != null )
                ((ScreenAdjustPresenter.View) mView).adjustFailure(mJustDirection);
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (cal != null) {
            cal.xfb = null;
            cal.yfb = null;
            cal = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.destroy();
    }

    @Override
    public void complete() {
        boolean ret = performCalibration();
        if (ret) {
            saveCalibrationResult();
            mHandler.sendEmptyMessage(Constants.HandlerMsg.MSG_ADJUST_COMPLETE);
        } else {
            mJustDirection = LEFT_TOP;
            LogTools.e(TAG, "Calibration failed");
            start();
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
        LogTools.p(TAG, res);
    }


    private static class AdjustHandler extends Handler {
        private WeakReference<ScreenAdjustController> controllerWeakReference = null;


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
            if (controller == null||controller.mView==null) {
                LogTools.e(TAG, "controller is null");
                return;
            }
            switch (msg.what) {
                case Constants.HandlerMsg.MSG_ADJUST_LEFT_TOP:
                    ((ScreenAdjustPresenter.View) controller.mView).displayAdjustView(LEFT_TOP, controller.cal);
                    break;
                case Constants.HandlerMsg.MSG_ADJUST_RIGHT_TOP:
                    ((ScreenAdjustPresenter.View) controller.mView).displayAdjustView(RIGHT_TOP, controller.cal);
                    break;
                case Constants.HandlerMsg.MSG_ADJUST_LEFT_BOTTOM:
                    ((ScreenAdjustPresenter.View) controller.mView).displayAdjustView(LEFT_BOTTOM, controller.cal);
                    break;
                case Constants.HandlerMsg.MSG_ADJUST_RIGHT_BOTTOM:
                    ((ScreenAdjustPresenter.View) controller.mView).displayAdjustView(RIGHT_BOTTOM, controller.cal);
                    break;
                case Constants.HandlerMsg.MSG_ADJUST_CENTER:
                    ((ScreenAdjustPresenter.View) controller.mView).displayAdjustView(CENTER, controller.cal);
                    break;
                case Constants.HandlerMsg.MSG_ADJUST_COMPLETE:
                    controller.mView.complete(controller.task);
                    break;
            }
        }
    }
}
