package com.sinohb.hardware.test.module.volume;

import com.sinohb.hardware.test.task.ThreadPool;

import java.util.concurrent.FutureTask;

public class VolumeController implements VolumePresenter.Controller{
    private VolumePresenter.View mView;
    private VolumeAdjustManagerable managerable;
    private IMediaplayer mediaplayer;
    public VolumeController(VolumePresenter.View view){
        this.mView = view;
        managerable = new VolumeManager();
        mediaplayer = new MediapayerTester();
        this.mView.setPresenter(this);
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
    public void start() {
        mediaplayer.play();
        VolumeTask volumeTask = new VolumeTask(this);
        FutureTask futureTask = new FutureTask(volumeTask);
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
        managerable.reset();
        mediaplayer.destroy();
    }

    @Override
    public void destroy() {

    }
}
