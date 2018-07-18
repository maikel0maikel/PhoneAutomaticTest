package com.sinohb.hardware.test.module.frc;

public interface RFCSendListener {
    //    void onSuccess(RFCSendListener l,int id);
//    void onFailure(RFCSendListener l,int id);
    void onSuccess(int NO, int id);

    void onFailure(int NO, int id);
}
