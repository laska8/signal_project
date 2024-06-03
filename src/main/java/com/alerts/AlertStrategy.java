package com.alerts;

import com.data_management.Patient;

public interface AlertStrategy {

    /**
     * Checks the alert for a specific given patient
     * @param patient
     */
    public void checkAlert(Patient patient);
}
