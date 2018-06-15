package com.sinohb.hardware.test.module.wifi.subject;

import java.util.ArrayList;
import java.util.List;

public class WifiSubjectImpl implements IWifiSubject {
    private List<WifiObserver> observers = new ArrayList<>();
    protected WifiSubjectImpl(){}

    @Override
    public void attchObserver(WifiObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detachObserver(WifiObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyOpenState(int openedState) {
        for (WifiObserver observer:observers){
            observer.notifyOpenState(openedState);
        }
    }


    @Override
    public void notifyConnectedState(int connectedState) {
        for (WifiObserver observer:observers){
            observer.notifyConnectedState(connectedState);
        }
    }


    @Override
    public void notifyScanFinished() {
        for (WifiObserver observer:observers){
            observer.notifyScanFinished();
        }
    }
}
