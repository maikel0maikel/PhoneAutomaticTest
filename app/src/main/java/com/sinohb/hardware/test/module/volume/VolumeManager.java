package com.sinohb.hardware.test.module.volume;

import android.content.Context;
import android.media.AudioManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

public class VolumeManager implements VolumeAdjustManagerable {
    private static final String TAG = "VolumeManager";
    private AudioManager audioManager;
    private int mCurrentVolumen;
    private int maxVolumen;

    public VolumeManager() {
        audioManager = (AudioManager) HardwareTestApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        if (isEnable()) {
            mCurrentVolumen = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVolumen = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } else {
            LogTools.p(TAG, "audioManager is null");
        }
    }

    @Override
    public int adjustLow() {
        if (isEnable()) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 2, 0);
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "audioManager is null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int adjustMedium() {
        if (isEnable()) {
            int volumenM = maxVolumen / 2 + 1;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumenM, 0);
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "audioManager is null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int adjustHight() {
        if (isEnable()) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolumen, 0);
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "audioManager is null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int reset() {
        if (isEnable()) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolumen, 0);
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "audioManager is null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

    private boolean isEnable() {
        return audioManager != null;
    }
}
