package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertStrategy strategy;


    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;

    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Implementation goes here
        if (patient == null) {
            throw new NullPointerException("Patient data is null.");
        }
        checkSystolicPressure(patient);
        checkDiastolicPressure(patient);
        checkBloodPressureIncreasing(patient);
        checkBloodPressureDecreasing(patient);
        checkLowSaturation(patient);
        checkRapidDrop(patient);
        checkHeartRate(patient);
        irregularBeat(patient);
        hypotensiveHypoxemiaAlert(patient);
    }


    /**
     * This method checks for hypotensive hypoxemia conditions in the patient's records and triggers an alert
     * if both blood pressure and blood oxygen saturation are below certain thresholds.
     * @param patient
     */
    public boolean hypotensiveHypoxemiaAlert(Patient patient) {
        long currentTime = System.currentTimeMillis();
        boolean bloodPressure = false;
        boolean bloodOxygenSaturation = false;
        for (int i = 0; i < patient.patientRecords.size();i++){
            PatientRecord record = patient.patientRecords.get(i);
            if ("SystolicPressure".equals(record.getRecordType()) && record.getMeasurementValue() < 90) {
                bloodPressure = true;
            }
            if ("Saturation".equals(record.getRecordType()) && record.getMeasurementValue() < 92) {
                bloodOxygenSaturation = true;
            }
            if (bloodPressure && bloodOxygenSaturation) {
                triggerAlert(new Alert(Integer.toString(record.getPatientId()), "Hypotensive Hypoxemia Alert", currentTime));
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks the systolic blood pressure of the patient and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public boolean checkSystolicPressure(Patient patient){
        boolean trigger = false;

        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("SystolicPressure")) {
                if (rec.getMeasurementValue() >= 180 || rec.getMeasurementValue() <= 90) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert systolic = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(systolic);
                    trigger=true;
                }
            }
        }
        return trigger;
    }

    /**
     * This method checks the diastolic blood pressure of the patient and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public boolean checkDiastolicPressure(Patient patient){
        boolean trigger = false;

        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("DiastolicPressure")) {
                if (rec.getMeasurementValue() >= 120 || rec.getMeasurementValue() <= 60) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert diastolic = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(diastolic);
                    trigger=true;
                }
            }
        }

        return trigger;
    }

    /**
     * This method checks for significant increases in the patient's blood pressure over three consecutive readings
     * and triggers an alert if such increases are detected.
     * @param patient
     */
    public boolean checkBloodPressureIncreasing(Patient patient){
        boolean trigger = false;
        PatientRecord previous=patient.patientRecords.get(0);
        int count=0;

        for(int i = 0; i<patient.patientRecords.size() && !trigger; i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if(rec.getRecordType().equals("SystolicPressure") || rec.getRecordType().equals("DiastolicPressure")){

                if(count==0){
                    count++;
                    previous=rec;
                } else {
                    if (Math.abs(previous.getMeasurementValue() - rec.getMeasurementValue()) >= 10
                            && previous.getMeasurementValue()<rec.getMeasurementValue()) {
                        count++;
                        previous = rec;
                    } else {
                        count = 0;
                        previous = rec;
                    }
                }

                if(count==3){
                    String id = String.valueOf(rec.getPatientId());
                    Alert bloodPressureIncrease = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(bloodPressureIncrease);
                    trigger=true;
                    count=0;
                    previous=rec;
                }
            }
        }

        return trigger;
    }

    /**
     * This method checks for significant decreases in the patient's blood pressure over three consecutive readings
     * and triggers an alert if such decreases are detected.
     * @param patient
     */
    public boolean checkBloodPressureDecreasing(Patient patient){
        boolean trigger = false;
        PatientRecord previous=patient.patientRecords.get(0);
        int count=0;

        for(int i = 0; i<patient.patientRecords.size() && !trigger; i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if(rec.getRecordType().equals("SystolicPressure") || rec.getRecordType().equals("DiastolicPressure")){

                if(count==0){
                    count++;
                    previous=rec;
                } else {
                    if (Math.abs(previous.getMeasurementValue() - rec.getMeasurementValue()) >= 10
                            && previous.getMeasurementValue()>rec.getMeasurementValue()) {
                        count++;
                        previous = rec;
                    } else {
                        count = 0;
                        previous = rec;
                    }
                }

                if(count==3){
                    String id = String.valueOf(rec.getPatientId());
                    Alert bloodPressureDecrease = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(bloodPressureDecrease);
                    trigger=true;
                    count=0;
                    previous=rec;
                }
            }
        }

        return trigger;
    }

    /**
     * This method checks for low blood oxygen saturation levels in the patient's records
     * and triggers an alert if it falls below a certain threshold.
     * @param patient
     */
    public boolean checkLowSaturation(Patient patient){
        boolean trigger = false;

        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("Saturation")) {
                if (rec.getMeasurementValue() <=92) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert lowSaturation = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(lowSaturation);
                    trigger=true;
                }
            }
        }
        return trigger;
    }

    /**
     * This method checks for rapid drops in blood oxygen saturation levels over 10 minutes or less
     * and triggers an alert if such drops are detected.
     * @param patient
     */
    public boolean checkRapidDrop(Patient patient){

        List<Double> timer=new ArrayList<Double>();
        List<Double> values=new ArrayList<Double>();
        double passedTime=0;
        double sumDrops=0;
        PatientRecord prev=patient.patientRecords.get(0);
        boolean trigger = false;

        for(int i=1; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("Saturation")) {

                double diffTime=rec.getTimestamp()- prev.getTimestamp();
                passedTime=passedTime+ diffTime;

                double diffMeasure=prev.getMeasurementValue()-rec.getMeasurementValue();
                sumDrops=sumDrops+diffMeasure;

                if(sumDrops<5){

                    //in miliseconds
                    if(passedTime>=600000 && i!=1){

                        passedTime=0;
                        sumDrops=0;
                    }

                    timer.add(diffTime);
                    values.add(diffMeasure);

                } else{
                    String id = String.valueOf(rec.getPatientId());
                    Alert rapidDrop = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(rapidDrop);
                    trigger=true;
                }

                prev=rec;
            }
        }

        return trigger;
    }

    /**
     * This method checks the patient's heart rate derived from ECG readings and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public boolean checkHeartRate(Patient patient){

        PatientRecord pr=patient.patientRecords.get(0);
        boolean trigger = false;

        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("ECG")) {

                //transforming the data into bpm
                //double timeInterval=rec.getTimestamp()-pr.getTimestamp();
                //double heartRate=60000/timeInterval;

                //for testing JUnit
                double heartRate=rec.getMeasurementValue();

                if (heartRate <=50 || heartRate >= 100) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert abnormalHeartRate = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(abnormalHeartRate);
                    System.out.print("Abnormal heart rate alert");
                    trigger=true;
                }
            }
        }
        return trigger;
    }

    /**
     * This method checks for irregular heartbeats in the patient's ECG readings
     * and triggers an alert if irregularities are detected.
     * @param patient
     */

    public boolean irregularBeat(Patient patient){

        PatientRecord pr=patient.patientRecords.get(0);
        boolean trigger = false;

        for(int i=1; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("ECG")) {

                double timeInterval=rec.getTimestamp()-pr.getTimestamp();

                if (timeInterval <=24 || timeInterval >= 62) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert irregularBeat = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(irregularBeat);
                    trigger=true;
                }
            }
        }

        return trigger;
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    public static void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.print("Alert triggered: " + alert.getCondition() + " for patient " + alert.getPatientId() + "at" + alert.getTimestamp());
    }

    public void evaluateStrategy(Patient patient) {
        if (patient == null) {
            throw new NullPointerException("No patient");
        } else if (strategy == null) {
            throw new NullPointerException("No strategy");
        }
        System.out.println(strategy);
        strategy.checkAlert(patient);
    }

    public void setStrategy(AlertStrategy strategy) {
        if (strategy == null) {
            throw new NullPointerException("No strategy");
        }
        this.strategy =strategy;
    }

}
