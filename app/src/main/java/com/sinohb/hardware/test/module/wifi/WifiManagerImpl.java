package com.sinohb.hardware.test.module.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.WifiConstants;
import com.sinohb.hardware.test.module.wifi.subject.WifiSubjectManager;
import com.sinohb.logger.LogTools;

import java.util.List;

public class WifiManagerImpl implements WifiManagerable {
    private static final String TAG = "WifiManagerImpl";
    private WifiManager mWifiManager;

    WifiManagerImpl() {
        mWifiManager = (WifiManager) HardwareTestApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public int openWifi() {
        if (mWifiManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else {
            //mWifiManager.setWifiEnabled(false);
            //WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.OpenOrCloseState.STATE_OPENED);
            return Constants.DEVICE_RESET;
        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int closeWifi() {
        if (mWifiManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else {
            // WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.OpenOrCloseState.STATE_CLOSED);
            return Constants.DEVICE_NORMAL;
        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int startScan() {
        if (mWifiManager == null) {
            return Constants.DEVICE_NOT_SUPPORT;
        }
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
        } else {

        }
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int connectWifi(String ssid,String pwd) {
        if (ssid == null||ssid.length()==0){
            ssid = WifiConstants.WifiConfigurate.SSID;
        }
        if (isSSIDConnected(ssid)) {
            LogTools.p(TAG, "设备已经连接");
            return Constants.DEVICE_CONNECTED;
        }
        return realConnect(ssid,pwd);
    }

    private int realConnect(String ssid,String pwd) {
        List<ScanResult> scanResultList = mWifiManager.getScanResults();
        for (ScanResult result : scanResultList) {
            if (result.SSID.equalsIgnoreCase(ssid)) {
                return realConnectSSID(result.SSID,pwd);
            }
        }
        LogTools.p(TAG, "未找到SSID直接去连接");
       return realConnectSSID(ssid,pwd);
    }

    private int realConnectSSID(String ssid,String pwd) {
        if (pwd == null||pwd.length()==0){
            pwd = "12345678";
        }
        WifiConfiguration wifiConfig = createWifiInfo(ssid, pwd, WifiCipherType.WIFICIPHER_WPA);
        if (wifiConfig == null) {
            LogTools.e(TAG, "createWifiInfo null connect failure");
            WifiSubjectManager.getInstance().notifyConnectedState(WifiConstants.ConnectState.STATE_CONNECT_FAILURE);
            return WifiConstants.WifiConfigurate.SSID_CREATE_FAILURE;
        }
        WifiConfiguration tempConfig = isExsits(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
            LogTools.p(TAG, "realConnectSSID remove tempConfig");
        }
        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean enabled = mWifiManager.enableNetwork(netID, true);
        boolean connected = mWifiManager.reconnect();
        LogTools.p(TAG, "realConnectSSID enableNetwork=" + enabled);
        LogTools.p(TAG, "realConnectSSID reconnect=" + connected);
        return Constants.DEVICE_SUPPORTED;
    }

    private boolean isSSIDConnected(String ssid) {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getSSID().equalsIgnoreCase(ssid);
        }
        return false;
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    public WifiConfiguration createWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs != null && existingConfigs.size() > 0) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);

    }
}
