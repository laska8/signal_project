package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;


public class BloodPressureStrategy extends AlertGenerator implements AlertStrategy{

    private DataStorage dataStorage;
    public BloodPressureStrategy(DataStorage dataStorage) {
        super(dataStorage);
        this.dataStorage = dataStorage;
    }
    @Override
    public void checkAlert(Patient patient) {

        super.checkSystolicPressure(patient);
        super.checkDiastolicPressure(patient);
        super.checkBloodPressureIncreasing(patient);
        super.checkBloodPressureDecreasing(patient);

    }
}
