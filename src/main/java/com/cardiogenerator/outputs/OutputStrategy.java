package com.cardiogenerator.outputs;
/**
 * This class is an interface.
 * All the different output strategies classes will implement this interface.
 * The method in OutputStrategy will be used to write data into files, console, tcp or web socket.
 */
public interface OutputStrategy {
    /**
     * Outputs patient data.
     *
     * @param patientId The ID of the patient.
     * @param timeStamp The timestamp of the data.
     * @param label The label of the data.
     * @param data The data to be output.
     */
    void output(int patientId, long timeStamp, String label, String data);
}
