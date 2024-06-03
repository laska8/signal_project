package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;

public class HeartRateStrategy extends AlertGenerator implements AlertStrategy{

    private DataStorage dataStorage;


    public HeartRateStrategy(DataStorage dataStorage) {
        super(dataStorage);
        this.dataStorage = dataStorage;
    }

    @Override
    public void checkAlert(Patient patient) {

        super.checkHeartRate(patient);
        super.irregularBeat(patient);
    }


}
