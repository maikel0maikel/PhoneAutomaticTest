package com.sinohb.hardware.test.module.rgb;


import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.R;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.app.fragment.ScreenAdjustFragment;
import com.sinohb.hardware.test.app.fragment.ScreenRGBFragment;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.constant.SerialConstants;
import com.sinohb.hardware.test.entities.StepEntity;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.logger.LogTools;

import java.util.ArrayList;
import java.util.List;


public class RGBController extends BaseDisplayViewController {
    public RGBController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new RGBTask(this);
    }


//    @Override
//    public BaseTestTask getTask() {
//        return null;
//    }


    @Override
    public void complete() {
        addStepEntity();
        super.complete();
    }

    private void addStepEntity() {
        if (task!=null){
            ArrayList<StepEntity> stepEntities = task.getStepEntities();
            if ((stepEntities == null||stepEntities.isEmpty())&&mView!=null){
                stepEntities = new ArrayList<>();
                if (mView instanceof ScreenAdjustFragment){
                    LogTools.p(TAG,"complete mView is ScreenAdjustFragment");
                    StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_adjust_left_top), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_adjust_right_top), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity3 = new StepEntity(3, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_adjust_left_bottom), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity4 = new StepEntity(4, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_adjust_right_bottom), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity5 = new StepEntity(5, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_adjust_center), Constants.TestItemState.STATE_TESTING);
                    stepEntities.add(stepEntity1);
                    stepEntities.add(stepEntity2);
                    stepEntities.add(stepEntity3);
                    stepEntities.add(stepEntity4);
                    stepEntities.add(stepEntity5);
                    task.setStepEntities(stepEntities);
                }else if (mView instanceof ScreenRGBFragment){
                    LogTools.p(TAG,"complete mView is ScreenRGBFragment");
                    StepEntity stepEntity1 = new StepEntity(1, HardwareTestApplication.getContext().
                            getResources().getString( R.string.label_rgb_r), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity2 = new StepEntity(2, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_rgb_g), Constants.TestItemState.STATE_TESTING);
                    StepEntity stepEntity3 = new StepEntity(3, HardwareTestApplication.getContext().
                            getResources().getString(R.string.label_rgb_b), Constants.TestItemState.STATE_TESTING);
                    stepEntities.add(stepEntity1);
                    stepEntities.add(stepEntity2);
                    stepEntities.add(stepEntity3);
                    task.setStepEntities(stepEntities);
                }
            }
            if (task.getmTaskId() == 0){
                if (mView instanceof ScreenRGBFragment){
                    task.setmTaskId(SerialConstants.ITEM_SCREEN);
                }else if (mView instanceof ScreenAdjustFragment){
                    task.setmTaskId(SerialConstants.ITEM_TOUCH);
                }
            }
        }
    }
}
