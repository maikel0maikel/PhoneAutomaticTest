package com.sinohb.hardware.test.utils;

import com.sinohb.hardware.test.HardwareTestApplication;

public class SP2PXUtils {
    private SP2PXUtils(){}
    public static int sp2px(float spValue) {
        final float fontScale = HardwareTestApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
