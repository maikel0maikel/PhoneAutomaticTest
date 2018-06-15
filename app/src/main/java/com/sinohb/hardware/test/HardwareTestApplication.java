package com.sinohb.hardware.test;

import com.sinohb.logger.SystemApplication;

public class HardwareTestApplication extends SystemApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        getLogger().setDebug(true);
    }
}
