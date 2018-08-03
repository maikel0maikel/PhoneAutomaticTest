package com.sinohb.hardware.test.entities;

public class ConfigEntity {
    public Wifi wifi;
    public Bluetooth bluetooth;
    public Radio radio;
    public class Wifi{
        public String ssid;
        public String pwd;
    }

    public class Bluetooth{
        public String mac;
    }
    public class Radio{
        public float frequency;
    }

}
