package com.sinohb.hardware.test.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.bluetooth.receiver.BluetoothEventReceiver;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothObserver;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.logger.LogTools;

public class BluetoothController extends BaseDisplayViewController implements BluetoothPresenter.Controller, BluetoothObserver {

    private BluetoothEventReceiver bluetoothReceiver;
    private BluetoothManagerable mBluetoothManager;

    public BluetoothController(BaseDisplayViewView view) {
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
        return mBluetoothManager == null ? Constants.DEVICE_NOT_SUPPORT : mBluetoothManager.open();

    }

    @Override
    public int closeBt() {
        return mBluetoothManager == null ? Constants.DEVICE_NOT_SUPPORT : mBluetoothManager.close();
    }

    @Override
    public int startDiscovery() {
        return mBluetoothManager == null ? Constants.DEVICE_NOT_SUPPORT : mBluetoothManager.startDiscovery();
    }

    @Override
    public int stopDisvcovery() {
        return mBluetoothManager == null ? Constants.DEVICE_NOT_SUPPORT : mBluetoothManager.stopDiscovery();
    }

    @Override
    public void bound() {

    }

    @Override
    public int connect(String mac) {
        return mBluetoothManager == null ? Constants.DEVICE_NOT_SUPPORT : mBluetoothManager.connect(mac);
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
    public void notifyConnected() {
        if (mView!=null){
            ((BaseDisplayViewView)mView).displayView();
        }
    }

    @Override
    public void displayView() {

    }

    private void registBluetoothReceiver() {
        if (bluetoothReceiver == null) {
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

    }

    private void unregistBluetoothReceiver() {
        if (bluetoothReceiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(bluetoothReceiver);
            bluetoothReceiver = null;
        }
    }

    @Override
    public void notifyOpenState(int openedState) {
        if (mView != null)
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
        LogTools.p(TAG, "notifyConnectedState connectedState=" + connectedState);
        if (task != null) {
            ((BluetoothTestTask) task).notifyConnectState(connectedState);
        }
    }

    @Override
    public void notifyDeviceFound(String name, String address) {
        LogTools.p(TAG,"notifyDeviceFound address:"+address);
        if (task != null) {
            ((BluetoothTestTask) task).notifyDeviceFound(address);
        }
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

    @Override
    public void destroy() {
        super.destroy();
        unregistBluetoothReceiver();
        BluetoothSubjectManager.getInstance().detachBluetoothObserver(this);
        BluetoothSubjectManager.getInstance().destroy();
    }

//    @Override
//    public BaseTestTask getTask() {
//        return task;
//    }
}
