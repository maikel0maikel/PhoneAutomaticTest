package com.sinohb.hardware.test.module.knob;


import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.module.key.KeyController;

public class KnobController extends KeyController {
    public KnobController(BaseDisplayViewView view) {
        super(view);
    }

    @Override
    protected void init() {
        if (task ==null){
            task = new KnobTestTask(this);
        }

    }

    @Override
    public void notifyPressKey(int keyCode) {
        if (task != null) {
            ((KnobTestTask) task).notifyTestKey(keyCode);
        }
    }
}
