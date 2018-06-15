package com.sinohb.hardware.test.module.wifi.subject;

public class WifiSubjectManager {

    private IWifiSubject subject;

    private static final class WifiSubjectInstance{
        private static final WifiSubjectManager INSTANCE = new WifiSubjectManager();
        private WifiSubjectInstance(){}
    }

    private WifiSubjectManager(){
        subject = new WifiSubjectImpl();
    }
    public static final WifiSubjectManager getInstance(){
        return WifiSubjectInstance.INSTANCE;
    }
    public void attchObserver(WifiObserver observer) {
        subject.attchObserver(observer);
    }

    public void detachObserver(WifiObserver observer) {
        subject.detachObserver(observer);
    }

    public void notifyConnectedState(int connectedState) {
        subject.notifyConnectedState(connectedState);
    }

    public void notifyOpenOrCloseState(int state){
        subject.notifyOpenState(state);
    }

    public void notifyScanFinished() {
        subject.notifyScanFinished();
    }

}
