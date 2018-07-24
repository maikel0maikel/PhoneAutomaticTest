package com.sinohb.hardware.test.module.wifi;


import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.hardware.test.module.wifi.receiver.WifiEventReceiver;
import com.sinohb.hardware.test.module.wifi.subject.WifiObserver;
import com.sinohb.hardware.test.module.wifi.subject.WifiSubjectManager;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class WifiController extends BaseExecuteController implements WifiPresenter.Controller, WifiObserver {
    private WifiEventReceiver mReceiver;
    private static final String TAG = "WifiController";
    private WifiManagerable mWifiManager;

    public WifiController(BaseExecuteView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        WifiSubjectManager.getInstance().attchObserver(this);
        registReceiver();
        mWifiManager = new WifiManagerImpl();
        task = new WifiTestTask(this);
    }

    @Override
    public int openWifi() {
        return mWifiManager == null ? Constants.DEVICE_NOT_SUPPORT : mWifiManager.openWifi();
    }

    @Override
    public int closeWifi() {
        return mWifiManager == null ? Constants.DEVICE_NOT_SUPPORT : mWifiManager.closeWifi();
    }

    @Override
    public int startScan() {
        return mWifiManager == null ? Constants.DEVICE_NOT_SUPPORT : mWifiManager.startScan();
    }

    @Override
    public int connectWifi(String ssid,String pwd) {
        return mWifiManager == null ? Constants.DEVICE_NOT_SUPPORT : mWifiManager.connectWifi(ssid,pwd);
    }

    @Override
    public void notifyOpenState(int state) {
        LogTools.e(TAG, "notifyOpenState:" + state);
        if (mView != null) {
            ((WifiPresenter.View)mView).notifyOpenOrCloseState(state);
        }
        if (task != null) {
            ((WifiTestTask)task).notifyOpenState(state);
        }
    }


    @Override
    public void notifyConnectedState(int connectedState) {
        if (task != null) {
            ((WifiTestTask)task).notifyConnectState(connectedState);
        }
    }

    @Override
    public void notifyScanFinished() {
        if (task != null) {
            ((WifiTestTask)task).notifyScanFinished();
        }
    }



    private void registReceiver() {
        if (mReceiver == null) {
            IntentFilter mIntentFilter = new IntentFilter();
            mReceiver = new WifiEventReceiver();
            mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            mIntentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
            //mIntentFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            HardwareTestApplication.getContext().registerReceiver(mReceiver, mIntentFilter);
        }
    }

    @Override
    public void destroy() {
        if (mReceiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        WifiSubjectManager.getInstance().detachObserver(this);
        super.destroy();
    }

//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
