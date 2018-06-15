package com.sinohb.hardware.test.module.bluetooth.subject;

public interface IBluetoothSubject {
    void attchBluetoothObserver(BluetoothObserver observer);
    void detachBluetoothObserver(BluetoothObserver observer);
    void notifyOpenState(int openedState);
    void notifyBoundState(int boundState);
    void notifyConnectedState(int connectedState);
    void notifyDeviceFound(String name,String address);
    void notifyScanStarted();
    void notifyScanFinished();
}
