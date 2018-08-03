package com.sinohb.hardware.test.module.temperature;

import com.marsir.vehicle.VehicleListener;
import com.marsir.vehicle.VehicleManager;
import com.sinohb.hardware.test.app.BaseExecuteView;
import com.sinohb.hardware.test.module.BaseExecuteController;
import com.sinohb.hardware.test.module.auxiliary.VehicleManagerable;
import com.sinohb.hardware.test.module.auxiliary.VehicleTestManager;
import com.sinohb.logger.LogTools;

public class TemperatureController extends BaseExecuteController implements TemperaturePresenter.Controller {
    private VehicleManagerable vehicleManager;
    private TemperatureStateListener listener;

    public TemperatureController(BaseExecuteView view) {
        super(view);
        init();
    }

    @Override
    protected void init() {
        task = new TemperatureTask(this);
        vehicleManager = new VehicleTestManager();
        listener = new TemperatureStateListener();
        vehicleManager.addVehicleListener(listener);
    }

    @Override
    public int getTemperature() {
        int temperature ;
        temperature = vehicleManager == null ? 0 : vehicleManager.getTemperature();
        if (mView != null) {
            ((TemperaturePresenter.View) mView).notifyTemperature(temperature);
        }
        return temperature;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (vehicleManager != null && listener != null) {
            vehicleManager.removeVehicleListener(listener);
            listener = null;
        }
    }

   static class TemperatureStateListener extends VehicleListener {
        @Override
        public void onExtendDataInfoChanged(int id, int value) {
            super.onExtendDataInfoChanged(id, value);
            LogTools.p(TAG, "onExtendDataInfoChanged id=" + id + ",value=" + value);
//            if (task != null && id == 2) {
//
//            }
        }

        @Override
        public void onEnvironTemperatureChanged(VehicleManager.EnvironmentTemperature type, int value) {
            super.onEnvironTemperatureChanged(type, value);
            LogTools.p(TAG, "onExtendDataInfoChanged type=" + type.name() + ",value=" + value);
        }
    }

//    @Override
//    public BaseTestTask getTask() {
//        return task;
//    }
}
