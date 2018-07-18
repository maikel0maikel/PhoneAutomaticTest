package com.sinohb.hardware.test.module.rearview;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.frc.RFCController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.frc.RFCSendListener;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;

public class RearViewController extends RFCController implements RearViewPresenter.Controller {


    public RearViewController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new RearViewTestTask(this);
    }

    @Override
    public void startRearView() {
        sendCmd(SerialConstants.SERIAL_REAR_VIEW_START_NO, SerialConstants.ID_REAR_VIEW, "{\"CommandArray\": [\"0x01\",\"0x01\",\"0x01\"]}");
    }

    @Override
    public void stopRearView() {
        sendCmd(SerialConstants.SERIAL_REAR_VIEW_STOP_NO, SerialConstants.ID_REAR_VIEW, "{\"CommandArray\": [\"0x01\",\"0x01\",\"0x00\"]}");
    }

    private void sendCmd(int i, int idRearStop, String s) {
        SerialCommand c = new SerialCommand(i, idRearStop, s, this);
        RFCFactory.getInstance().sendMsg(c);
    }

    @Override
    public void notifyRearViewStart() {
        if (mView != null) {
            ((RearViewPresenter.View) mView).notifyRearViewStart();
        }
    }

    @Override
    public void notifyRearViewStop() {
        if (mView != null) {
            ((RearViewPresenter.View) mView).notifyRearViewStop();
        }
    }

    @Override
    protected void receiverData(int no, int id, String msg, RFCSendListener listener) {
        if (id == 0xC001 && listener == RearViewController.this) {
            ((RearViewTestTask) task).notifyRearResult();
        }
    }

    @Override
    public void onSuccess(int NO, int id) {
        if (task != null && task.getmExecuteState() != BaseTestTask.STATE_RUNNING && NO == 1) {
            LogTools.p(TAG, "onSuccess no:" + NO + ",task.getmExecuteState()=" + task.getmExecuteState());
           stopRearView();
        }
    }

    @Override
    public void onFailure(int NO, int id) {
        LogTools.p(TAG, "onFailure no:" + NO + ",id=" + id);
        if (task != null) {
            ((RearViewTestTask) task).sendFailure();
        }
    }
//
//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }
}
