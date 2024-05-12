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
    public static PatientRecord previous;
    public static int count=0;

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
        if (patient==null){
            System.out.println("Patient data is null");
            return;
        }
        //blood pressure
        checkSystolicPressure(patient);
        checkDiastolicPressure(patient);
        checkBloodPressure(patient);

        //blood saturation
        checkLowSaturation(patient);
        checkRapidDrop(patient);

        //ECG
        checkHeartRate(patient);
        irregularBeat(patient);
        //Hypotensive Hypoxemia Alert
        hypotensiveHypoxemiaAlert(patient);
    }

    /**
     * This method checks for hypotensive hypoxemia conditions in the patient's records and triggers an alert
     * if both blood pressure and blood oxygen saturation are below certain thresholds.
     * @param patient
     */
    public void hypotensiveHypoxemiaAlert(Patient patient) {
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
            }
        }

    }

    /**
     * This method checks the systolic blood pressure of the patient and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public void checkSystolicPressure(Patient patient){
        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("SystolicPressure")) {
                if (rec.getMeasurementValue() >= 180 || rec.getMeasurementValue() <= 90) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert systolic = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(systolic);
                }
            }
        }
    }

    /**
     * This method checks the diastolic blood pressure of the patient and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public void checkDiastolicPressure(Patient patient){
        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("DiastolicPressure")) {
                if (rec.getMeasurementValue() >= 120 || rec.getMeasurementValue() <= 60) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert diastolic = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(diastolic);
                }
            }
        }
    }

    /**
     * This method checks for significant changes in the patient's blood pressure readings over a short period
     * and triggers an alert if such changes are detected.
     * @param patient
     */
    public void checkBloodPressure(Patient patient){
        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if(rec.getRecordType().equals("SystolicPressure") || rec.getRecordType().equals("DiastolicPressure")){

                if(count==0){
                    count++;
                    previous=rec;
                } else {
                    if (count != 3) {
                        if (Math.abs(previous.getMeasurementValue() - rec.getMeasurementValue()) >= 10 ||
                                Math.abs(previous.getMeasurementValue() - rec.getMeasurementValue()) <= 10) {
                            count++;
                            previous=rec;

                        }
                    }
                }

                if(count==3){
                    String id = String.valueOf(rec.getPatientId());
                    Alert bloodPressure = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(bloodPressure);
                    count=0;
                }
            }
        }
    }

    /**
     * This method checks for low blood oxygen saturation levels in the patient's records
     * and triggers an alert if it falls below a certain threshold.
     * @param patient
     */
    public void checkLowSaturation(Patient patient){
        for(int i=0; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("Saturation")) {
                if (rec.getMeasurementValue() <=92) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert lowSaturation = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(lowSaturation);
                }
            }
        }
    }

    /**
     * This method checks for rapid drops in blood oxygen saturation levels over a short period
     * and triggers an alert if such drops are detected.
     * @param patient
     */
    public void checkRapidDrop(Patient patient){

        List<Double> timer=new ArrayList<Double>();
        List<Double> values=new ArrayList<Double>();
        int initial=0;
        double passedTime=0;
        double sumDrops=0;
        PatientRecord prev=patient.patientRecords.get(0);

        for(int i=1; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("Saturation")) {

                double diffTime=rec.getTimestamp()- prev.getTimestamp();
                passedTime=passedTime+ diffTime;

                double diffMeasure=prev.getMeasurementValue()-rec.getMeasurementValue();
                sumDrops=sumDrops+diffMeasure;

                if(sumDrops<5){

                    //in miliseconds
                    if(passedTime>=600000){

                        passedTime=passedTime-timer.get(initial);
                        sumDrops=sumDrops+values.get(initial);
                        initial++;
                    }

                    timer.add(diffTime);
                    values.add(diffMeasure);

                } else{
                    String id = String.valueOf(rec.getPatientId());
                    Alert rapidDrop = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(rapidDrop);
                }

                prev=rec;
            }
        }
    }

    /**
     * This method checks the patient's heart rate derived from ECG readings and triggers an alert if it falls outside the normal range.
     * @param patient
     */
    public void checkHeartRate(Patient patient){

        PatientRecord pr=patient.patientRecords.get(0);

        for(int i=1; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("ECG")) {

                double timeInterval=rec.getTimestamp()-pr.getTimestamp();
                double heartRate=60000/timeInterval;

                if (heartRate <=50 || heartRate >= 100) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert abnormalHeartRate = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(abnormalHeartRate);
                }
            }
        }
    }

    /**
     * This method checks for irregular heartbeats in the patient's ECG readings
     * and triggers an alert if irregularities are detected.
     * @param patient
     */

    public void irregularBeat(Patient patient){

        PatientRecord pr=patient.patientRecords.get(0);

        for(int i=1; i<patient.patientRecords.size(); i++) {
            PatientRecord rec = patient.patientRecords.get(i);

            if (rec.getRecordType().equals("ECG")) {

                double timeInterval=rec.getTimestamp()-pr.getTimestamp();

                if (timeInterval <=24 || timeInterval >= 62) {
                    String id = String.valueOf(rec.getPatientId());
                    Alert irregularBeat = new Alert(id, rec.getRecordType(), rec.getTimestamp());
                    triggerAlert(irregularBeat);
                }
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private static void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
        System.out.print("Alert triggered: " + alert.getCondition() + " for patient " + alert.getPatientId() + "at" + alert.getTimestamp());
    }
}
