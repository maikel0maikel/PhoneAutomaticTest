package com.sinohb.hardware.test.module.bluetooth.subject;

public class BluetoothSubjectManager {

    private IBluetoothSubject subject;

    private static final class BluetoothSubjectInstance{
        private static final BluetoothSubjectManager INSTANCE = new BluetoothSubjectManager();
        private BluetoothSubjectInstance(){}
    }

    private BluetoothSubjectManager(){
        subject = new BluetoothSubjectImpl();
    }
    public static final BluetoothSubjectManager getInstance(){
        return BluetoothSubjectInstance.INSTANCE;
    }
    public void attchBluetoothObserver(BluetoothObserver observer) {
        subject.attchBluetoothObserver(observer);
    }

    public void detachBluetoothObserver(BluetoothObserver observer) {
        subject.detachBluetoothObserver(observer);
    }

    public void notifyOpenState(int openedState) {
        subject.notifyOpenState(openedState);
    }

    public void notifyBoundState(int boundState) {
        subject.notifyBoundState(boundState);
    }

    public void notifyConnectedState(int connectedState) {
        subject.notifyConnectedState(connectedState);
    }

    public void notifyDeviceFound(String name, String address) {
        subject.notifyDeviceFound(name,address);
    }

    public void notifyScanStarted() {
        subject.notifyScanStarted();
    }

    public void notifyScanFinished() {
        subject.notifyScanFinished();
    }

}
