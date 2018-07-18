package com.sinohb.hardware.test.module.volume;

import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.hardware.test.module.media.IMediaplayer;
import com.sinohb.hardware.test.module.media.MediapayerTester;

public class VolumeController extends BaseExecuteController implements VolumePresenter.Controller {
    private VolumeAdjustManagerable managerable;
    private IMediaplayer mediaplayer;

    public VolumeController(BaseExecuteView view) {
        super(view);
    }


    @Override
    protected void init() {
        managerable = new VolumeManager();
        mediaplayer = new MediapayerTester();
        task = new VolumeTask(this);
    }

    @Override
    public void adjustLow() {
        managerable.adjustLow();
    }

    @Override
    public void adjustMedium() {
        managerable.adjustMedium();
    }

    @Override
    public void adjustHight() {
        managerable.adjustHight();
    }


    @Override
    public void complete() {
        managerable.reset();
        mediaplayer.destroy();
    }

}
