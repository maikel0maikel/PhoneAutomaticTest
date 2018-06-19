package com.sinohb.hardware.test.module.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.hardware.test.utils.BtUtils;
import com.sinohb.logger.LogTools;

import java.lang.reflect.InvocationTargetException;

/**
 * 蓝牙管理类
 * 实现打开、关闭、连接、绑定等功能
 */
public class BluetoothManager implements BluetoothManagerable {
    private BluetoothAdapter mDeviceBluetoothManager;
    private static final String TAG = "BluetoothManager";
    BluetoothManager(){
        mDeviceBluetoothManager = BluetoothAdapter.getDefaultAdapter();
    }
    @Override
    public int open() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG,"该设备不支持蓝牙[ method --->open ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (!mDeviceBluetoothManager.isEnabled()) {
            mDeviceBluetoothManager.enable();
        } else {
           // BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_ON);
            return BluetoothConstants.DEVICE_RESET;
        }
        return  BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int close() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG,"该设备不支持蓝牙[ method --->close ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (mDeviceBluetoothManager.isEnabled()) {
            mDeviceBluetoothManager.disable();
        } else {
            //BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
            return BluetoothConstants.DEVICE_RESET;
        }
        return  BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int startDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG,"该设备不支持蓝牙[ method --->startDiscovery ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (mDeviceBluetoothManager.isEnabled() && !mDeviceBluetoothManager.isDiscovering()) {
            mDeviceBluetoothManager.startDiscovery();
        } else if (!mDeviceBluetoothManager.isEnabled()) {
            BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
        }
        return  BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int stopDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG,"该设备不支持蓝牙[ method --->stopDiscovery ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (mDeviceBluetoothManager.isDiscovering()) {
            mDeviceBluetoothManager.cancelDiscovery();
        } else {
            BluetoothSubjectManager.getInstance().notifyScanFinished();
        }
        return  BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int connect(String btAddress) {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG,"该设备不支持蓝牙[ method --->connect ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        BluetoothDevice device = mDeviceBluetoothManager.getRemoteDevice(btAddress);
        int boundState = device.getBondState();
        switch (boundState) {
            case BluetoothDevice.BOND_BONDED:
                break;
            case BluetoothDevice.BOND_BONDING:
                break;
            case BluetoothDevice.BOND_NONE:
                createBond(device);
                break;
        }
        return  BluetoothConstants.DEVICE_SUPPORT;
    }

    private void createBond(BluetoothDevice device) {
        Class clz = BluetoothDevice.class;
        try {
            BtUtils.createBond(clz,device);
        } catch (NoSuchMethodException e) {
            LogTools.e(TAG,e);
        } catch (InvocationTargetException e) {
            LogTools.e(TAG,e);
        } catch (IllegalAccessException e) {
            LogTools.e(TAG,e);
        }
    }
}
