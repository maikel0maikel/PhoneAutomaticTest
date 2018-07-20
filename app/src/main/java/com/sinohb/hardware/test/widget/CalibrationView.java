package com.sinohb.hardware.test.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.sinohb.hardware.test.entities.Calibration;
import com.sinohb.logger.LogTools;

public class CalibrationView extends View implements Runnable{
    private static final String TAG = "CalibrationView";
    private Canvas cv;
    private Paint paint;
    private Bitmap bmp;
    private int screen_pos;
    private Context mContext;
    private int X_RES;
    private int Y_RES;
    private Paint mRipplePaint;
    private Paint mCirclePaint;
//    private Path mArcPath;
    private int mMaxRadius = 30;
    private int mInterval = 20;
    private int count = 0;
    private Paint touchPaint;
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
        paint.setFilterBitmap(true);
        //paint.setStyle(Paint.Style.STROKE);
        bmp = Bitmap.createBitmap(X_RES, Y_RES, Bitmap.Config.ARGB_8888);
        cv = new Canvas(bmp);
        screen_pos = 0;
        cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));

        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Color.GRAY);
        mRipplePaint.setStrokeWidth(2);

//        mCirclePaint = new Paint();
//        mCirclePaint.setAntiAlias(true);
//        mCirclePaint.setStyle(Paint.Style.FILL);
//        mCirclePaint.setColor(Color.GRAY);

       // mArcPath = new Path();
        touchPaint = new Paint();
        touchPaint.setDither(true);
        touchPaint.setAntiAlias(true);
        touchPaint.setStrokeWidth(3);
        touchPaint.setColor(Color.BLUE);
        touchPaint.setFilterBitmap(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
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
        p.setColor(Color.WHITE);
        canvas.drawText("屏幕校准",
                (X_RES / 2) - (txt_welcome_size / 2)* txt_welcome_count/** - txt_welcome_size / 2**/,
                Y_RES / 2 - txt_welcome_size - 30,
                p);

        //"请按住光标中央以校准"
        p.setFakeBoldText(false);
        p.setColor(Color.WHITE);
        p.setTextSize(txt_content_size);
        //参数2（X_RES / 2 - (txt_content_size / 2 * txt_content1_count)）：当前屏幕宽度的一半减去字数
        canvas.drawText("请按住十字光标以校准",
                X_RES / 2 - (txt_content_size / 2 * txt_content1_count),
                Y_RES / 2 + 150,
                p);

        //"你的屏幕"
        p.setColor(Color.WHITE);
        p.setTextSize(txt_content_size);
        canvas.drawText("你的屏幕",
                X_RES / 2 - txt_content_size / 2 * txt_content2_count,
                Y_RES / 2 + 200,
                p);

        //线,渐变效果!!!
//        Shader shader = new LinearGradient((X_RES / 2) - (txt_welcome_size / 2) - txt_welcome_size * 2,
//                (Y_RES / 2) - txt_welcome_size,
//                X_RES / 2,
//                (Y_RES / 2) - txt_welcome_size,
//                new int[]{Color.WHITE, Color.GREEN},
//                null,
//                Shader.TileMode.MIRROR);
//        p.setShader(shader);

//        canvas.drawLine((X_RES / 2) - (txt_welcome_size / 2) - txt_welcome_size * 2,
//                (Y_RES / 2) - txt_welcome_size,
//                (X_RES / 2) + (txt_welcome_size / 2) + txt_welcome_size * 2,
//                (Y_RES / 2) - txt_welcome_size,
//                p);

//        if (isTouch){
//            drawRippy(canvas);
//        }
    }

    private void drawRippy(Canvas canvas) {
        //获取加号图片大小
        final int pw = 0;
        final int ph = 0;
        final float px = x ;
        final float py = y ;

        final int rw = pw / 2;
        final int rh = ph / 2;

        //清空所有已经画过的path至原始状态
        //mArcPath.reset();
        final int fw = 0;
        final int fh =0;
        final float fx =0;
        final float fy = 0;
        //起始轮廓点移至x，y坐标点,即加号图片正下方再往下20位置
        // mArcPath.moveTo(px, py + rh + mInterval);
        //设置二次贝塞尔，实现平滑曲线，前两个参数为操作点坐标，后两个参数为结束点坐标
        // mArcPath.quadTo(px, fy - mInterval, fx + fw * 0.618f, fy - mInterval);
        //0~255，数值越小越透明
        mRipplePaint.setAlpha(255);
        // cv.drawPath(mArcPath, mRipplePaint);
        //绘制半径为6的实心圆点
        //cv.drawCircle(px, py + rh, 6, mCirclePaint);

        //保存画布当前的状态
        int save = canvas.save();
        for (int step = count; step <= mMaxRadius; step += mInterval) {
            //step越大越靠外就越透明
            mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
            canvas.drawCircle(px, py, (float) (rw + step), mRipplePaint);

        }
        canvas.restoreToCount(save);
        postDelayed(this, 20);
    }

    private int currentPos ;
    private Calibration currentCal;
    public boolean drawCalibrationCross(int pos, Calibration cal) {
        this.currentCal = cal;
        this.currentPos = pos;
        LogTools.p(TAG, "pos:" + pos);
        drawCalibration(paint);
        return true;
    }

    private void drawCalibration(Paint paint){
        if(currentCal == null){
            return;
        }
        removeCallbacks(this);
        isTouch = false;
        cv.drawColor(Color.BLACK);

        // draw X line
        cv.drawLine(currentCal.xfb[currentPos] - 20, currentCal.yfb[currentPos],
                currentCal.xfb[currentPos] - 4, currentCal.yfb[currentPos], paint);
        cv.drawLine(currentCal.xfb[currentPos] + 4, currentCal.yfb[currentPos],
                currentCal.xfb[currentPos] + 20, currentCal.yfb[currentPos], paint);

        // draw Y line
        cv.drawLine(currentCal.xfb[currentPos], currentCal.yfb[currentPos] - 20,
                currentCal.xfb[currentPos], currentCal.yfb[currentPos] - 4, paint);
        cv.drawLine(currentCal.xfb[currentPos], currentCal.yfb[currentPos] + 4,
                currentCal.xfb[currentPos], currentCal.yfb[currentPos] + 20, paint);

        x= currentCal.xfb[currentPos];
        y = currentCal.yfb[currentPos];
        invalidate();
    }

//    /**
//     * view大小变化时系统调用
//     * @param w
//     * @param h
//     * @param oldw
//     * @param oldh
//     */
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (bmp != null) {
//            bmp.recycle();
//            bmp = null;
//        }
//    }
    @Override
    public void run() {
        //把run对象的引用从队列里拿出来，这样，他就不会执行了，但 run 没有销毁
        removeCallbacks(this);
        count += 2;
        count %= mInterval;
        invalidate();//重绘
    }

    /**
     * 销毁view时调用，收尾工作
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bmp != null) {
            bmp.recycle();
            bmp = null;
        }
    }
    private boolean isTouch = false;
    private int x,y;
    public void onTouch(MotionEvent event){
        isTouch = true;
        drawRippy(cv);
        if (event.getAction() == MotionEvent.ACTION_UP){
            removeCallbacks(this);
            isTouch = false;
            drawCalibration(paint);
        }else if (event.getAction() == MotionEvent.ACTION_DOWN){
            removeCallbacks(this);
            drawCalibration(touchPaint);
        }
    }
}
