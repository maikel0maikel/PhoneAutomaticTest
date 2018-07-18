package com.sinohb.hardware.test.module.wifi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.sinohb.hardware.test.constant.WifiConstants;
import com.sinohb.hardware.test.module.wifi.subject.WifiSubjectManager;
import com.sinohb.logger.LogTools;

public class WifiEventReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiEventReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            //LogTools.e(TAG, "action:" + action);
            //获取当前的wifi状态int类型数据
            int mWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            LogTools.p(TAG,"mWifiState:"+mWifiState);
            switch (mWifiState) {
                case WifiManager.WIFI_STATE_ENABLED://已打开
                    WifiSubjectManager.getInstance().notifyOpenOrCloseState(WifiConstants.OpenOrCloseState.STATE_OPENED);
                    break;
                case WifiManager.WIFI_STATE_ENABLING://打开中
                    WifiSubjectManager.getInstance().notifyOpenOrCloseState(WifiConstants.OpenOrCloseState.STATE_OPENING);
                    break;
                case WifiManager.WIFI_STATE_DISABLED://已关闭
                    WifiSubjectManager.getInstance().notifyOpenOrCloseState(WifiConstants.OpenOrCloseState.STATE_CLOSED);
                    break;
                case WifiManager.WIFI_STATE_DISABLING://关闭中
                    WifiSubjectManager.getInstance().notifyOpenOrCloseState(WifiConstants.OpenOrCloseState.STATE_CLOSING);
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN://未知
                    WifiSubjectManager.getInstance().notifyOpenOrCloseState(WifiConstants.OpenOrCloseState.STATE_UNKONWN);
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            LogTools.e(TAG, "action:" + action);
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {//扫描完成
            LogTools.e(TAG, "action:" + action);
            WifiSubjectManager.getInstance().notifyScanFinished();
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            LogTools.e(TAG, "action:" + action);
            // NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            // LogTools.p(TAG, "网络状态变化:");
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            //String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
            if (info != null && info.isConnected()) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null && WifiConstants.WifiConfigurate.SSID_REAL.equals(wifiInfo.getSSID())) {
                    WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.ConnectState.STATE_CONNECTED);
                }else {
                    LogTools.p(TAG,"wifi not connected or not equals WifiConstants.WifiConfigurate.SSID:"+WifiConstants.WifiConfigurate.SSID_REAL);
                    WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.ConnectState.STATE_CONNECT_FAILURE);
                }
            } else {
                LogTools.p(TAG,"wifi not connected");
                WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.ConnectState.STATE_CONNECT_FAILURE);
            }
        }
    }
}
