package com.sinohb.hardware.test.module.radio;

public interface RadioManagerable {

    int AUTO = 0;

    int FORWARD = 1;

    int BACKWARD = 2;

    int openRadio();

    int play(int freq);

    int search(int type);

    int closeRadio();

    int getCurrentFreq();

    boolean isOpen();

    void destroy();

    public interface RadioListener{

        void notifyRadioState(int state);

        void notifyRadioFreq(int freq);
    }
}
