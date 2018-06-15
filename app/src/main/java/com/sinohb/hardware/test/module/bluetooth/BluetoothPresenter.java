package com.sinohb.hardware.test.module.bluetooth;

import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;

public interface BluetoothPresenter {

    public interface View extends BaseView<Controller> {
        void notifyOpenOrCloseState(int btOnOrOffState);

        void notifyBoundState(int btBoundState);

        void notifyConnectState(int btConnectState);
    }

    public interface Controller extends BasePresenter {
        int openBt();

        int closeBt();

        int startDiscovery();

        int stopDisvcovery();

        void bound();

        void connect();

        void disconnect();

        void sendMessage(byte[] message);

        void sendMessage(String message);
    }

}
