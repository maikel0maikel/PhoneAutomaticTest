package com.sinohb.hardware.test.module.frc;


import com.sinohb.hardware.test.entities.SerialCommand;

public interface IRFCServer {

    void connectService();

    void sendMsg(SerialCommand command);

    void disconnectService();

    boolean isConnected();

    void registSendListener(RFCSendListener listener);

    void removeSendListener(RFCSendListener listener);
}
