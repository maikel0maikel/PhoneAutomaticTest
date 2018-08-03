package com.sinohb.hardware.test.module.serial;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.logger.LogTools;

import org.json.JSONException;
import org.json.JSONObject;


public class SerialController extends BaseDisplayViewController implements SerialPresenter.Controller {
    private static final String TAG = SerialController.class.getSimpleName();

    @Override
    protected void onSuccess(int NO, int id, String data) {
        LogTools.p(TAG, "onSuccess id:" + id);
        if (id == 0x8601) {
            notifyResult(data);
            if (task != null) {
                ((SerialTask) task).sendOk();
            }
        }
        super.onSuccess(NO, id, data);
    }

    @Override
    protected void onFailure(int NO, int id) {
        LogTools.e(TAG, "onFailure");
        if (task != null) {
            ((SerialTask) task).sendFailure();
        }
        super.onFailure(NO, id);
    }

    public SerialController(BaseDisplayViewView view) {
        super(view);
        init();
        // sendListener = new CMDSendListener(this);
    }


    @Override
    protected void init() {
        task = new SerialTask(this);
    }


    @Override
    public void displayView() {

    }

    @Override
    public void getVersion() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_VERSION_NO, SerialConstants.ID_VERSION, "{\"CommandArray\": [\"0x0802\"]}", sendListener);
        RFCFactory.getInstance().sendMsg(c);
    }

    private void notifyResult(String result) {
//        if (task != null) {
//            ((SerialTask) task).notifyResult();
//        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            String version = jsonObject.getString("0x0802");
            if (mView != null) {
                ((SerialPresenter.View) mView).notifyVersion(version);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogTools.e(TAG, e, "parse eror " + result);
        }
    }


}
