package com.sinohb.hardware.test.module.fan;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.frc.RFCController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.frc.RFCSendListener;
import com.sinohb.logger.LogTools;

public class FanController extends RFCController implements FanPresenter.Controller {
    private int mNo = 0;

    public FanController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new FanTestTask(this);
    }

    @Override
    public void start() {
        super.start();
        mNo = 0;
    }

    @Override
    protected void receiverData(int no, int id, String msg, RFCSendListener listener) {
        if (listener == FanController.this&&id == 0xC001){
            LogTools.p(TAG,"receiverData ok mNo="+mNo);
            if (mNo == SerialConstants.SERIAL_FAN_TURN_ON_NO){
                ((FanTestTask)task).notifyTestTurnOn();
            }else if (mNo == SerialConstants.SERIAL_FAN_TURN_OFF_NO){
                ((FanTestTask)task).notifyTestTurnOff();
            }
        }
    }

    @Override
    public void turnOnFan() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_FAN_TURN_ON_NO, SerialConstants.ID_FAN,"{\"CommandArray\": [\"0x02\",\"0x01\",\"0x01\"]}",this);
        RFCFactory.getInstance().sendMsg(c);
        mNo = SerialConstants.SERIAL_FAN_TURN_ON_NO;
    }

    @Override
    public void turnOffFan() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_FAN_TURN_OFF_NO, SerialConstants.ID_FAN,"{\"CommandArray\": [\"0x02\",\"0x01\",\"0x00\"]}",this);
        RFCFactory.getInstance().sendMsg(c);
        mNo = SerialConstants.SERIAL_FAN_TURN_OFF_NO;
    }

    @Override
    public void notifyTest() {
        if (mView!=null){
            ((FanPresenter.View)mView).notifyTest();
        }
    }

    @Override
    public void onSuccess(int NO, int id) {
        if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO){
            ((FanPresenter.View)mView).notifyTurnOn();
        }else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO){
            ((FanPresenter.View)mView).notifyTurnOff();
        }
        LogTools.p(TAG,"onSuccess send success");
    }

    @Override
    public void onFailure(int NO, int id) {
        if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO){
            ((FanPresenter.View)mView).notifyTurnOn();
        }else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO){
            ((FanPresenter.View)mView).notifyTurnOff();
        }
        LogTools.p(TAG,"onFailure send fail");
    }

}
