package com.sinohb.hardware.test.module.auxiliary;

import com.marsir.vehicle.VehicleListener;
import com.marsir.vehicle.VehicleManager;
import com.sinohb.hardware.test.app.BaseDisplayViewView;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.hardware.test.module.BaseDisplayViewController;
import com.sinohb.logger.LogTools;

public class AuxController extends BaseDisplayViewController implements AuxPresenter.Controller {
    private VehicleManagerable auxManager;
    private AuxStateListener listener;

    public AuxController(BaseDisplayViewView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        auxManager = new VehicleTestManager();
        listener = new AuxStateListener();
        auxManager.addVehicleListener(listener);
        task = new AuxTestTask(this);
    }

    @Override
    public int getAuxStatus() {
        return auxManager == null ? Constants.DEVICE_NOT_SUPPORT : auxManager.getAuxStatus();
    }

    @Override
    public void notifyAuxStatus(int state) {
        if (mView != null) {
            ((AuxPresenter.View) mView).notifyAuxStatusView(state);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (auxManager != null&& listener != null) {
            auxManager.removeVehicleListener(listener);
        }
    }

    @Override
    public void displayView() {

    }

    class AuxStateListener extends VehicleListener {
        @Override
        public void onExtendDataInfoChanged(int id, int value) {
            super.onExtendDataInfoChanged(id, value);
            LogTools.p(TAG, "onExtendDataInfoChanged id=" + id + ",value=" + value);
            if (task != null && id == 0) {
                notifyAuxStatus(value);
                if (value == 1) {
                    ((AuxTestTask) task).notifyAuxInsert();
                }
            }
        }

        @Override
        public void onEnvironTemperatureChanged(VehicleManager.EnvironmentTemperature type, int value) {
            super.onEnvironTemperatureChanged(type, value);
            LogTools.p(TAG, "onExtendDataInfoChanged type=" + type.name() + ",value=" + value);
        }
    }
}
