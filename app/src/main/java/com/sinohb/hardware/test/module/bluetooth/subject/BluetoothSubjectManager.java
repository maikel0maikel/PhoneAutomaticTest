package com.sinohb.hardware.test.module.bluetooth.subject;


public class BluetoothSubjectManager {

    private IBluetoothSubject subject;
    private static BluetoothSubjectManager INSTANCE;
//
//    private static final class BluetoothSubjectInstance{
//        private static final BluetoothSubjectManager INSTANCE = new BluetoothSubjectManager();
//        private BluetoothSubjectInstance(){}
//    }

    private BluetoothSubjectManager() {
        subject = new BluetoothSubjectImpl();
    }

    public static final BluetoothSubjectManager getInstance() {
        if (INSTANCE == null) {
            synchronized (BluetoothSubjectManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothSubjectManager();
                }
            }
        }
        return INSTANCE;
    }

    public void attchBluetoothObserver(BluetoothObserver observer) {
        if (subject != null)
            subject.attchBluetoothObserver(observer);
    }

    public void detachBluetoothObserver(BluetoothObserver observer) {
        if (subject != null)
            subject.detachBluetoothObserver(observer);
    }

    public void notifyOpenState(int openedState) {
        if (subject != null)
            subject.notifyOpenState(openedState);
    }

    public void notifyBoundState(int boundState) {

        if (subject != null)
            subject.notifyBoundState(boundState);
    }

    public void notifyConnectedState(int connectedState) {
        if (subject != null)
            subject.notifyConnectedState(connectedState);
    }

    public void notifyDeviceFound(String name, String address) {
        if (subject != null)
            subject.notifyDeviceFound(name, address);
    }

    public void notifyScanStarted() {
        if (subject != null) subject.notifyScanStarted();
    }

    public void notifyScanFinished() {
        if (subject!=null)
        subject.notifyScanFinished();
    }

    public void destroy() {
        if (subject != null) {
            subject.destroy();
            subject = null;
        }
        INSTANCE = null;
    }

}
