package com.sinohb.hardware.test.module.brightness;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.util.concurrent.FutureTask;

public class BrightnessController implements BrightnessPresenter.Controller {
    private static final String TAG = "BrightnessController";
    private BrightnessPresenter.View mView;
    private BrightnessTask mTask;
    private int mCurrentBrightness;
    private static final int BRIGHTNESS_L = 0;
    private static final int BRIGHTNESS_M = 128;
    private static final int BRIGHTNESS_H = 255;

    public BrightnessController(BrightnessPresenter.View view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    /**
     * 保存亮度设置状态
     *
     * @param resolver
     * @param brightness
     */
    private void setBrightness(ContentResolver resolver, int brightness) {
        Uri uri = android.provider.Settings.System
                .getUriFor("screen_brightness");

        android.provider.Settings.System.putInt(resolver, "screen_brightness",
                brightness);
        // resolver.registerContentObserver(uri, true, myContentObserver);
        resolver.notifyChange(uri, null);
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    private int getSystemBrightness(ContentResolver resolver) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    @Override
    public void changeLow() {
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), BRIGHTNESS_L);
    }

    @Override
    public void changeMedium() {
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), BRIGHTNESS_M);
    }

    @Override
    public void changeHigh() {
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), BRIGHTNESS_H);
    }

    @Override
    public void start() {
        mCurrentBrightness = getSystemBrightness(HardwareTestApplication.getContext().getContentResolver());
        LogTools.p(TAG, "当前亮度" + mCurrentBrightness);
        mTask = new BrightnessTask(this);
        FutureTask futureTask = new FutureTask(mTask);
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
        LogTools.p(TAG, "任务完成恢复当前亮度");
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), mCurrentBrightness);
    }

    @Override
    public void destroy() {

    }
}
