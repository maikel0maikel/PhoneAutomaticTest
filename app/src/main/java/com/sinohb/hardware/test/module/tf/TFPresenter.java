package com.sinohb.hardware.test.module.tf;

import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.module.usb.USBController;
import com.sinohb.logger.LogTools;

public class TFPresenter extends USBController{
    private static final String TF_DIR_PATH = "/storage/sdcard1";
    public TFPresenter(BaseExecuteView view) {
        super(view);
        if (task!=null){
            task.setmTaskId(SerialConstants.ITEM_TF);
        }
    }

    @Override
    public void setDirPath() {
        setPath(TF_DIR_PATH);
    }

    @Override
    protected void notifyStorageState(int state, String path, USBController controller) {
        LogTools.p(TAG,"state:"+state+",path:"+path+",controller:"+controller.toString());
        if (controller == this && TF_DIR_PATH.equals(path)) {
            notifyStatus(state);
        }
    }
}
