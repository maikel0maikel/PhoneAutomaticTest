package com.sinohb.hardware.test.module.frc;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hbserialport.service.ISerialPortService;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RFCServerImpl implements IRFCServer {
    private static final String TAG = "RFCServerImpl";
    private ISerialPortService mSerialPortService;
    private static final int STATE_BINGNONE = 0;
    private static final int STATE_BINDING = 1;
    private static final int STATE_BINDED = 2;
    private static final int STATE_BIND_FAILER = 3;
    private int mBindState = STATE_BINGNONE;
    private final List<SerialCommand> serialCommands = new LinkedList<>();
    private List<RFCSendListener> rfcSendListeners = new ArrayList<>();
    private RFCDataReceiver receiver;
    private SendRunnable sendRunnable;
    private static final int STATE_SENDING = 1;
    private static final int STATE_SEND_OK = 2;
    private static final int STATE_SEND_FAIL = 3;

    private ServiceConnection mSerialPortConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSerialPortService = ISerialPortService.Stub.asInterface(service);
            mBindState = STATE_BINDED;
            LogTools.e(TAG, "onServiceConnected --->method call ");
            /**
             * can send saved command
             * executeCommands();
             */
            executeCommands();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBindState = STATE_BIND_FAILER;
            LogTools.e(TAG, "onServiceDisconnected --->method call ");
            stopSend();
            realConnect();
        }
    };

    RFCServerImpl() {
        init();
    }

    private void init() {
        registReceiver();
        if (sendRunnable == null) {
            sendRunnable = new SendRunnable(this);
            ThreadPool.getPool().executeSingleTask(sendRunnable);
            LogTools.p(TAG, "RFCServerImpl 开启发送线程");
        } else {
            sendRunnable.stop = false;
            ThreadPool.getPool().executeSingleTask(sendRunnable);
            LogTools.p(TAG, "sendRunnable 不为空 开启发送线程");
        }
    }

    @Override
    public void connectService() {
        if (mBindState == STATE_BINDING || mBindState == STATE_BINDED) {
            LogTools.p(TAG, "service is connected");
            return;
        }
        realConnect();
    }

    private void realConnect() {
        if (mBindState == STATE_BINDING) {
            LogTools.p(TAG, "正在绑定……");
            return;
        }
        mBindState = STATE_BINDING;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hbserialport.service", "com.hbserialport.service.BackgroundService"));
        HardwareTestApplication.getContext().bindService(intent, mSerialPortConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void sendMsg(SerialCommand command) {
        if (mSerialPortService == null) {
            LogTools.e(TAG, "service is null do connect");
            realConnect();
            /**
             * can add command
             * SerialCommand c = new SerialCommand(serialNo,msgId,data);
             */
            serialCommands.add(command);
            return;
        }
        synchronized (serialCommands) {
            serialCommands.add(command);
            serialCommands.notify();
        }
    }


    @Override
    public void disconnectService() {
        if (mBindState == STATE_BINDED) {
            HardwareTestApplication.getContext().unbindService(mSerialPortConnection);
            mBindState = STATE_BINGNONE;
        }
        unregistReceiver();
        stopSend();
    }

    private void stopSend() {
        if (sendRunnable != null) {
            sendRunnable.stop();
            sendRunnable = null;
        }
        rfcSendListeners.clear();
        serialCommands.clear();
    }

    @Override
    public boolean isConnected() {
        return mBindState == STATE_BINDING || mBindState == STATE_BINDED;
    }

    @Override
    public void registSendListener(RFCSendListener listener) {
        if (!rfcSendListeners.contains(listener)) {
            rfcSendListeners.add(listener);
        }
    }

    @Override
    public void removeSendListener(RFCSendListener listener) {
        rfcSendListeners.remove(listener);
    }

    private void executeCommands() {
        synchronized (serialCommands) {
            LogTools.p(TAG, "executeCommands serialCommands.size = " + serialCommands.size());
            serialCommands.notify();
        }
    }


    static class SendRunnable implements Runnable {
        private boolean stop = false;
        private WeakReference<RFCServerImpl> weakReference;
        private SerialCommand mCurrentCommand = null;
        private Object msyncSend = new Object();
        private int mSendState = 0;

        SendRunnable(RFCServerImpl server) {
            this.weakReference = new WeakReference<>(server);
        }

        @Override
        public void run() {
            if (weakReference == null || weakReference.get() == null) {
                stop = true;
                LogTools.e(TAG, "SendRunnable weakReference or weakReference.get() is null");
                return;
            }
            RFCServerImpl server = weakReference.get();
            while (!stop && server != null) {
                synchronized (server.serialCommands) {
                    while (!stop && server.serialCommands.isEmpty()) {
                        try {
                            LogTools.p(TAG, "进入等待");
                            server.serialCommands.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!server.serialCommands.isEmpty()) {
                        mCurrentCommand = server.serialCommands.remove(0);
                    }
                }
                if (mCurrentCommand != null) {
                    synchronized (msyncSend) {
                        try {
                            server.mSerialPortService.sendMsg(mCurrentCommand.getSerialNo(), mCurrentCommand.getMsgId(),
                                    mCurrentCommand.getData());
                            LogTools.p(TAG, "send: msgId=" + mCurrentCommand.getMsgId() + ",data=" + mCurrentCommand.getData());
                            mSendState = STATE_SENDING;
                            msyncSend.wait(3 * 1000);
                            if (mSendState == STATE_SENDING) {
                                LogTools.p(TAG, "发送超时---");
                                sendFail();
                            }
                        } catch (Exception e) {
                            LogTools.e(TAG, e, "msgId:" + mCurrentCommand.getMsgId() + ",data:" + mCurrentCommand.getData());
                            sendFail();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogTools.e(TAG, "SerialCommand is null");
                }

            }
            LogTools.p(TAG, "发送指令线程销毁----");
        }

        private void sendFail() {
            mSendState = STATE_SEND_FAIL;
            if (mCurrentCommand.getSendListener() != null) {
                mCurrentCommand.getSendListener().onFailure(mCurrentCommand.getSerialNo(), mCurrentCommand.getMsgId());
            }
        }

        public void stop() {
            stop = true;
            if (weakReference != null && weakReference.get() != null) {
                synchronized (weakReference.get().serialCommands) {
                    weakReference.get().serialCommands.notify();
                }
            }

            synchronized (msyncSend) {
                msyncSend.notify();
            }
        }

        void onSendSuccess(int NO, int id, String data) {
            synchronized (msyncSend) {
                mSendState = STATE_SEND_OK;
                LogTools.p(TAG, "serialNo=" + NO + ",msgId=" + id + ",msg=" + data);
                if (mCurrentCommand != null && mCurrentCommand.getSendListener() != null) {
                    mCurrentCommand.getSendListener().onSuccess(mCurrentCommand.getSerialNo(), id, data);
                }
                msyncSend.notify();
            }
        }
    }

    private void registReceiver() {
        if (receiver == null) {
            receiver = new RFCDataReceiver();
            IntentFilter intenetFliter = new IntentFilter();
            intenetFliter.addAction("123456");
            HardwareTestApplication.getContext().registerReceiver(receiver, intenetFliter);
        }
    }

    private void unregistReceiver() {
        if (receiver != null) {
            HardwareTestApplication.getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    class RFCDataReceiver extends BroadcastReceiver {
        private static final String ACTION = "123456";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            if (ACTION.equals(intent.getAction())) {
                String msg = intent.getStringExtra("msg");
                int msgId = intent.getIntExtra("msgId", -1);
                int serialNo = intent.getIntExtra("serialNo", -1);
                if ((msgId == 0x8601 || msgId == 0xC001) && sendRunnable != null) {
                    sendRunnable.onSendSuccess(serialNo, msgId, msg);
                }
            }
        }
    }

}
