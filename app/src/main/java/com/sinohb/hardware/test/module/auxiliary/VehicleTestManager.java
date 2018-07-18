package com.sinohb.hardware.test.module.auxiliary;


import com.marsir.vehicle.VehicleListener;
import com.marsir.vehicle.VehicleManager;
import com.sinohb.hardware.test.HardwareTestApplication;
import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;

public class VehicleTestManager implements VehicleManagerable {
    private static final String TAG = VehicleTestManager.class.getSimpleName();
    private VehicleManager vehicleManager;

    public VehicleTestManager() {
        vehicleManager = (VehicleManager) HardwareTestApplication.getContext().getSystemService("vehicle");
    }

    @Override
    public int getAuxStatus() {
        return vehicleManager == null ? Constants.DEVICE_NOT_SUPPORT : vehicleManager.getExtendInfo(0);
    }

    @Override
    public int getTemperature() {
        return vehicleManager == null ? Constants.DEVICE_NOT_SUPPORT : vehicleManager.getExtendInfo(2);
    }


    @Override
    public boolean isEnable() {
        return vehicleManager != null;
    }

    @Override
    public void addVehicleListener(VehicleListener listener) {
        if (isEnable()){
            vehicleManager.addVehicleListener(listener);
        }else {
            LogTools.e(TAG,"ve");
        }
    }

    @Override
    public void removeVehicleListener(VehicleListener listener) {
        if (isEnable()){
            vehicleManager.removeVehicleListener(listener);
        }
    }


}
