package com.sinohb.hardware.test.module.wifi.subject;

public class WifiSubjectManager {

    private IWifiSubject subject;
    private static WifiSubjectManager INSTANCE;


    private WifiSubjectManager() {
        subject = new WifiSubjectImpl();
    }

    public static final WifiSubjectManager getInstance() {
        if (INSTANCE == null) {
            synchronized (WifiSubjectManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WifiSubjectManager();
                }
            }
        }
        return INSTANCE;
    }

    public void attchObserver(WifiObserver observer) {
        if (subject != null)
            subject.attchObserver(observer);
    }

    public void detachObserver(WifiObserver observer) {
        if (subject != null)
            subject.detachObserver(observer);
    }

    public void notifyConnectedState(int connectedState) {
        if (subject != null)
            subject.notifyConnectedState(connectedState);
    }

    public void notifyOpenOrCloseState(int state) {
        if (subject != null)
            subject.notifyOpenState(state);
    }

    public void notifyScanFinished() {
        if (subject != null)
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
