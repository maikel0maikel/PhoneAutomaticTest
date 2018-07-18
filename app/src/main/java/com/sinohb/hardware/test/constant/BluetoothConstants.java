package com.sinohb.hardware.test.constant;

public class BluetoothConstants {
    public static final String BTEAR_SERVICE = "btear";

    private BluetoothConstants() {
    }


    public static class OpenState {
        public static final int STATE_TURNING_ON = 1;
        public static final int STATE_TURNED_ON = 2;
        public static final int STATE_TURNING_OFF = 3;
        public static final int STATE_TURNED_OFF = 4;

        private OpenState() {
        }
    }

    public static class BoundState {
        public static final int STATE_BOND_BONDING = 5;
        public static final int STATE_BOND_BONDED = 6;
        public static final int STATE_BOND_NONE = 7;

        private BoundState() {
        }
    }

    public static class ConnectState {
        public static final int STATE_CONNECTING = 8;
        public static final int STATE_CONNECTED = 9;
        public static final int STATE_DISCONNECTING = 10;
        public static final int STATE_DISCONNECTED = 11;

        private ConnectState() {
        }
    }
}
