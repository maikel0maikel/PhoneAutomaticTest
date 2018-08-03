package com.sinohb.hardware.test.module.media;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

public class MediapayerTester implements IMediaplayer {
    private static final String TAG = "MediapayerTester";
    private MediaPlayer mMediaPlayer;

    public MediapayerTester() {
        initPlayer();
    }

    private void initPlayer() {
        mMediaPlayer = MediaPlayer.create(HardwareTestApplication.getContext(), R.raw.crazydream);
        if (mMediaPlayer != null) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    public int play() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "mMediaPlayer 为null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int stop() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "mMediaPlayer 为null");
        return Constants.DEVICE_NOT_SUPPORT;
    }


    @Override
    public int destroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
            LogTools.p(TAG,"mMediaPlayer destroy");
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "mMediaPlayer 为null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

}
