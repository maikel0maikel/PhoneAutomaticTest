package com.sinohb.hardware.test.module.wifi;


import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.module.wifi.receiver.WifiEventReceiver;
import com.sinohb.hardware.test.module.wifi.subject.WifiObserver;
import com.sinohb.hardware.test.module.wifi.subject.WifiSubjectManager;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.util.concurrent.FutureTask;

public class WifiController implements WifiPresenter.Controller ,WifiObserver{
    private WifiPresenter.View mView;
    private WifiEventReceiver mReceiver;
    private static final String TAG = "WifiController";
    private WifiTestTask mTask;
    private WifiManagerable mWifiManager;
    public WifiController(WifiPresenter.View view) {
        WifiSubjectManager.getInstance().attchObserver(this);
        this.mView = view;
        registReceiver();
        mWifiManager = new WifiManagerImpl();
        view.setPresenter(this);

    }

    @Override
    public int openWifi() {
        return mWifiManager.openWifi();
    }

    @Override
    public int closeWifi() {
        return mWifiManager.closeWifi();
    }

    @Override
    public int startScan() {
        return mWifiManager.startScan();
    }

    @Override
    public int connectWifi() {
        return mWifiManager.connectWifi();
    }

    @Override
    public void notifyOpenState(int state) {
        LogTools.e(TAG,"notifyOpenState:"+state);
        mTask.notifyOpenState(state);
    }


    @Override
    public void notifyConnectedState(int connectedState) {
        mTask.notifyConnectState(connectedState);
    }

    @Override
    public void notifyScanFinished() {
        mTask.notifyScanFinished();
    }

    @Override
    public void start() {
        mTask = new WifiTestTask(this);
        FutureTask futureTask = new FutureTask(mTask);
        ThreadPool.getPool().execute(futureTask);
    }

    private void registReceiver() {
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

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void destroy() {
        if (mReceiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        if (mTask!=null){
            mTask.stopTask();
            mTask = null;
        }
        WifiSubjectManager.getInstance().detachObserver(this);
    }
}
