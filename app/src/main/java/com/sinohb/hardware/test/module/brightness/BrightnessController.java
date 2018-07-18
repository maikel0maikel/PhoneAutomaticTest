package com.sinohb.hardware.test.module.brightness;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class BrightnessController extends BaseDisplayViewController implements BrightnessPresenter.Controller {
    private static final String TAG = "BrightnessController";
    private int mCurrentBrightness;
    private static final int BRIGHTNESS_L = 0;
    private static final int BRIGHTNESS_M = 128;
    private static final int BRIGHTNESS_H = 255;

    public BrightnessController(BaseDisplayViewView view) {
        super(view);
        init();
    }


    @Override
    protected void init() {
        mCurrentBrightness = getSystemBrightness(HardwareTestApplication.getContext().getContentResolver());
        task = new BrightnessTask(this);
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
//        if (mView != null) {
//            ((BrightnessPresenter.View) mView).changeLow();
//        }
        if (task!=null){
            ((BrightnessTask)task).setBrightness(BrightnessTask.STEP_L);
        }
    }

    @Override
    public void changeMedium() {
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), BRIGHTNESS_M);
//        if (mView != null) {
//            ((BrightnessPresenter.View) mView).changeMedium();
//        }
        if (task!=null){
            ((BrightnessTask)task).setBrightness(BrightnessTask.STEP_M);
        }
    }

    @Override
    public void changeHigh() {
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), BRIGHTNESS_H);
//        if (mView != null) {
//            ((BrightnessPresenter.View) mView).changeHigh();
//        }
        if (task!=null){
            ((BrightnessTask)task).setBrightness(BrightnessTask.STEP_H);
        }
    }

    @Override
    public void start() {
        super.start();
        mCurrentBrightness = getSystemBrightness(HardwareTestApplication.getContext().getContentResolver());
    }

    @Override
    public void complete() {
        LogTools.p(TAG, "任务完成恢复当前亮度");
        setBrightness(HardwareTestApplication.getContext().getContentResolver(), mCurrentBrightness);
        super.complete();
    }
}
