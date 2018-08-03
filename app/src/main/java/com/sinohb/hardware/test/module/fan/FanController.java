package com.sinohb.hardware.test.module.fan;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.logger.LogTools;


public class FanController extends BaseDisplayViewController implements FanPresenter.Controller {
    //private CMDSendListener sendListener;

//    static class CMDSendListener implements RFCSendListener {
//
//        private WeakReference<FanController> weakReference;
//
//        CMDSendListener(FanController controller) {
//            weakReference = new WeakReference<>(controller);
//        }
//
//        private boolean isNotNull() {
//            return weakReference != null && weakReference.get() != null && weakReference.get().task != null;
//        }
//
//        @Override
//        public void onSuccess(int NO, int id, String data) {
//            if (id == 0xC001 && isNotNull()) {
//                LogTools.p(TAG, "receiverData ok mNo=" + NO);
//                if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO) {
//                    ((FanTestTask) weakReference.get().task).notifyTestTurnOn();
//                } else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO) {
//                    ((FanTestTask) weakReference.get().task).notifyTestTurnOff();
//                }
//            }
//            LogTools.p(TAG, "onSuccess send success");
//        }
//
//        @Override
//        public void onFailure(int NO, int id) {
//            LogTools.p(TAG, "onFailure no:" + NO + ",id=" + id);
//            if (weakReference != null && weakReference.get() != null && weakReference.get().mView != null) {
//                if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO) {
//                    ((FanPresenter.View) weakReference.get().mView).notifyTurnOn();
//                } else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO) {
//                    ((FanPresenter.View) weakReference.get().mView).notifyTurnOff();
//                }
//            }
//        }
//
//    }

    public FanController(BaseDisplayViewView view) {
        super(view);
        init();
        //sendListener = new CMDSendListener(this);
    }

    @Override
    protected void init() {
        task = new FanTestTask(this);
    }

    @Override
    protected void onSuccess(int NO, int id, String data) {
        super.onSuccess(NO, id, data);
        if (id == 0xC001 && task != null) {
            LogTools.p(TAG, "receiverData ok mNo=" + NO);
            if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO) {
                ((FanTestTask) task).notifyTestTurnOn();
            } else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO) {
                ((FanTestTask) task).notifyTestTurnOff();
            }
        }
        LogTools.p(TAG, "onSuccess send success");
    }

    @Override
    protected void onFailure(int NO, int id) {
        super.onFailure(NO, id);
        LogTools.p(TAG, "onFailure no:" + NO + ",id=" + id);
        if (mView != null) {
            if (NO == SerialConstants.SERIAL_FAN_TURN_ON_NO) {
                ((FanPresenter.View) mView).notifyTurnOn();
            } else if (NO == SerialConstants.SERIAL_FAN_TURN_OFF_NO) {
                ((FanPresenter.View) mView).notifyTurnOff();
            }
        }
    }

    @Override
    public void turnOnFan() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_FAN_TURN_ON_NO, SerialConstants.ID_FAN, "{\"CommandArray\": [\"0x02\",\"0x01\",\"0x01\"]}", sendListener);
        RFCFactory.getInstance().sendMsg(c);
        if (mView!=null )
        ((FanPresenter.View) mView).notifyTurnOn();
    }

    @Override
    public void turnOffFan() {
        SerialCommand c = new SerialCommand(SerialConstants.SERIAL_FAN_TURN_OFF_NO, SerialConstants.ID_FAN, "{\"CommandArray\": [\"0x02\",\"0x01\",\"0x00\"]}", sendListener);
        RFCFactory.getInstance().sendMsg(c);
        if (mView!=null)
        ((FanPresenter.View) mView).notifyTurnOff();
    }

    @Override
    public void notifyTest() {
        if (mView != null) {
            ((FanPresenter.View) mView).notifyTest();
        }
    }


}
