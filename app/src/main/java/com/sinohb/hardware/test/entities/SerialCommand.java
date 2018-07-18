package com.sinohb.hardware.test.entities;

import com.sinohb.hardware.test.module.frc.RFCSendListener;

public class SerialCommand {
    private int serialNo;
    private int msgId;
    private String data;
    private RFCSendListener sendListener;
    public SerialCommand(){

    }
    public SerialCommand(int serialNo, int msgId, String data){
        this.serialNo = serialNo;
        this.msgId = msgId;
        this.data = data;
    }
    public SerialCommand(int serialNo, int msgId, String data,RFCSendListener l){
        this.serialNo = serialNo;
        this.msgId = msgId;
        this.data = data;
        this.sendListener = l;
    }
    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public RFCSendListener getSendListener() {
        return sendListener;
    }

    public void setSendListener(RFCSendListener sendListener) {
        this.sendListener = sendListener;
    }
}
