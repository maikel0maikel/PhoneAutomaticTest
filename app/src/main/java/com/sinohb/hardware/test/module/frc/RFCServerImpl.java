package com.sinohb.hardware.test.module.frc;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.hbserialport.service.ISerialPortService;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;


public class RFCServerImpl implements IRFCServer {
    private static final String TAG = "RFCServerImpl";
    private ISerialPortService mSerialPortService;
    private static final int STATE_BINGNONE = 0;
    private static final int STATE_BINDING = 1;
    private static final int STATE_BINDED = 2;
    private static final int STATE_BIND_FAILER = 3;
    private int mBindState = STATE_BINGNONE;
    private LinkedBlockingDeque<SerialCommand> serialCommands = new LinkedBlockingDeque<>();
    private List<RFCSendListener> rfcSendListeners = new ArrayList<>();

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
            realConnect();
        }
    };

    RFCServerImpl() {

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
            serialCommands.offer(command);
            return;
        }
        send(command);
    }

    private void send(final SerialCommand c) {
        ThreadPool.getPool().executeSingleTask(new Runnable() {
            @Override
            public void run() {
                if (mSerialPortService == null) {
                    realConnect();
                    return;
                }
                realSendCommand(c);

            }
        });
    }

    @Override
    public void disconnectService() {
        if (mBindState == STATE_BINDED) {
            HardwareTestApplication.getContext().unbindService(mSerialPortConnection);
            mBindState = STATE_BINGNONE;
        }
    }

    @Override
    public boolean isConnected() {
        return mBindState == STATE_BINDING || mBindState == STATE_BINDED;
    }

    @Override
    public void registSendListener(RFCSendListener listener) {
        if (!rfcSendListeners.contains(listener)){
            rfcSendListeners.add(listener);
        }
    }

    @Override
    public void removeSendListener(RFCSendListener listener) {
        rfcSendListeners.remove(listener);
    }

    private void executeCommands() {
        ThreadPool.getPool().executeSingleTask(new Runnable() {
            @Override
            public void run() {
                while (!serialCommands.isEmpty()) {
                    if (mSerialPortService == null) {
                        realConnect();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    SerialCommand c = serialCommands.pop();
                    realSendCommand(c);
                }
            }
        });
    }

    private void realSendCommand(SerialCommand c) {
        if (c!=null){
            try {
                mSerialPortService.sendMsg(c.getSerialNo(), c.getMsgId(), c.getData());
                LogTools.p(TAG, "msgId:" +  c.getMsgId() + ",data:" + c.getData());
                if (c.getSendListener()!=null){
                    c.getSendListener().onSuccess(c.getSerialNo(),c.getMsgId());
                }
            } catch (Exception e) {
                LogTools.e(TAG, e,"msgId:" +  c.getMsgId() + ",data:" + c.getData());
                if (c.getSendListener()!=null){
                    c.getSendListener().onFailure(c.getSerialNo(),c.getMsgId());
                }
            }
        }else {
            LogTools.e(TAG, "SerialCommand is null");
        }
    }

    private void notifySendSuccess(int no,int id){
        for (RFCSendListener listener:rfcSendListeners){
            listener.onSuccess(no,id);
        }
    }

    private void notifySendFailure(int no,int id){
        for (RFCSendListener listener:rfcSendListeners){
            listener.onFailure(no,id);
        }
    }

}
