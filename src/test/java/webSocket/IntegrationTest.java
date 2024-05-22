package webSocket;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientReader;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Integration tests for the WebSocketClientReader and related components.
 */
class IntegrationTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;
    private WebSocketClientReader client;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private long currentTime;

    /**
     * Sets up the test environment before each test.
     *
     * @throws URISyntaxException if the URI is incorrect
     */
    @BeforeEach
    void setUp() throws URISyntaxException {
        storage = new DataStorage();
        alertGenerator = spy(new AlertGenerator(storage));
        client = spy(new WebSocketClientReader(storage, new URI("ws://localhost:8080")));

        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        currentTime = System.currentTimeMillis();
    }
    /**
     * Restores the original output stream after each test.
     */

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    /**
     * Tests the integration of WebSocketClientReader with DataStorage and AlertGenerator.
     * Ensures that a message is correctly processed and an alert is generated.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testIntegration() throws Exception {
        doNothing().when(client).connect();
        doNothing().when(client).reconnect();

        client.onOpen(mock(ServerHandshake.class));

        // Ensure the timestamp falls within the expected range
        long timestamp = currentTime - 1000; // 1 second ago

        String message = "1," + timestamp + ",ECG,160";
        client.onMessage(message);

        List<Patient> patients = storage.getAllPatients();
        assertFalse(patients.isEmpty());

        Patient patient = patients.get(0);
        assertEquals(1, patient.getPatientId());

        // Verify the data was stored correctly
        List<PatientRecord> records = storage.getRecords(1, timestamp, timestamp);
        assertFalse(records.isEmpty());
        assertEquals(1, records.size());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(160, records.get(0).getMeasurementValue(), 0.001);

        alertGenerator.evaluateData(patient);

        String output = outContent.toString();
        assertTrue(output.contains("Abnormal heart rate alert"));
    }
    /**
     * Tests that an error during WebSocketClientReader operation is correctly handled.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void testIntegrationError() throws Exception {

        doThrow(new RuntimeException("Test exception")).when(client).connect();

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errContent));

        try {
            client.onError(new Exception("Test error"));
        } finally {
            System.setErr(originalErr);
        }

        String errOutput = errContent.toString();

        assertTrue(errOutput.contains("Test error"), "Error output should contain 'Test error'");
        assertTrue(errOutput.contains("java.lang.Exception: Test error"), "Error output should contain 'java.lang.Exception: Test error'");
    }

    /**
     * Tests that WebSocketClientReader successfully connects to the WebSocket server.
     *
     * @throws URISyntaxException if the URI is incorrect
     * @throws IOException if an error occurs during connection
     */
    @Test
    public void testConnectionSuccess() throws URISyntaxException, IOException {
        WebSocketClientReader spyClient = spy(client);

        doNothing().when(spyClient).connect();

        spyClient.readData(storage, new URI("ws://localhost:8080"));

        verify(spyClient, times(1)).connect();
    }
    /**
     * Tests that WebSocketClientReader throws an IOException when the connection fails.
     */
    @Test
    void testConnectionFailure() {
        DataStorage storage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(storage, URI.create("ws://localhost"));

        WebSocketClientReader spyClient= Mockito.spy(client);
        doThrow(new RuntimeException("Connection failed")).when(spyClient).connect();

        IOException thrown = assertThrows(IOException.class, () -> {
            spyClient.readData(storage, URI.create("ws://localhost"));
        });

        assertEquals("Failed to connect to WebSocket server", thrown.getMessage());
    }
}