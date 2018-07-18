package com.sinohb.hardware.test.module.frc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.logger.LogTools;

public abstract class RFCController extends BaseDisplayViewController implements RFCSendListener {
    protected RFCDataReceiver receiver;

    public RFCController(BaseDisplayViewView view) {
        super(view);
        registReceiver();
    }


//    @Override
//    public void start() {
//        registReceiver();
//        super.start();
//    }
//
//    @Override
//    public void complete() {
//        unregistReceiver();
//        super.complete();
//    }

    @Override
    public void destroy() {
        unregistReceiver();
        super.destroy();
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
                receiverData(serialNo,msgId,msg,RFCController.this);
            }
        }
    }

    protected abstract void receiverData(int no,int id,String msg,RFCSendListener listener);
}
