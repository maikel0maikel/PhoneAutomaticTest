package com.sinohb.hardware.test.module.wifi;


import com.sinohb.hardware.test.constant.WifiConstants;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class WifiTestTask extends BaseTestTask {
    private static final String TAG = "WifiTestTask";
    private static final int STEP_OPEN = 1;
    private static final int SETP_OPEN_FINISH = 2;
    private static final int STEP_CLOSE = 3;
    private static final int STEP_CLOSE_FINISH = 4;
    private static final int STEP_REOPEN = 5;
    private static final int STEP_REOPEN_FINISH = 6;
    private static final int STEP_DISCOVERY = 7;
    private static final int STEP_DISCOVERY_OK = 8;
    private static final int STEP_RESET = 9;
    private static final int STEP_RESET_FINISHED = 10;
    private static final int STEP_CONNECT = 11;
    private static final int STEP_CONNECT_OK = 12;
    private static final int STEP_CONNECT_FAILURE = 13;
    private int mTestStep = 0;
    private static final int READ_TIME_OUT = 1000 * 10;
    private static final int CONNECT_TIME_OUT = READ_TIME_OUT;

    public WifiTestTask(WifiPresenter.Controller presenter) {
        super(presenter);
    }

    @Override
    public Boolean call() {
        boolean isPass = true;
        WifiPresenter.Controller controller = (WifiPresenter.Controller) mPresenter;
        LogTools.p(TAG, "Wifi开始测试");
        while (!isFinish) {
            synchronized (mSync) {
                if (mExecuteState == STATE_NONE) {//开始执行第一步打开Wifi测试
                    mExecuteState = STATE_RUNNING;
                    LogTools.p(TAG, "Wifi测试打开");
                    mTestStep = STEP_OPEN;
                    int result = controller.openWifi();
                    if (result == WifiConstants.DEVICE_RESET) {
                        controller.closeWifi();
                        mTestStep = STEP_RESET;
                        stepWaite(STEP_RESET);
                    }else if (deviceNotSupport(result)) return false;
                    mTestStep = STEP_OPEN;
                    result = controller.openWifi();
                    if (deviceNotSupport(result)) return false;
                    stepWaite(STEP_OPEN);
                    if (stepFailure(SETP_OPEN_FINISH, "Wifi测试打开，测试结果【测试不通过】")) return false;
                    LogTools.p(TAG, "Wifi测试打开结束，测试结果【测试通过】");
                    mTestStep = STEP_CLOSE;
                    LogTools.p(TAG, "Wifi测试关闭");
                    result = controller.closeWifi();
                    if (deviceNotSupport(result)) return false;
                    stepWaite(STEP_CLOSE);
                    if (stepFailure(STEP_CLOSE_FINISH, "Wifi测试关闭，测试结果【测试不通过】")) return false;
                    LogTools.p(TAG, "Wifi测试关闭结束，测试结果【测试通过】");
                    mTestStep = STEP_REOPEN;
                    LogTools.p(TAG, "重新打开Wifi");
                    result = controller.openWifi();
                    if (deviceNotSupport(result)) return false;
                    stepWaite(STEP_REOPEN);
                    if (stepFailure(STEP_REOPEN_FINISH, "Wifi测试重新打开结束测试，测试结果【测试不通过】")) return false;
                    LogTools.p(TAG, "重新打开Wifi结束");
                    mTestStep = STEP_DISCOVERY;
                    result = controller.startScan();
                    if (deviceNotSupport(result)) return false;
                    LogTools.p(TAG, "Wifi测试扫描");
                    stepWaite(STEP_DISCOVERY);
                    if (stepFailure(STEP_DISCOVERY_OK, "Wifi测试扫描，测试结果【测试不通过】")) return false;
                    LogTools.p(TAG, "Wifi测试扫描通过");
                    mTestStep = STEP_CONNECT;
                    LogTools.p(TAG, "Wifi测试连接");
                    result = controller.connectWifi();
                    if (result == WifiConstants.DEVICE_CONNECTED) {
                        LogTools.p(TAG, "Wifi已经连接");
                    } else if (deviceNotSupport(result)) return false;
                    stepWaite(STEP_CONNECT);
                    if (stepFailure(STEP_CONNECT_OK, "Wifi测试连接，测试结果【测试不通过】")) return false;
                    LogTools.p(TAG, "Wifi测试连接通过");
                    LogTools.p(TAG, "开始请求百度...");
                    int code = 0;
                    try {
                        code = getHttpCode("https://www.baidu.com/");
                    } catch (IOException e) {
                        LogTools.e(TAG, e, "请求失败");
                    }
                    if (code != 200) {
                        LogTools.p(TAG, "请求百度失败，网络不可用，测试结果【测试不通过】");
                        return false;
                    }
                    LogTools.p(TAG, "Wifi所有项目测试完毕，测试结果【测试通过】");
                } else if (mExecuteState == STATE_PAUSE) {
                    LogTools.i(TAG, "Wifi测试任务暂停");
                    try {
                        mSync.wait();
                    } catch (InterruptedException e) {
                        LogTools.e(TAG, e);
                    }
                }
            }
        }
        return isPass;
    }

    private boolean stepFailure(int setpOpenFinish, String s) {
        if (mTestStep != setpOpenFinish) {
            isFinish = true;
            LogTools.p(TAG, s);
            return true;
        }
        return false;
    }

    private void stepWaite(int step) {
        synchronized (mSync) {
            while (mTestStep == step) {
                LogTools.p(TAG, "进入等待----:step:" + step);
                try {
                    mSync.wait(TASK_WAITE_TIME);
                } catch (InterruptedException e) {
                    LogTools.e(TAG, e);
                }
            }
        }
    }

    private boolean deviceNotSupport(int result) {
        if (WifiConstants.DEVICE_SUPPORT != result) {
            isFinish = true;
            LogTools.p(TAG, "Wifi测试测试结果设备不支持【测试不通过】");
            return true;
        }
        return false;
    }

    public void notifyOpenState(int state) {
        LogTools.p(TAG, "mTestStep:" + mTestStep);
        synchronized (mSync) {
            switch (state) {
                case WifiConstants.OpenOrCloseState.STATE_OPENED:
                    if (mTestStep == STEP_OPEN) {
                        mTestStep = SETP_OPEN_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_REOPEN) {
                        mTestStep = STEP_REOPEN_FINISH;
                        mSync.notify();
                    }
                    break;
                case WifiConstants.OpenOrCloseState.STATE_CLOSED:
                    if (mTestStep == STEP_CLOSE) {
                        mTestStep = STEP_CLOSE_FINISH;
                        mSync.notify();
                    } else if (mTestStep == STEP_RESET) {
                        mTestStep = STEP_RESET_FINISHED;
                        mSync.notify();
                    }
                    break;
            }
        }
    }

    public void notifyConnectState(int state) {
        synchronized (mSync) {
            if (mTestStep == STEP_CONNECT) {
                mTestStep = state == WifiConstants.ConnectState.STATE_CONNECTED ? STEP_CONNECT_OK : STEP_CONNECT_FAILURE;
                mSync.notify();
            }
        }
    }

    public void notifyScanFinished() {
        synchronized (mSync) {
            if (mTestStep == STEP_DISCOVERY) {
                mTestStep = STEP_DISCOVERY_OK;
                mSync.notify();
            }
        }
    }




    private int getHttpCode(String requestUrl) throws IOException {
        int responseCode = 0;
        URL url = new URL(requestUrl);
        HttpURLConnection con = null;
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            con = https;
        } else {
            con = (HttpURLConnection) url.openConnection();
        }
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(CONNECT_TIME_OUT);
        con.setReadTimeout(READ_TIME_OUT);
        con.setRequestMethod("GET");
        responseCode = con.getResponseCode();
        con.disconnect();
        return responseCode;
    }

    private static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                LogTools.p(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                LogTools.p(TAG, "checkServerTrusted");
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LogTools.e(TAG,e,"trustAllHosts error");
        }
    }

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
