package com.sinohb.hardware.test.module.bluetooth;

public interface BluetoothManagerable {

    int open();

    int close();

    int startDiscovery();

    int stopDiscovery();

    int connect(String btAddress);
}
