package alerts;

import com.Main.Main;
import com.alerts.*;
import com.alerts.decorator.PriorityAlertDecorator;
import com.alerts.decorator.RepeatedAlertDecorator;
import com.cardiogenerator.HealthDataSimulator;
import com.data_management.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlertGeneratorTest {
    private AlertGenerator alertGenerator;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;


    private long currentTime;
    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        currentTime = System.currentTimeMillis();
    }
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testCheckSystolicPressure(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(190,"SystolicPressure", 1714376789050L);
        assertEquals(true, generator.checkSystolicPressure(person1)); //trigger alert

        Patient person2=new Patient(2);
        person2.addRecord(80,"SystolicPressure", 1714376789050L);
        assertEquals(true, generator.checkSystolicPressure(person1)); //trigger alert

        Patient person3=new Patient(3);
        person3.addRecord(100,"SystolicPressure", 1714376789050L);
        person3.addRecord(110,"SystolicPressure", 1714376789050L);
        person3.addRecord(120,"SystolicPressure", 1714376789050L);
        assertEquals(false, generator.checkSystolicPressure(person3)); //everything in order
    }

    @Test
    void testCheckDiastolicPressure(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(130,"DiastolicPressure", 1714376789050L);
        assertEquals(true, generator.checkDiastolicPressure(person1)); //trigger alert

        Patient person2=new Patient(2);
        person2.addRecord(50,"DiastolicPressure", 1714376789050L);
        assertEquals(true, generator.checkDiastolicPressure(person1)); //trigger alert

        Patient person3=new Patient(3);
        person3.addRecord(100,"DiastolicPressure", 1714376789050L);
        person3.addRecord(110,"DiastolicPressure", 1714376789050L);
        person3.addRecord(90,"DiastolicPressure", 1714376789050L);
        assertEquals(false, generator.checkDiastolicPressure(person3)); //everything in order
    }


    @Test
    void testCheckBloodPressureIncreasing(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(70,"DiastolicPressure", 1714376789050L);
        person1.addRecord(90,"DiastolicPressure", 1714376789050L);
        person1.addRecord(110,"DiastolicPressure", 1714376789050L);

        assertEquals(true, generator.checkBloodPressureIncreasing(person1));

        Patient person2=new Patient(2);
        person2.addRecord(70,"DiastolicPressure", 1714376789050L);
        person2.addRecord(75,"DiastolicPressure", 1714376789050L);
        person2.addRecord(80,"DiastolicPressure", 1714376789050L);

        assertEquals(false, generator.checkBloodPressureIncreasing(person2));
    }

    @Test
    void testCheckBloodPressureDecreasing(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(100,"DiastolicPressure", 1714376789050L);
        person1.addRecord(90,"DiastolicPressure", 1714376789050L);
        person1.addRecord(80,"DiastolicPressure", 1714376789050L);

        assertEquals(true, generator.checkBloodPressureDecreasing(person1));

        Patient person2=new Patient(2);
        person2.addRecord(90,"DiastolicPressure", 1714376789050L);
        person2.addRecord(85,"DiastolicPressure", 1714376789050L);
        person2.addRecord(80,"DiastolicPressure", 1714376789050L);

        assertEquals(false, generator.checkBloodPressureDecreasing(person2));
    }

    @Test
    void testCheckLowSaturation(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        Patient person=new Patient(1);

        person.addRecord(80,"Saturation", 1714376789050L);

        assertEquals(true, generator.checkLowSaturation(person));
    }

    @Test
    void testCheckRapidDrop(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(95,"Saturation", 100);
        person1.addRecord(89,"Saturation", 600100);
        assertEquals(true, generator.checkRapidDrop(person1));

        Patient person2=new Patient(2);
        person2.addRecord(95,"Saturation", 100);
        person2.addRecord(92,"Saturation", 600000);
        person2.addRecord(89,"Saturation", 600100);
        assertEquals(true, generator.checkRapidDrop(person2));

        Patient person3=new Patient(3);
        person3.addRecord(95,"Saturation", 100);
        person3.addRecord(92,"Saturation", 600000);
        assertEquals(false, generator.checkRapidDrop(person3));

        Patient person4=new Patient(4);
        person4.addRecord(95,"Saturation", 100);
        person4.addRecord(92,"Saturation", 600100);
        assertEquals(false, generator.checkRapidDrop(person4));

        Patient person5=new Patient(5);
        person5.addRecord(95,"Saturation", 100);
        person5.addRecord(92,"Saturation", 600100);
        person5.addRecord(94,"Saturation", 600400);
        person5.addRecord(93,"Saturation", 600600);
        assertEquals(false, generator.checkRapidDrop(person5));
}

    @Test
    void testCheckHeartRate(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(40,"ECG", 1714376789050L);
        assertEquals(true, generator.checkHeartRate(person1));

        Patient person2=new Patient(2);
        person2.addRecord(110,"ECG", 1714376789050L);
        assertEquals(true, generator.checkHeartRate(person2));
    }

    @Test
    void testIrregularBeat(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(70,"ECG", 10);
        person1.addRecord(70,"ECG", 80);
        assertEquals(true, generator.irregularBeat(person1));

        Patient person2=new Patient(2);
        person2.addRecord(70,"ECG", 10);
        person2.addRecord(70,"ECG", 15);
        person2.addRecord(70,"ECG", 20);
        assertEquals(true, generator.irregularBeat(person2));

        Patient person3=new Patient(3);
        person3.addRecord(70,"ECG", 10);
        person3.addRecord(70,"ECG", 50);
        assertEquals(false, generator.irregularBeat(person3)); //everything in order
    }
    @Test
    void hypotensiveHypoxemiaAlert(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(60,"Saturation", 1714376789050L);
        person1.addRecord(70,"SystolicPressure", 1714376789050L);
        assertEquals(true, generator.hypotensiveHypoxemiaAlert(person1));
        Patient person2 = new Patient(10);
        person2.addRecord(93,"Saturation", 1714376789050L);
        person2.addRecord(90,"SystolicPressure", 1714376789050L);
        assertEquals(false, generator.hypotensiveHypoxemiaAlert(person2));
        Patient person3 = new Patient(24);
        person2.addRecord(92,"Saturation", 1714376789050L);
        person2.addRecord(95,"SystolicPressure", 1714376789050L);
        assertEquals(false, generator.hypotensiveHypoxemiaAlert(person2));

    }
    @Test
    void evaluateData(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Exception exception = assertThrows(NullPointerException.class,()->{
            generator.evaluateData(null);
        });
        assertEquals("Patient data is null.", exception.getMessage()); //trigger alert//PASSED
    }
    @Test
    void testRepeatedAlertDecorator() {
        Alert basicAlert = new Alert("1", "High Blood Pressure", System.currentTimeMillis());
        Alert repeatedAlert = new RepeatedAlertDecorator(basicAlert);
        repeatedAlert.triggerAlert();
        String output = outContent.toString();
        assertTrue(output.contains("High Blood Pressure for patient 1"), "Output: " + output);
        assertTrue(output.contains("Repeated alert check"), "Output: " + output);
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert basicAlert = new Alert("1", "High Blood Pressure", System.currentTimeMillis());
        Alert priorityAlert = new PriorityAlertDecorator(basicAlert);
        priorityAlert.triggerAlert();
        String output = outContent.toString();
        assertTrue(output.contains("High Blood Pressure for patient 1"), "Output: " + output);
        assertTrue(output.contains("Priority alert triggered"), "Output: " + output);
    }
    @Test
    void testCombinedDecorators() {
        Alert basicAlert = new Alert("1", "High Blood Pressure", System.currentTimeMillis());
        Alert repeatedAlert = new RepeatedAlertDecorator(basicAlert);
        Alert priorityAlert = new PriorityAlertDecorator(repeatedAlert);
        priorityAlert.triggerAlert();
        String output = outContent.toString();
        assertTrue(output.contains("High Blood Pressure for patient 1"), "Output: " + output);
        assertTrue(output.contains("Repeated alert check"), "Output: " + output);
        assertTrue(output.contains("Priority alert triggered"), "Output: " + output);
    }
    @Test
    void testBasicAlert() {
        Alert alert = new Alert("1", "High Blood Pressure", System.currentTimeMillis());
        alert.triggerAlert();
        String output = outContent.toString();
        assertTrue(output.contains("High Blood Pressure for patient 1"), "Output: " + output);
    }

    @Test
    void testSystolicPressureStrategy(){

        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(190,"SystolicPressure", 1714376789050L);
        BloodPressureStrategy strategy = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy);
        generator.evaluateStrategy(person1);
        assertEquals(true, strategy.checkSystolicPressure(person1));

        Patient person2=new Patient(2);
        person2.addRecord(80,"SystolicPressure", 1714376789050L);
        BloodPressureStrategy strategy2 = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy2);
        generator.evaluateStrategy(person2);
        assertEquals(true, strategy2.checkSystolicPressure(person2));
    }

    @Test
    void testDiastolicPressureStrategy(){

        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(130,"DiastolicPressure", 1714376789050L);
        BloodPressureStrategy strategy = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy);
        generator.evaluateStrategy(person1);
        assertEquals(true, strategy.checkDiastolicPressure(person1));

        Patient person2=new Patient(2);
        person2.addRecord(50,"DiastolicPressure", 1714376789050L);
        BloodPressureStrategy strategy2 = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy2);
        generator.evaluateStrategy(person2);
        assertEquals(true, strategy2.checkDiastolicPressure(person1));
    }

    @Test
    void testCheckBloodPressureIncreasingStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(70,"DiastolicPressure", 1714376789050L);
        person1.addRecord(90,"DiastolicPressure", 1714376789050L);
        person1.addRecord(110,"DiastolicPressure", 1714376789050L);

        BloodPressureStrategy strategy = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy);
        generator.evaluateStrategy(person1);

        assertEquals(true, strategy.checkBloodPressureIncreasing(person1));
    }

    @Test
    void testCheckBloodPressureDecreasingStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Patient person1=new Patient(1);
        person1.addRecord(100,"DiastolicPressure", 1714376789050L);
        person1.addRecord(90,"DiastolicPressure", 1714376789050L);
        person1.addRecord(80,"DiastolicPressure", 1714376789050L);

        BloodPressureStrategy strategy = new BloodPressureStrategy(storage);
        generator.setStrategy(strategy);
        generator.evaluateStrategy(person1);

        assertEquals(true, strategy.checkBloodPressureDecreasing(person1));
    }

    @Test
    void testCheckLowSaturationStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        Patient person=new Patient(1);

        person.addRecord(80,"Saturation", 1714376789050L);

        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy(storage);
        generator.setStrategy(strategy);
        generator.evaluateStrategy(person);

        assertEquals(true, strategy.checkLowSaturation(person));
    }

    @Test
    void testCheckRapidDropStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy(storage);
        generator.setStrategy(strategy);


        Patient person1=new Patient(1);
        person1.addRecord(95,"Saturation", 100);
        person1.addRecord(89,"Saturation", 600100);
        generator.evaluateStrategy(person1);
        assertEquals(true, strategy.checkRapidDrop(person1));

        Patient person2=new Patient(2);
        person2.addRecord(95,"Saturation", 100);
        person2.addRecord(92,"Saturation", 600000);
        person2.addRecord(89,"Saturation", 600100);
        generator.evaluateStrategy(person2);
        assertEquals(true, strategy.checkRapidDrop(person2));

        Patient person3=new Patient(3);
        person3.addRecord(95,"Saturation", 100);
        person3.addRecord(92,"Saturation", 600000);
        generator.evaluateStrategy(person3);
        assertEquals(false, strategy.checkRapidDrop(person3));

        Patient person4=new Patient(4);
        person4.addRecord(95,"Saturation", 100);
        person4.addRecord(92,"Saturation", 600100);
        generator.evaluateStrategy(person4);
        assertEquals(false, strategy.checkRapidDrop(person4));

        Patient person5=new Patient(5);
        person5.addRecord(95,"Saturation", 100);
        person5.addRecord(92,"Saturation", 600100);
        person5.addRecord(94,"Saturation", 600400);
        person5.addRecord(93,"Saturation", 600600);
        generator.evaluateStrategy(person5);
        assertEquals(false, strategy.checkRapidDrop(person5));
    }

    @Test
    void testCheckHeartRateStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        HeartRateStrategy strategy = new HeartRateStrategy(storage);
        generator.setStrategy(strategy);

        Patient person1=new Patient(1);
        person1.addRecord(40,"ECG", 1714376789050L);
        generator.evaluateStrategy(person1);
        assertEquals(true, strategy.checkHeartRate(person1));

        Patient person2=new Patient(2);
        person2.addRecord(110,"ECG", 1714376789050L);
        generator.evaluateStrategy(person2);
        assertEquals(true, strategy.checkHeartRate(person2));
    }

    @Test
    void testIrregularBeatStrategy(){
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        HeartRateStrategy strategy = new HeartRateStrategy(storage);
        generator.setStrategy(strategy);

        Patient person1=new Patient(1);
        person1.addRecord(70,"ECG", 10);
        person1.addRecord(70,"ECG", 80);
        generator.evaluateStrategy(person1);
        assertEquals(true, strategy.irregularBeat(person1));

        Patient person2=new Patient(2);
        person2.addRecord(70,"ECG", 10);
        person2.addRecord(70,"ECG", 15);
        person2.addRecord(70,"ECG", 20);
        generator.evaluateStrategy(person2);
        assertEquals(true, strategy.irregularBeat(person2));

        Patient person3=new Patient(3);
        person3.addRecord(70,"ECG", 10);
        person3.addRecord(70,"ECG", 50);
        generator.evaluateStrategy(person3);
        assertEquals(false, strategy.irregularBeat(person3)); //everything in order
    }
    @Test
    void testEvaluateStrategyNoStrategy() {
        Patient patient = new Patient(1);
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);;
        Exception exception = assertThrows(NullPointerException.class, () -> {
            generator.setStrategy(null);
            generator.evaluateStrategy(patient);
        });
        assertEquals("No strategy", exception.getMessage());
    }

    @Test
    void testEvaluateStrategyNoPatient() {
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);
        HeartRateStrategy strategy = new HeartRateStrategy(storage);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            generator.setStrategy(strategy);
            generator.evaluateStrategy(null);
        });

        assertEquals("No patient", exception.getMessage());
}
    @Test
    void testEvaluateStrategyNoPatientNoStrategy() {
        Patient patient=new Patient(1);
        DataStorage storage = new DataStorage();
        AlertGenerator generator=new AlertGenerator(storage);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            generator.evaluateStrategy(patient);
        });

        assertEquals("No strategy", exception.getMessage());
}
}