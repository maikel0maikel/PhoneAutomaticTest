package com.sinohb.hardware.test.module.bluetooth;


import com.marsir.btear.BtearListener;
import com.marsir.btear.BtearManager;
import com.marsir.btear.BtearParam;
import com.marsir.btear.BtearScanListener;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.logger.LogTools;


/**
 * 蓝牙管理类
 * 实现打开、关闭、连接、绑定等功能
 */
public class BLEManager implements BluetoothManagerable, BtearListener,BtearScanListener {
    private BtearManager mDeviceBluetoothManager;
    private static final String TAG = "BluetoothTestManager";

    BLEManager() {
        mDeviceBluetoothManager = (BtearManager) HardwareTestApplication.getContext().getSystemService(BluetoothConstants.BTEAR_SERVICE);
        mDeviceBluetoothManager.addBtearListener(this);
        mDeviceBluetoothManager.addBtearScanListener(this);
    }

    @Override
    public int open() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->open ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (!isEnable()) {
            LogTools.p(TAG,"call open method");
            mDeviceBluetoothManager.powerOn();
        } else {
            return BluetoothConstants.DEVICE_RESET;
        }
        return BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int close() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->close ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            LogTools.p(TAG,"call close method");
            mDeviceBluetoothManager.powerOff();
        } else {
            //BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
            return BluetoothConstants.DEVICE_RESET;
        }
        return BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int startDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->startDiscovery ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            mDeviceBluetoothManager.startScanDevice();
        } else {
            BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
        }
        return BluetoothConstants.DEVICE_SUPPORT;
    }

    private boolean isEnable() {

        return mDeviceBluetoothManager.getPowerState() == BtearManager.POWER_STATE_ENABLED;
    }

    @Override
    public int stopDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->stopDiscovery ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            mDeviceBluetoothManager.stopScanDevice();
        } else {
            BluetoothSubjectManager.getInstance().notifyScanFinished();
        }
        return BluetoothConstants.DEVICE_SUPPORT;
    }

    @Override
    public int connect(String btAddress) {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->connect ]");
            return BluetoothConstants.DEVICE_NOT_SUPPORT;
        }
          mDeviceBluetoothManager.linkTo(btAddress);
        return BluetoothConstants.DEVICE_SUPPORT;
    }


    @Override
    public void onPowerStateChanged(int btState) {
        LogTools.p(TAG, "onPowerStateChanged:" + btState);
        switch (btState){
            case BtearManager.POWER_STATE_ENABLING:
                BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNING_ON);
                break;
            case BtearManager.POWER_STATE_ENABLED:
                BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_ON);
                break;
            case BtearManager.POWER_STATE_DISABLING:
                BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNING_OFF);
                break;
            case BtearManager.POWER_STATE_DISABLED:
                BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
                break;
        }
    }

    @Override
    public void onCallStateChanged(int i) {
        LogTools.p(TAG,"onCallStateChanged method call i:"+i);
    }

    @Override
    public void onMediaStateChanged(int i) {
        LogTools.p(TAG,"onMediaStateChanged method call i:"+i);
    }

    @Override
    public void onParamChanged(BtearParam btearParam, int i) {
        LogTools.p(TAG,"onParamChanged method call i:"+i+",btearParam:"+btearParam);
    }

    @Override
    public void onScanDeviceEvent(String s, String s1) {
        LogTools.p(TAG,"s:"+s+",s1:"+s1);
        BluetoothSubjectManager.getInstance().notifyScanStarted();
    }

    @Override
    public void onScanFinish() {
        LogTools.p(TAG,"onScanFinish method call");
        BluetoothSubjectManager.getInstance().notifyScanFinished();
    }
}
