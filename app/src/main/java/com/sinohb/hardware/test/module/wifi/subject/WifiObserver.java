package com.sinohb.hardware.test.module.wifi.subject;

public interface WifiObserver {
    void notifyOpenState(int openedState);
    void notifyConnectedState(int connectedState);
    void notifyScanFinished();
}
