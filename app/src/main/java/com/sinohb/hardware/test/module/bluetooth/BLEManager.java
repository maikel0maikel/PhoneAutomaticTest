package com.sinohb.hardware.test.module.bluetooth;


import com.marsir.btear.BtearListener;
import com.marsir.btear.BtearManager;
import com.marsir.btear.BtearParam;
import com.marsir.btear.BtearScanListener;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.BluetoothConstants;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.bluetooth.subject.BluetoothSubjectManager;
import com.sinohb.logger.LogTools;


/**
 * 蓝牙管理类
 * 实现打开、关闭、连接、绑定等功能
 */
public class BLEManager implements BluetoothManagerable /**, BtearListener,BtearScanListener**/
{
    private BtearManager mDeviceBluetoothManager;
    private static final String TAG = "BluetoothTestManager";
    private MyBtearListener mBtearListener;
    private MyBtearScanListener mBtearScanListener;

    BLEManager() {
        mDeviceBluetoothManager = (BtearManager) HardwareTestApplication.getContext().getSystemService(BluetoothConstants.BTEAR_SERVICE);
        if (mDeviceBluetoothManager != null) {
            if (mBtearScanListener == null)
                mBtearScanListener = new MyBtearScanListener();
            if (mBtearListener == null)
                mBtearListener = new MyBtearListener();
            mDeviceBluetoothManager.addBtearListener(mBtearListener);
            mDeviceBluetoothManager.addBtearScanListener(mBtearScanListener);

        }
    }

    @Override
    public int open() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->open ]");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (!isEnable()) {
            LogTools.p(TAG, "call open method");
            mDeviceBluetoothManager.powerOn();
        } else {
            return Constants.DEVICE_RESET;
        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int close() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->close ]");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            LogTools.p(TAG, "call close method");
            mDeviceBluetoothManager.powerOff();
        } else {
            //BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
            return Constants.DEVICE_NORMAL;
        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int startDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->startDiscovery ]");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            mDeviceBluetoothManager.startScanDevice();
        } else {
            BluetoothSubjectManager.getInstance().notifyOpenState(BluetoothConstants.OpenState.STATE_TURNED_OFF);
        }
        return Constants.DEVICE_SUPPORTED;
    }

    private boolean isEnable() {

        return !(mDeviceBluetoothManager.getPowerState() == BtearManager.POWER_STATE_DISABLED
                || mDeviceBluetoothManager.getPowerState() == BtearManager.POWER_STATE_DISABLING);
    }

    @Override
    public int stopDiscovery() {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->stopDiscovery ]");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (isEnable()) {
            mDeviceBluetoothManager.stopScanDevice();
        } else {
            BluetoothSubjectManager.getInstance().notifyScanFinished();
        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int connect(String btAddress) {
        if (mDeviceBluetoothManager == null) {
            LogTools.e(TAG, "该设备不支持蓝牙[ method --->connect ]");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (btAddress == null || btAddress.length() == 0) {
            LogTools.e(TAG, "btAddress is empty!!");
            return BluetoothConstants.ConnectState.STATE_DISCONNECTED;
        }
        if (mDeviceBluetoothManager.getPowerState() == BtearManager.POWER_STATE_LINKED){
            LogTools.p(TAG,"connect device is already connected");
            return Constants.DEVICE_CONNECTED;
        }
        mDeviceBluetoothManager.linkTo(btAddress);
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public void destroy() {
        if (mDeviceBluetoothManager != null) {
            if (mBtearScanListener != null) {
                mDeviceBluetoothManager.removeBtearScanListener(mBtearScanListener);
                mBtearScanListener = null;
            }
            if (mBtearListener != null) {
                mDeviceBluetoothManager.removeBtearListener(mBtearListener);
                mBtearListener = null;
            }
        }
    }


    static class MyBtearListener implements BtearListener {

        @Override
        public void onPowerStateChanged(int btState) {
            LogTools.p(TAG, "onPowerStateChanged:" + btState);
            switch (btState) {
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
                case BtearManager.POWER_STATE_LINKED:
                    BluetoothSubjectManager.getInstance().notifyConnectedState(BluetoothConstants.ConnectState.STATE_CONNECTED);
                    break;
            }
        }

        @Override
        public void onCallStateChanged(int i) {
            LogTools.p(TAG, "onCallStateChanged method call i:" + i);
        }

        @Override
        public void onMediaStateChanged(int i) {
            LogTools.p(TAG, "onMediaStateChanged method call i:" + i);
        }

        @Override
        public void onParamChanged(BtearParam btearParam, int i) {
            LogTools.p(TAG, "onParamChanged method call i:" + i + ",btearParam:" + btearParam);
        }
    }

    static class MyBtearScanListener implements BtearScanListener {

        @Override
        public void onScanDeviceEvent(String name, String mac) {
            BluetoothSubjectManager.getInstance().notifyScanStarted();
            BluetoothSubjectManager.getInstance().notifyDeviceFound(name, mac);
        }

        @Override
        public void onScanFinish() {
            LogTools.p(TAG, "onScanFinish method call");
            BluetoothSubjectManager.getInstance().notifyScanFinished();
        }
    }
}
