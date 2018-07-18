package com.sinohb.hardware.test.module.radio;

public interface RadioManagerable {


    int openRadio();

    int play(int freq);

    int search();

    int closeRadio();

    boolean isOpen();

    void destroy();

    public interface RadioListener{

        void notifyRadioState(int state);

        void notifyRadioFreq(int freq);
    }
}
