package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;

public class OxygenSaturationStrategy extends AlertGenerator implements AlertStrategy{

    private DataStorage dataStorage;
    public OxygenSaturationStrategy(DataStorage dataStorage) {
        super(dataStorage);
        this.dataStorage = dataStorage;
    }
    @Override
    public void checkAlert(Patient patient) {

        super.checkLowSaturation(patient);
        super.checkRapidDrop(patient);
    }
}
