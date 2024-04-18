package com.cardiogenerator.generators;

import com.cardiogenerator.outputs.OutputStrategy;
/**
 * This class is an interface.
 * All the medical data generators will implement this interface.
 * The method in PatientDataGenerator will be used to generate various medical data for a specific patient.
 */
public interface PatientDataGenerator {
    /**
     * Generates data for a specific patient.
     *
     * @param patientId The ID of the patient.
     * @param outputStrategy The output strategy to use for the generated data.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
