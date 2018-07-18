package com.sinohb.hardware.test.module.audoex;



import com.marsir.audio.AudioExManager;
import com.marsir.common.CommonManager;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.entities.AmplifierEntity;
import com.sinohb.hardware.test.module.media.IMediaplayer;
import com.sinohb.hardware.test.module.media.MediapayerTester;
import com.sinohb.hardware.test.module.volume.VolumeAdjustManagerable;
import com.sinohb.hardware.test.module.volume.VolumeManager;

public class EffectTestManager implements EffectManagerable {
    private static final String TAG = "EffectTestManager";
    private AudioExManager audioExManager;
    private static final int CLOSE_EFFECT = 0;
    private static final int OPPEN_POP_EFFECT = 4;
    private IMediaplayer mediaplayer;
    private CommonManager mCommonManager;
    public EffectTestManager() {
        audioExManager = (AudioExManager) HardwareTestApplication.getContext().getSystemService("audioex");
        VolumeAdjustManagerable managerable = new VolumeManager();
        managerable.adjustMedium();
        mediaplayer = new MediapayerTester();
        mCommonManager = (CommonManager) HardwareTestApplication.getContext().getSystemService("common");
    }


    @Override
    public int playNormal() {
        if (isEnable()) {
            setEffect(CLOSE_EFFECT);
            if (!mediaplayer.isPlaying()) {
                mediaplayer.play();
            }
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }


    @Override
    public int playEffect() {
        if (isEnable()) {
            setEffect(OPPEN_POP_EFFECT);
            if (!mediaplayer.isPlaying()) {
                mediaplayer.play();
            }
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int closeEffect() {
        if (isEnable()) {
            destroy();
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int playAmplifier(AmplifierEntity entity) {
        if (entity == null){
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            if (!mediaplayer.isPlaying()) {
                mediaplayer.play();
            }
            audioExManager.setGold(entity.w, entity.h);
            if (mCommonManager!=null){
                mCommonManager.saveSystemSetting();
            }
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public void destroy() {
        if (mediaplayer != null) {
            mediaplayer.destroy();
        }
        setEffect(CLOSE_EFFECT);
    }

    private boolean isEnable() {
        return audioExManager != null;
    }

    private void setEffect(int effect) {
        if (isEnable()) {
            audioExManager.setEq(effect, 0, 0, 0, 0, 0, 0);
        }
    }


}
