package com.sinohb.hardware.test.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CalibrationActivity extends Activity{
    static final int SAMPLE_COUNTS = 5;
    static final int POINT_DEGREE = 2;
    static final int FACTOR_COUNTS = 7;
    static final int TOP_LEFT = 0;
    static final int TOP_RIGHT = 1;
    static final int BOTTOM_RIGHT = 2;
    static final int BOTTOM_LEFT = 3;
    static final int CENTER = 4;
    static final int X_AXIS = 0;
    static final int Y_AXIS = 1;
    static final int EDGE_GAP = 50;

    static final String CALIBRATION_FILE = "/data/pointercal";
    static final String TAG = "CalibrationActivity";
    static final boolean DEBUG = true;

    private int X_RES;
    private int Y_RES;
    private Display dpy;

    private calibration cal;
    CalibrationView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cal = new calibration();

        dpy = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        X_RES = dpy.getWidth();
        Y_RES = dpy.getHeight();

        initScreenPoints();
        view = new CalibrationView(this);
        setContentView(view);
    }

    class calibration {
        int x[] = new int[5];
        int y[] = new int[5];
        int xfb[] = new int[5];
        int yfb[] = new int[5];
        int a[] = new int[7];
    }

    // TopLeft-->TopRight-->BottomRight-->BottomLeft-->Center
    // For 240 * 320 resolution, we use 50 pixel as edge gap
    private boolean initScreenPoints() {
        cal.xfb[TOP_LEFT] = EDGE_GAP;                // TopLeft
        cal.yfb[TOP_LEFT] = EDGE_GAP;

        cal.xfb[TOP_RIGHT] = X_RES - EDGE_GAP;        // TopRight
        cal.yfb[TOP_RIGHT] = EDGE_GAP;

        cal.xfb[BOTTOM_RIGHT] = X_RES - EDGE_GAP;    // BottomRight
        cal.yfb[BOTTOM_RIGHT] = Y_RES - EDGE_GAP;

        cal.xfb[BOTTOM_LEFT] = EDGE_GAP;            // BottomLeft
        cal.yfb[BOTTOM_LEFT] = Y_RES - EDGE_GAP;

        cal.xfb[CENTER] = X_RES / 2;                // Center
        cal.yfb[CENTER] = Y_RES / 2;
        return true;
    }

    private boolean perform_calibration() {
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
            Log.w(TAG, "determinant is too small, det =" + det);
            return false;
        }

        if (DEBUG) {
            Log.i(TAG, "(n,x,y,x2,y2,xy,det)=("
                    + n + ","
                    + x + ","
                    + y + ","
                    + x2 + ","
                    + y2 + ","
                    + xy + ","
                    + det + ")");
        }

        a = (x2 * y2 - xy * xy) / det;
        b = (xy * y - x * y2) / det;
        c = (x * xy - y * x2) / det;
        e = (n * y2 - y * y) / det;
        f = (x * y - n * xy) / det;
        g = (n * x2 - x * x) / det;

        Log.i(TAG, "(a,b,c,e,f,g)=("
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

    private boolean saveCalibrationResult() {
        FileOutputStream fos;
        String res = "";

        // save the calibration factor in file system for InputDevice
        try {
            fos = openFileOutput("pointercal.txt", Context.MODE_PRIVATE);

            res = String.format("%d %d %d %d %d %d %d", cal.a[1], cal.a[2], cal.a[0], cal.a[4], cal.a[5], cal.a[3], cal.a[6]);

            if (DEBUG) {
                Log.i(TAG, "calibration result=" + res);
            }
            fos.write(res.getBytes());
            fos.close();
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Log.w(TAG, "open calibration file write error: " + CALIBRATION_FILE);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
    private int screen_pos;
    public class CalibrationView extends View {
        private Canvas cv;
        private Paint paint;
        private Bitmap bmp;

        private Context mContext;

        public CalibrationView(Context c) {
            super(c);
            // set full screen and no title
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            mContext = c;
            paint = new Paint();
            paint.setDither(true);
            paint.setAntiAlias(true); ////抗锯齿，如果没有调用这个方法，写上去的字不饱满，不美观，看地不太清楚
            paint.setStrokeWidth(2); //设置空心线宽
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE); //设置画笔风格，空心或者实心。
            bmp = Bitmap.createBitmap(X_RES, Y_RES, Bitmap.Config.ARGB_8888);
            cv = new Canvas(bmp);
            screen_pos = 0;
            drawCalibrationCross(screen_pos);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bmp, 0, 0, null);

            //“欢迎”字体大小
            float txt_welcome_size = 60;
            //“欢迎”字数
            float txt_welcome_count = 2;
            //"请按住十字光标以校准"字体大小
            float txt_content_size = 36;
            //"请按住十字光标以校准"字数
            float txt_content1_count = 10;
            //"你的屏幕"字数
            float txt_content2_count = 4;

            //"欢迎"
            Paint p = new Paint();
            p.setTextSize(txt_welcome_size);
            p.setFakeBoldText(true);
            p.setColor(Color.GRAY);
            canvas.drawText("欢迎",
                    (X_RES / 2) - (txt_welcome_size / 2) - txt_welcome_size / 2,
                    Y_RES / 2 - txt_welcome_size - 30,
                    p);

            //"请按住光标中央以校准"
            p.setFakeBoldText(false);
            p.setColor(Color.GREEN);
            p.setTextSize(txt_content_size);
            //参数2（X_RES / 2 - (txt_content_size / 2 * txt_content1_count)）：当前屏幕宽度的一半减去字数
            canvas.drawText("请按住十字光标以校准",
                    X_RES / 2 - (txt_content_size / 2 * txt_content1_count),
                    Y_RES / 2 + 150,
                    p);

            //"你的屏幕"
            p.setColor(Color.GREEN);
            p.setTextSize(txt_content_size);
            canvas.drawText("你的屏幕",
                    X_RES / 2 - txt_content_size / 2 * txt_content2_count,
                    Y_RES / 2 + 200,
                    p);

            //线,渐变效果!!!
            Shader shader = new LinearGradient((X_RES / 2) - (txt_welcome_size / 2) - txt_welcome_size * 2,
                    (Y_RES / 2) - txt_welcome_size,
                    X_RES / 2,
                    (Y_RES / 2) - txt_welcome_size,
                    new int[]{Color.WHITE, Color.GREEN},
                    null,
                    Shader.TileMode.MIRROR);
            p.setShader(shader);

            canvas.drawLine((X_RES / 2) - (txt_welcome_size / 2) - txt_welcome_size * 2,
                    (Y_RES / 2) - txt_welcome_size,
                    (X_RES / 2) + (txt_welcome_size / 2) + txt_welcome_size * 2,
                    (Y_RES / 2) - txt_welcome_size,
                    p);

        }

        private boolean drawCalibrationCross(int pos) {

            if (DEBUG) {
                Log.i(TAG, "draw cross at pos " + pos);
            }

            cv.drawColor(Color.BLACK);

            // draw X line
            cv.drawLine(cal.xfb[pos] - 10, cal.yfb[pos],
                    cal.xfb[pos] - 2, cal.yfb[pos], paint);
            cv.drawLine(cal.xfb[pos] + 2, cal.yfb[pos],
                    cal.xfb[pos] + 10, cal.yfb[pos], paint);

            // draw Y line
            cv.drawLine(cal.xfb[pos], cal.yfb[pos] - 10,
                    cal.xfb[pos], cal.yfb[pos] - 2, paint);
            cv.drawLine(cal.xfb[pos], cal.yfb[pos] + 2,
                    cal.xfb[pos], cal.yfb[pos] + 10, paint);

            invalidate();
            return true;
        }

    }


    public boolean onTouchEvent(MotionEvent event) {
        float tmpx, tmpy;
        boolean ret;
        if (screen_pos > SAMPLE_COUNTS - 1) {
            Log.i(TAG, "get sample ok");
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            tmpx = event.getX();
            tmpy = event.getY();
            if (Math.abs(cal.xfb[screen_pos] - tmpx) > 15 &&
                    Math.abs(cal.yfb[screen_pos] - tmpy) > 15) {
                Toast.makeText(this, "无效的校准点",Toast.LENGTH_SHORT).show();
                return false;
            }

            cal.x[screen_pos] = (int) (tmpx * 4096.0 / (float) X_RES + 0.5);
            cal.y[screen_pos] = (int) (tmpy * 4096.0 / (float) Y_RES + 0.5);

            if (screen_pos == 4) {
                ret = perform_calibration();
                if (ret) {
                    saveCalibrationResult();
                    Toast.makeText(this, "完成",Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    screen_pos = 0;
                    Log.w(TAG, "Calibration failed");
                }
            } else {
                screen_pos++;
                view. drawCalibrationCross(screen_pos);
            }
        }
        return true;
    }
}
