package com.sinohb.hardware.test.module.rearview;

import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.logger.LogTools;


public class RearViewController extends BaseDisplayViewController implements RearViewPresenter.Controller {

    public RearViewController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new RearViewTestTask(this);
        //sendListener = new CMDSendListener(this);
    }

    @Override
    protected void onSuccess(int NO, int id, String data) {
        super.onSuccess(NO, id, data);
        LogTools.p(TAG, "onFailure no:" + NO + ",id=" + id);
        if (task != null) {
            if (id == 0xC001) {
                if (NO == SerialConstants.SERIAL_REAR_VIEW_START_NO) {
                    ((RearViewTestTask) task).notifyRearResult();
                } else if (NO == SerialConstants.SERIAL_REAR_VIEW_STOP_NO) {
                    ((RearViewTestTask) task).notifyStop();
                }
            }
            if (task.getmExecuteState() != BaseTestTask.STATE_RUNNING && NO == 1) {
                LogTools.p(TAG, "onSuccess no:" + NO + ",task.getmExecuteState()=" + task.getmExecuteState());
                stopRearView();
            }
        }
    }

    @Override
    protected void onFailure(int NO, int id) {
        super.onFailure(NO, id);
        LogTools.p(TAG, "onFailure no:" + NO + ",id=" + id);
        if (task != null) {
            ((RearViewTestTask) task).sendFailure();
        }
    }

    /**
     * 启动倒车后视
     */
    @Override
    public void startRearView() {
        sendCmd(SerialConstants.SERIAL_REAR_VIEW_START_NO, SerialConstants.ID_REAR_VIEW, "{\"CommandArray\": [\"0x01\",\"0x01\",\"0x01\"]}");
    }

    /**
     * 停止倒车后视
     */
    @Override
    public void stopRearView() {
        sendCmd(SerialConstants.SERIAL_REAR_VIEW_STOP_NO, SerialConstants.ID_REAR_VIEW, "{\"CommandArray\": [\"0x01\",\"0x01\",\"0x00\"]}");
    }

    private void sendCmd(int i, int idRearStop, String s) {
        SerialCommand c = new SerialCommand(i, idRearStop, s, sendListener);
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

}
