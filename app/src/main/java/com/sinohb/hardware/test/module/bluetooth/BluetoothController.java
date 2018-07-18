package com.sinohb.hardware.test.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.hardware.test.module.bluetooth.receiver.BluetoothEventReceiver;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothObserver;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.hardware.test.task.BaseTestTask;

public class BluetoothController extends BaseExecuteController implements BluetoothPresenter.Controller, BluetoothObserver {

    private BluetoothEventReceiver bluetoothReceiver;
    private BluetoothManagerable mBluetoothManager;

    public BluetoothController(BaseExecuteView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        registBluetoothReceiver();
        task = new BluetoothTestTask(this);
        mBluetoothManager = new BLEManager();
        BluetoothSubjectManager.getInstance().attchBluetoothObserver(this);
    }

    @Override
    public int openBt() {
        return mBluetoothManager.open();

    }

    @Override
    public int closeBt() {
        return mBluetoothManager.close();
    }

    @Override
    public int startDiscovery() {
        return mBluetoothManager.startDiscovery();
    }

    @Override
    public int stopDisvcovery() {
        return mBluetoothManager.stopDiscovery();
    }

    @Override
    public void bound() {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void sendMessage(byte[] message) {

    }

    @Override
    public void sendMessage(String message) {

    }


    @Override
    public void destroy() {
        unregistBluetoothReceiver();
        super.destroy();
    }

    private void registBluetoothReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        bluetoothReceiver = new BluetoothEventReceiver();
        HardwareTestApplication.getContext().registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void unregistBluetoothReceiver() {
        if (bluetoothReceiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
    }

    @Override
    public void notifyOpenState(int openedState) {
        ((BluetoothPresenter.View) mView).notifyOpenOrCloseState(openedState);
        if (task != null) {
            ((BluetoothTestTask) task).notifyBtOpenState(openedState);
        }
    }

    @Override
    public void notifyBoundState(int boundState) {

    }

    @Override
    public void notifyConnectedState(int connectedState) {

    }

    @Override
    public void notifyDeviceFound(String name, String address) {

    }

    @Override
    public void notifyScanStarted() {
        if (task != null) {
            ((BluetoothTestTask) task).notifyBtStartDiscovery();
        }
    }

    @Override
    public void notifyScanFinished() {
        if (task != null) {
            ((BluetoothTestTask) task).notifyBtStopDiscovery();
        }
    }

//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
