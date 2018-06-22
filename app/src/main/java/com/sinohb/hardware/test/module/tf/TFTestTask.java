package com.sinohb.hardware.test.module.tf;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.module.usb.USBTestTask;

public class TFTestTask extends USBTestTask{
    public TFTestTask(String path, BasePresenter presenter) {
        super(path, presenter);
    }
}
