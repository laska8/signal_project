package com.cardiogenerator.outputs;

public interface OutputStrategy {
    void output(int patientId, long timeStamp, String label, String data);
}
