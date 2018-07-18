package com.sinohb.hardware.test.constant;


public class WifiConstants {
    private WifiConstants() {
    }

    public static final class OpenOrCloseState {
        public static final int STATE_OPENED = 1;
        public static final int STATE_OPENING = 2;
        public static final int STATE_CLOSED = 3;
        public static final int STATE_CLOSING = 4;
        public static final int STATE_UNKONWN = 5;

        private OpenOrCloseState() {
        }
    }
    public static final class ConnectState{
        public static final int STATE_CONNECTED = 1;
        public static final int STATE_CONNECT_FAILURE = 2;
        private ConnectState(){}
    }
    public static final class WifiConfigurate{
        public static final String SSID = "360免费WiFi-8B";
        public static final String SSID_REAL ="\"" + SSID + "\"";
        public static final int SSID_NOT_FOUND = -1000;
        public static final int SSID_CREATE_FAILURE = -1001;
        private WifiConfigurate(){}
    }

}
