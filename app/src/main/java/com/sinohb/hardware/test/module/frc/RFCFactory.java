package com.sinohb.hardware.test.module.frc;

import com.sinohb.hardware.test.entities.SerialCommand;

public class RFCFactory {
    private IRFCServer server;

    private RFCFactory(){server = new RFCServerImpl();}

    public static RFCFactory getInstance(){

        return INSTANCE.INSTANCE;
    }

    private static final class INSTANCE{
        private static RFCFactory INSTANCE = new RFCFactory();
    }

    public void connectService(){
        server.connectService();
    }

    public void sendMsg(SerialCommand c){
        server.sendMsg(c);
    }

    public  void disconnectService(){
        server.disconnectService();
    }

    public  boolean isConnected(){
        return server.isConnected();
    }
}
