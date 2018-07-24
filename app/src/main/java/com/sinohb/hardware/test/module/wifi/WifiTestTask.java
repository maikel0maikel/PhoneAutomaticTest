package com.sinohb.hardware.test.module.wifi;



import android.os.Environment;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.constant.WifiConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.task.BaseAutoTestTask;
import com.sinohb.hardware.test.utils.FileUtils;
import com.sinohb.logger.LogTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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


public class WifiTestTask extends BaseAutoTestTask {
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
    private static final int SETP_OPEN_FAILURE = 14;
    private static final int STEP_RESET_FAILURE = 15;
    private static final int  STEP_CLOSE_FAILURE = 16;
    private static final int STEP_REOPEN_FAILURE = 17;
    private static final int STEP_NET_REQUEST = 18;
    private int mTestStep = 0;
    private static final int READ_TIME_OUT = 1000 * 10;
    private static final int CONNECT_TIME_OUT = READ_TIME_OUT;

    private static final int[] STEP_TITLES = {R.string.label_wifi_open, R.string.label_wifi_close,
            R.string.label_wifi_reopen, R.string.label_wifi_discovery, R.string.label_wifi_connect, R.string.label_wifi_request};
    public WifiTestTask(WifiPresenter.Controller presenter) {
        super(presenter);
        mTaskId = SerialConstants.ITEM_WIFI;
    }

    private void addStepEntity() {
        int i = 1;
        for (int res:STEP_TITLES) {
            StepEntity stepEntity = new StepEntity(i, HardwareTestApplication.getContext().
                    getResources().getString(res), Constants.TestItemState.STATE_TESTING);
            stepEntities.add(stepEntity);
            i++;
        }
    }


