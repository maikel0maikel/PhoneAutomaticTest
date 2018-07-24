package com.sinohb.hardware.test.module.radio;


import com.marsir.radio.RadioExListener;
import com.marsir.radio.RadioExManager;
import com.marsir.radio.RadioExParam;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

public class RadioTestManager implements RadioManagerable, RadioExListener {
    private static final String TAG = "RadioTestManager";
    private RadioExManager radioExManager;
    private static final int DEFAULT_FREQ = 8750;
    private RadioListener listener;
    RadioTestManager(RadioListener listener) {
        this.listener = listener;
        radioExManager = (RadioExManager) HardwareTestApplication.getContext().getSystemService("radioex");
        if (isEnable()) {
            radioExManager.addRadioExListener(this);
            RadioExParam radioExParam = radioExManager.getParameters();
            radioExParam.fm_seek_step = 10;
            radioExManager.setParam(radioExParam);
        }
    }


    @Override
    public int openRadio() {
        if (isEnable()) {
            if (isOpen()) {
                return Constants.DEVICE_RESET;
            }
            radioExManager.powerOn(RadioExManager.BAND_FM, DEFAULT_FREQ);
            LogTools.p(TAG, "openRadio radioExManager.getState() :" + radioExManager.getState());
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int play(int freq) {
        if (isEnable()) {
            if (!isOpen()) {
                return Constants.DEVICE_STATE_ERROR;
            }
            radioExManager.setFreq(freq);
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int search() {
        if (isEnable()) {
            if (!isOpen()) {
                return Constants.DEVICE_STATE_ERROR;
            }
            radioExManager.setSearch(RadioExManager.SEARCH_AUTO);
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public int closeRadio() {
        if (isEnable()) {
            if (isOpen()) {
                radioExManager.powerOff();
                LogTools.p(TAG, "closeRadio radioExManager.getState() :" + radioExManager.getState());
                return Constants.DEVICE_SUPPORTED;
            }else {
                return Constants.DEVICE_NORMAL;
            }
        }
        return Constants.DEVICE_NOT_SUPPORT;
    }

    @Override
    public boolean isOpen() {
        return radioExManager.getState() == RadioExManager.STATE_ON;
    }

    @Override
    public void destroy() {
        if (isEnable()){
            radioExManager.removeRadioExListener(this);
        }
    }

    private boolean isEnable() {
        return radioExManager != null;
    }

    @Override
    public void onStateChanged(int i, int i1) {
        int state = radioExManager.getState();
        LogTools.p(TAG, "onStateChanged " + i + ",i1:" + i1 + ",state:" + state);
        if (listener!=null){
            listener.notifyRadioState(state);
        }
    }

    @Override
    public void onBandChanged(int i) {

    }

    @Override
    public void onFreqChanged(int i) {
        //LogTools.p(TAG, "onFreqChanged i=" + i);
    }

    @Override
    public void onSeekFreqEvent(int i, int i1) {
        LogTools.p(TAG, "onSeekFreqEvent i=" + i );
        if (listener!=null){
            listener.notifyRadioFreq(i);
        }
    }


    @Override
    public void onSensitivityChanged(int i) {

    }



}
