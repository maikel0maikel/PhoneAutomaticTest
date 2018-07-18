package com.hbserialport.service; 

interface ISerialPortService
{

	void sendMsg(int serialNo, int msgId, String data);

}