    private boolean deviceNotSupport(int result) {
        if (Constants.DEVICE_SUPPORTED != result) {
            LogTools.p(TAG, "Wifi测试测试结果设备不支持【测试不通过】");
            mExecuteState = STATE_TEST_UNPASS;
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
                    }else if (mTestStep == STEP_RESET){
                        mTestStep = STEP_RESET_FAILURE;
                        mSync.notify();
                    }else if (mTestStep == STEP_CLOSE){
                        mTestStep = STEP_CLOSE_FAILURE;
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
                    }else if (mTestStep == STEP_OPEN){
                        mTestStep = SETP_OPEN_FAILURE;
                        mSync.notify();
                    } else if (mTestStep == STEP_REOPEN){
                        mTestStep = STEP_REOPEN_FAILURE;
                        mSync.notify();
                    }
                    break;
            }
        }
    }

    public void notifyConnectState(int state) {
        synchronized (mSync) {
            if (mTestStep == STEP_CONNECT) {
                LogTools.p(TAG,"notifyConnectState method call state:"+state);
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
            LogTools.e(TAG, e, "trustAllHosts error");
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

    @Override
    protected void startTest() {
        mTestStep = STEP_OPEN;
        if (!stepEntities.isEmpty()){
            stepEntities.clear();
        }
        addStepEntity();
    }

    @Override
    protected void executeRunningState() throws InterruptedException {
        int result;
        while (mExecuteState == STATE_RUNNING) {
            LogTools.p(TAG,"mTestStep:"+mTestStep);
            synchronized (mSync) {
                switch (mTestStep) {
                    case STEP_OPEN:
                        LogTools.p(TAG, "Wifi测试打开");
                        result = ((WifiPresenter.Controller) mPresenter).openWifi();
                        if (result == Constants.DEVICE_RESET) {
                            LogTools.p(TAG, "WiFi处于打开状态，关闭重置");
                            mTestStep = STEP_RESET;
                            break;
                        } else if (deviceNotSupport(result)) {
                            break;
                        }
                        //mTestStep = STEP_DISCOVERY;//步骤调整
                        stepWait(STEP_OPEN, "WiFi打开失败，超时--");
                        break;
                    case SETP_OPEN_FINISH:
                        stepSuccess(STEP_CLOSE, 1, 0);
                        LogTools.p(TAG,"wifi打开成功");
                        break;
                    case SETP_OPEN_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "wifi测试打开结束，测试结果【测试不通过】");
                        break;
                    case STEP_RESET:
                        if (stepEntities.size() == STEP_TITLES.length) {
                            StepEntity stepEntity = new StepEntity(0,
                                    HardwareTestApplication.getContext().getResources().getString(R.string.label_wifi_reset),
                                    Constants.TestItemState.STATE_TESTING);
                            stepEntities.add(0,stepEntity);
                        }
                        result = ((WifiPresenter.Controller) mPresenter).closeWifi();
                        if (result == Constants.DEVICE_NORMAL){
                            stepOk(STEP_OPEN, 0);
                            break;
                        }else if (deviceNotSupport(result)){
                            break;
                        }
                        stepWait(STEP_RESET, "WiFi重置失败，超时--");
                        break;
                    case STEP_RESET_FINISHED:
                        stepOk(STEP_OPEN, 0);
                        LogTools.p(TAG, "重置成功");
                        break;
                    case STEP_RESET_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "wifi重置失败，测试结果【测试不通过】");
                        break;
                    case STEP_CLOSE:
                        LogTools.p(TAG, "Wifi测试关闭");
                        result = ((WifiPresenter.Controller) mPresenter).closeWifi();
                        if (deviceNotSupport(result)){
                            break;
                        }
                        //mExecuteState = STATE_FINISH;//步骤调整
                        stepWait(STEP_CLOSE, "WiFi关闭失败，超时--");
                        break;
                    case STEP_CLOSE_FINISH:
                        stepSuccess(STEP_REOPEN, 2, 1);
                        LogTools.p(TAG, "wifi测试关闭结束，测试结果【测试通过】");
                        break;
                    case STEP_CLOSE_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "wifi测试打开结束，测试结果【测试不通过】");
                        break;
                    case STEP_REOPEN:
                        LogTools.p(TAG, "重新打开Wifi");
                        result = ((WifiPresenter.Controller) mPresenter).openWifi();
                        if (deviceNotSupport(result)){
                            break;
                        }
                        stepWait(STEP_REOPEN, "WiFi重新打开失败，超时--");
                        break;
                    case STEP_REOPEN_FINISH:
                        stepSuccess(STEP_DISCOVERY, 3, 2);
                        LogTools.p(TAG, "wifi重新打开，测试结果【测试通过】");
                        break;
                    case STEP_REOPEN_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "wifi测试重新打开结束，测试结果【测试不通过】");
                        break;
                    case STEP_DISCOVERY:
                        LogTools.p(TAG, "Wifi测试扫描");
                        result = ((WifiPresenter.Controller) mPresenter).startScan();
                        if (deviceNotSupport(result)) {
                            break;
                        }
                        stepWait(STEP_DISCOVERY, "WiFi扫描失败，超时--");
                        break;
                    case STEP_DISCOVERY_OK:
                        stepSuccess(STEP_CONNECT, 4, 3);
                        LogTools.p(TAG, "wifi扫描ok，测试结果【测试通过】");
                        break;
                    case STEP_CONNECT:
                        LogTools.p(TAG, "Wifi测试连接，先睡眠2s防止获取不到热点");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LogTools.p(TAG, "Wifi测试连接");
                        String ssid = "";
                        String pwd = "";
                        /**
                         * 读取配置文件中的热点
                         */
                        try {
                            String json = FileUtils.getFileContent(Environment.getExternalStorageDirectory().getAbsolutePath()+"/wifiConfig.txt");
                            JSONObject object = new JSONObject(json);
                            ssid = object.getString("ssid");
                            pwd = object.getString("pwd");
                            LogTools.p(TAG,"ssid:"+ssid+",pwd:"+pwd);
                        } catch (IOException e) {
                            LogTools.e(TAG,"读取wifi热点失败");
                        } catch (JSONException e) {
                            LogTools.e(TAG,e,"读取wifi热点失败");
                        }
                        result = ((WifiPresenter.Controller) mPresenter).connectWifi(ssid,pwd);
                        if (result == Constants.DEVICE_CONNECTED) {
                            LogTools.p(TAG, "Wifi已经连接");
                            mTestStep = STEP_CONNECT_OK;
                            break;
                        } else if (deviceNotSupport(result)){
                            break;
                        }
                        stepWait(STEP_CONNECT, "WiFi扫描失败，超时--");
                        break;
                    case STEP_CONNECT_OK:
                        stepSuccess(STEP_NET_REQUEST, 5, 4);
                        LogTools.p(TAG,"wifi连接成功");
                        break;
                    case STEP_CONNECT_FAILURE:
                        mExecuteState = STATE_TEST_UNPASS;
                        LogTools.p(TAG, "wifi连接失败，测试结果【测试不通过】");
                        break;
                    case STEP_NET_REQUEST:
                        LogTools.p(TAG, "开始请求百度...");
                        int code = 0;
                        try {
                            code = getHttpCode("https://www.baidu.com/");
                        } catch (IOException e) {
                            LogTools.e(TAG, e, "请求失败");
                        }
                        if (code == 200) {
                            LogTools.p(TAG,"请求百度成功");
                            mExecuteState = STATE_FINISH;
                            stepEntities.get(stepEntities.size()-1).setTestState(Constants.TestItemState.STATE_SUCCESS);
                        }else {
                            mExecuteState = STATE_TEST_UNPASS;
                            LogTools.p(TAG, "请求百度失败，网络不可用，测试结果【测试不通过】");
                            stepEntities.get(stepEntities.size()-1).setTestState(Constants.TestItemState.STATE_FAIL);
                        }
                        //mTestStep = STEP_CLOSE;//步骤调整
                        break;
                }
            }
            Thread.sleep(100);
        }
    }

    private void stepSuccess(int nextStep, int i, int i2) {
        if (stepEntities.size() > STEP_TITLES.length) {
            stepOk(nextStep, i);
        } else {
            stepOk(nextStep, i2);
        }
    }

    private void stepWait(int step, String s) throws InterruptedException {
        mSync.wait(TASK_WAITE_TIME);
        if (mTestStep == step) {
            mExecuteState = STATE_TEST_UNPASS;
            LogTools.p(TAG, s);
        }
    }


    private void stepOk(int step, int i) {
        mTestStep = step;
        stepEntities.get(i).setTestState(Constants.TestItemState.STATE_SUCCESS);
    }



}
