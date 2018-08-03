package com.sinohb.hardware.test.module.frc;

import com.sinohb.hardware.test.entities.SerialCommand;

public class RFCFactory {
    private IRFCServer server;
    private static RFCFactory INSTANCE;

    private RFCFactory() {
        server = new RFCServerImpl();
    }

    public static RFCFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (RFCFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RFCFactory();
                }
            }
        }
        return INSTANCE;
    }


    public void connectService() {
        server.connectService();
    }

    public void sendMsg(SerialCommand c) {
        server.sendMsg(c);
    }

    public void disconnectService() {
        server.disconnectService();
        INSTANCE = null;
    }

    public boolean isConnected() {
        return server.isConnected();
    }
}
