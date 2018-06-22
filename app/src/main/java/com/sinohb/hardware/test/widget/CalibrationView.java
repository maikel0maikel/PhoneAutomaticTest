package com.sinohb.hardware.test.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.sinohb.hardware.test.entities.Calibration;
import com.sinohb.logger.LogTools;

public class CalibrationView extends View {
    private static final String TAG = "CalibrationView";
    private Canvas cv;
    private Paint paint;
    private Bitmap bmp;
    private int screen_pos;
    private Context mContext;
    private int X_RES;
    private int Y_RES;

    public CalibrationView(Context c) {
        super(c);
        // set full screen and no title
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        X_RES = displayMetrics.widthPixels;
        Y_RES = displayMetrics.heightPixels;
        LogTools.p(TAG, "屏幕宽：" + X_RES + ",屏幕高：" + Y_RES);
        mContext = c;
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        bmp = Bitmap.createBitmap(X_RES, Y_RES, Bitmap.Config.ARGB_8888);
        cv = new Canvas(bmp);
        screen_pos = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, 0, 0, null);

        //“屏幕校准”字体大小
        float txt_welcome_size = 60;
        //“屏幕校准”字数
        float txt_welcome_count = 4;
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
        p.setColor(Color.BLUE);
        canvas.drawText("屏幕校准",
                (X_RES / 2) - (txt_welcome_size / 2)* txt_welcome_count/** - txt_welcome_size / 2**/,
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

    public boolean drawCalibrationCross(int pos, Calibration cal) {
        LogTools.p(TAG, "pos:" + pos);

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
