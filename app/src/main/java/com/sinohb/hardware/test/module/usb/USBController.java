package com.sinohb.hardware.test.module.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.logger.LogTools;

public class USBController extends BaseExecuteController implements USBPresenter.Controller {
    private static final String USB_DIR_PATH = "/storage/usbdisk0";
    private StorageBroadcastReceiver receiver;

    public USBController(BaseExecuteView view) {
        super(view);
        init();
    }


    @Override
    protected void init() {
        task = new USBTestTask(this);
        task.setmTaskId(SerialConstants.ITEM_USB);
        registReceiver();
    }

    private void registReceiver() {
        if (receiver == null) {
            receiver = new StorageBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter.setPriority(1000);
            intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
            intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
            intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
            intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
            intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
            intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
            intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentFilter.addDataScheme("file");
            HardwareTestApplication.getContext().registerReceiver(receiver, intentFilter);
        }
    }

    private void unregistReceiver(){
        if (receiver !=null){
            HardwareTestApplication.getContext().unregisterReceiver(receiver);
            receiver = null;
        }
    }


    @Override
    public void setDirPath() {
        setPath(USB_DIR_PATH);
    }

    @Override
    public void notifyStatus(int state) {
        if (mView != null) {
            ((USBPresenter.View) mView).notifyStoregeStatus(state);
        }
    }

    protected void setPath(String path) {
        if (task != null) {
            ((USBTestTask) task).setUsbPath(path);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        unregistReceiver();
    }

    public class StorageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                if (intent.getData() != null) {
                    notifyStorageState(1,intent.getData().getPath(),USBController.this);
                }
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
                if (intent.getData() != null) {
                    notifyStorageState(0,intent.getData().getPath(),USBController.this);
                }
            } else if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)) {
            } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
            } else if (Intent.ACTION_MEDIA_SHARED.equals(action)) {
            }
        }
    }


    protected void notifyStorageState(int state, String path, USBController controller) {
        LogTools.p(TAG,"state:"+state+",path:"+path+",controller:"+controller.toString());
        if (controller == this && USB_DIR_PATH.equals(path)) {
            notifyStatus(state);
        }
    }
}
