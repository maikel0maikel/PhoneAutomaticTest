package com.sinohb.hardware.test.module;

import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BasePresenter;
import com.sinohb.hardware.test.app.BaseView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.SerialCommand;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.module.frc.RFCFactory;
import com.sinohb.hardware.test.module.frc.RFCSendListener;
import com.sinohb.hardware.test.task.BaseTestTask;
import com.sinohb.hardware.test.task.ThreadPool;
import com.sinohb.logger.LogTools;

import java.util.List;
import java.util.concurrent.FutureTask;

public abstract class BaseController implements BasePresenter,RFCSendListener{
    protected static final String TAG = BaseExecuteController.class.getSimpleName();
    protected BaseTestTask task;
    protected BaseView mView;
    private int updateCount = 0;
    private int transactCount = 0;
    public BaseController(BaseView view) {
        mView = view;
        mView.setPresenter(this);
    }

    protected abstract void init();

    @Override
    public void start() {
        if (task != null && (task.getmExecuteState() == BaseTestTask.STATE_NONE || task.isFinish())) {
            task.setFinish(false);
            task.setmExecuteState(BaseTestTask.STATE_NONE);
            task.setManaul(true);
            FutureTask<Boolean> futureTask = new FutureTask(task);
            ThreadPool.getPool().execute(futureTask);
        }
    }

    @Override
    public void pause() {
        if (task != null) {
            task.pause();
        }
    }

    @Override
    public void stop() {
        if (task != null) {
            task.stopTask();
        }
    }

    @Override
    public BaseTestTask getTask() {
        return task;
    }

    @Override
    public void complete() {
        if (mView != null) {
            mView.complete(task);
        }
        updateResult();
        transactResult();
    }

    private void transactResult() {
        if (task == null){
            LogTools.p(TAG,"transactResult task is null");
            return;
        }
        List<StepEntity> stepEntities = task.getStepEntities();
        StringBuilder builder = new StringBuilder();
        builder.append("{\"ID\":").append(task.getmTaskId()).append(",").append("\"result\":").append("\"\n");
        if (stepEntities!=null){
            int i = 1;
            for (StepEntity entity:stepEntities){
                String pass = entity.getTestState()== Constants.TestItemState.STATE_SUCCESS?
                        HardwareTestApplication.getContext().getResources().getString(R.string.lable_pass): HardwareTestApplication.getContext().getResources().getString(R.string.label_un_pass);
                builder.append(i).append(".").append(entity.getStepTitle()).append("(").append(pass).append(")").append("\n");
                i++;
            }
        }
        builder.append("\"}");
        String data = builder.toString();
        LogTools.p(TAG,"单个测试结果：data:"+data);
        if (data.length()>0){
            SerialCommand c = new SerialCommand(SerialConstants.SERIAL_TRANSACT_RESULT_NO, SerialConstants.ID_TEST_DETIAL,
                    data, this);
            //RFCFactory.getInstance().sendMsg(c);
        }
        builder.setLength(0);
    }

    private void updateResult() {
        if (task == null){
            LogTools.p(TAG,"updateResult task is null");
            return;
        }
        if (task.isManaul()){
            SerialCommand c = new SerialCommand(SerialConstants.SERIAL_UPDATE_RESULT_NO, SerialConstants.ID_UPDATE_RESULT,
                    "{\"CommandArray\": [\""+task.getmTaskId()+"\",\""+task.isPass()+"\"]}", this);
           // RFCFactory.getInstance().sendMsg(c);
        }
    }

    @Override
    public void onSuccess(int NO, int id) {
        if (NO == SerialConstants.SERIAL_UPDATE_RESULT_NO){
            updateCount = 0;
        }else if (NO == SerialConstants.SERIAL_TRANSACT_RESULT_NO){
            transactCount = 0;
        }
    }

    @Override
    public void onFailure(int NO, int id) {
        if (NO == SerialConstants.SERIAL_UPDATE_RESULT_NO){
            updateCount++;
            if (updateCount<=3){
                updateResult();
            }else {
                updateCount = 0;
            }
        }else if (NO == SerialConstants.SERIAL_TRANSACT_RESULT_NO){
            transactCount++;
            if (transactCount<=3){
                transactResult();
            }else {
                transactCount = 0;
            }
        }
    }

    @Override
    public void destroy() {

    }
}
