package com.sinohb.hardware.test.module.wifi.subject;

public interface IWifiSubject {
    void attchObserver(WifiObserver observer);

    void detachObserver(WifiObserver observer);

    void notifyOpenState(int openedState);

    void notifyConnectedState(int connectedState);

    void notifyScanFinished();
}
