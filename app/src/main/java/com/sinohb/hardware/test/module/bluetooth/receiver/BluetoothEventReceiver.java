package com.sinohb.hardware.test.module.bluetooth.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.logger.LogTools;

/**
 * 蓝牙广播监听如下动作
 * 【打开、关闭、正在关闭、正在开启】
 * 【绑定、绑定中、未绑定】
 * 【连接中、已连接、断开中、正在断开】
 * 【开始扫描、扫描完成】
 * 【找到设备】
 */
public class BluetoothEventReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothEventReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogTools.p(TAG, "action=" + intent.getAction());
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON://bluetooth is opening
                    BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNING_ON);
                    break;
                case BluetoothAdapter.STATE_ON://bluetooth is opened
                    BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_ON);
                    break;
                case BluetoothAdapter.STATE_CONNECTING://bluetooth is connecting
                    BluetoothSubjectManager.getInstance().notifyConnectedState(BluetoothConstants.ConnectState.STATE_CONNECTING);
                    break;
                case BluetoothAdapter.STATE_CONNECTED://bluetooth is connected
                    BluetoothSubjectManager.getInstance().notifyConnectedState(BluetoothConstants.ConnectState.STATE_CONNECTED);
                    break;
                case BluetoothAdapter.STATE_DISCONNECTING://bluetooth is disconnecting
                    BluetoothSubjectManager.getInstance().notifyConnectedState(BluetoothConstants.ConnectState.STATE_DISCONNECTING);
                    break;
                case BluetoothAdapter.STATE_DISCONNECTED:////bluetooth is disconnected
                    BluetoothSubjectManager.getInstance().notifyConnectedState(BluetoothConstants.ConnectState.STATE_DISCONNECTED);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF://bluetooth is closing
                    BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNING_OFF);
                    break;
                case BluetoothAdapter.STATE_OFF://bluetooth is closed
                    BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
                    break;
            }
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {//found bluetooth device
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
            if (bluetoothDevice != null) {
                BluetoothSubjectManager.getInstance().notifyDeviceFound(bluetoothDevice.getName() == null ?
                        bluetoothDevice.getAddress() : bluetoothDevice.getName(), bluetoothDevice.getAddress());
            }
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            switch (state) {
                case BluetoothDevice.BOND_BONDING://device is bounding
                    BluetoothSubjectManager.getInstance().notifyBoundState(BluetoothConstants.BoundState.STATE_BOND_BONDING);
                    break;
                case BluetoothDevice.BOND_BONDED://device bounded
                    BluetoothSubjectManager.getInstance().notifyBoundState(BluetoothConstants.BoundState.STATE_BOND_BONDED);
                    break;
                case BluetoothDevice.BOND_NONE://device not bound
                    BluetoothSubjectManager.getInstance().notifyBoundState(BluetoothConstants.BoundState.STATE_BOND_NONE);
                    break;
            }
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {//start discovery
            BluetoothSubjectManager.getInstance().notifyScanStarted();

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//discovery finish
            BluetoothSubjectManager.getInstance().notifyScanFinished();
        } else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {

        }
    }
}
