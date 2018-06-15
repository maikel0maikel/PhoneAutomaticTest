package com.sinohb.hardware.test.entities;

public class BluetoothDeviceEntity {
    private String mDeviceName;
    private String mDeviceAddress;
    private int mRssi;

    public BluetoothDeviceEntity() {

    }

    public BluetoothDeviceEntity(String name, String address, int rssi) {
        this.mDeviceName = name;
        this.mDeviceAddress = address;
        this.mRssi = rssi;
    }

    public String getmDeviceName() {
        return mDeviceName;
    }

    public void setmDeviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
    }

    public String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public void setmDeviceAddress(String mDeviceAddress) {
        this.mDeviceAddress = mDeviceAddress;
    }

    public int getmRssi() {
        return mRssi;
    }

    public void setmRssi(int mRssi) {
        this.mRssi = mRssi;
    }
}
