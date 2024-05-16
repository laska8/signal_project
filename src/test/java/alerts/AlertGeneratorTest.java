package alerts;

import com.alerts.AlertGenerator;
import com.data_management.*;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;

import com.data_management.FileReader;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.io.File;
import java.io.IOException;
import java.util.List;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

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


}