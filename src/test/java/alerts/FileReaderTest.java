package alerts;

import com.data_management.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for the FileReader class.
 */
class FileReaderTest {
    /**
     * Tests the addition and retrieval of patient records from a file.
     */
    @Test
    void testAddAndGetRecords() {

        DataReader reader = new FileReader("src/test/java/data_management/outputFileTest");

        DataStorage storage = new DataStorage();

        try{
            reader.readData(storage);
        } catch(Exception e){
            System.out.println("something went wrong");
        }

        List<PatientRecord> records = storage.getRecords(16, 1714748468033L, 1714748468034L);
        assertEquals(2, records.size());
        assertEquals(-0.34656395320945643, records.get(0).getMeasurementValue());

        List<Patient> allRecords = storage.getAllPatients();
        assertEquals(1, allRecords.get(1).getPatientId());
        assertEquals(5, allRecords.get(3).getPatientId());
    }
    /**
     * Tests the behavior of getRecords method when no records are found.
     */
    @Test
    void testGetRecordsNullBehaviour() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(10, 1714748468033L, 1714748468034L);
        assertEquals(0, records.size()); // Check if the new empty Array was created
    }
    /**
     * Tests the behavior of readData method when file reading fails.
     */
    @Test
    void testReadDataFileReadingError() {
        DataReader reader = new FileReader("invalid/directory/path");
        DataStorage storage = new DataStorage();

        Exception exception = assertThrows(IOException.class, () -> {
            reader.readData(storage);
        });

        String expectedMessage = "Error walking through directory";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
}

}