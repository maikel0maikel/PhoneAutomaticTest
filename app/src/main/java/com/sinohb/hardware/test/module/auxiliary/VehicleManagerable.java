package com.sinohb.hardware.test.module.auxiliary;

import com.marsir.vehicle.VehicleListener;

public interface VehicleManagerable {

    int getAuxStatus();

    int getTemperature();

    boolean isEnable();

    void addVehicleListener(VehicleListener listener);

    void removeVehicleListener(VehicleListener listener);
}
