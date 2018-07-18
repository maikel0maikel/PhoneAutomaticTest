package com.sinohb.hardware.test.module.serial;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.frc.RFCController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.frc.RFCSendListener;
import com.sinohb.logger.LogTools;

import org.json.JSONException;
import org.json.JSONObject;

public class SerialController extends RFCController implements SerialPresenter.Controller {
    private static final String TAG = SerialController.class.getSimpleName();

    public SerialController(BaseDisplayViewView view) {
        super(view);
        init();
    }


    @Override
    protected void init() {
        task = new SerialTask(this);
    }

    @Override
    protected void receiverData(int no, int id, String msg, RFCSendListener listener) {
        if (listener == SerialController.this && id == 0x8601 && msg.contains("0802")) {
            LogTools.p(TAG,"receiverData ok");
            notifyResult(msg);
        }
    }

    @Override
    public void displayView() {

    }

    @Override
    public void getVersion() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_VERSION_NO, SerialConstants.ID_VERSION, "{\"CommandArray\": [\"0x0802\"]}", this);
        RFCFactory.getInstance().sendMsg(c);
    }

    private void notifyResult(String result) {
        if (task != null) {
            ((SerialTask) task).notifyResult();
        }
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


    @Override
    public void onSuccess(int NO, int id) {
        LogTools.p(TAG, "onSuccess");
        if (task != null) {
            ((SerialTask) task).sendOk();
        }
    }

    @Override
    public void onFailure(int NO, int id) {
        LogTools.e(TAG, "onFailure");
        if (task != null) {
            ((SerialTask) task).sendFailure();
        }
    }

}
