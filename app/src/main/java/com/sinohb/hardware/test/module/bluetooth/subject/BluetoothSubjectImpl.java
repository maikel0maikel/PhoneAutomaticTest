package com.sinohb.hardware.test.module.bluetooth.subject;

import java.util.ArrayList;
import java.util.List;

public class BluetoothSubjectImpl implements IBluetoothSubject{
    private List<BluetoothObserver> observers = new ArrayList<>();
    protected BluetoothSubjectImpl(){}
    @Override
    public void attchBluetoothObserver(BluetoothObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detachBluetoothObserver(BluetoothObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyOpenState(int openedState) {
        for (BluetoothObserver observer:observers){
            observer.notifyOpenState(openedState);
        }
    }

    @Override
    public void notifyBoundState(int boundState) {
        for (BluetoothObserver observer:observers){
            observer.notifyBoundState(boundState);
        }
    }

    @Override
    public void notifyConnectedState(int connectedState) {
        for (BluetoothObserver observer:observers){
            observer.notifyConnectedState(connectedState);
        }
    }

    @Override
    public void notifyDeviceFound(String name, String address) {
        for (BluetoothObserver observer:observers){
            observer.notifyDeviceFound(name,address);
        }
    }

    @Override
    public void notifyScanStarted() {
        for (BluetoothObserver observer:observers){
            observer.notifyScanStarted();
        }
    }

    @Override
    public void notifyScanFinished() {
        for (BluetoothObserver observer:observers){
            observer.notifyScanFinished();
        }
    }
}
