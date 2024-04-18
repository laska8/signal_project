package com.cardiogenerator.generators;

import java.util.Random;

import com.cardiogenerator.outputs.OutputStrategy;
/**
 * This class is responsible for generating blood saturation for a group of patients.
 * It overrides the method generate that can ensure the generation of blood saturation in a realistic range.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;
    /**
     * Constructs a BloodSaturationDataGenerator with the certain number of patients.
     *
     * @param patientCount The number of patients.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }
    /**
     * Generates blood saturation data for a certain patient.
     *
     * @param patientId The ID of the patient.
     * @param outputStrategy The output strategy to use for the generated data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
