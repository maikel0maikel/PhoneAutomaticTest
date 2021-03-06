package com.sinohb.hardware.test.module.volume;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

public class MediapayerTester implements IMediaplayer {
    private static final String TAG = "MediapayerTester";
    private MediaPlayer mMediaPlayer;

    MediapayerTester() {
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
    public int destroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
            return Constants.DEVICE_SUPPORTED;
        }
        LogTools.p(TAG, "mMediaPlayer 为null");
        return Constants.DEVICE_NOT_SUPPORT;
    }

}
