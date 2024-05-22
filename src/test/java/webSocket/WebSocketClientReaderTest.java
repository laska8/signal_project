package webSocket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import com.data_management.DataStorage;
import com.data_management.WebSocketClientReader;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
/**
 * Unit tests for the WebSocketClientReader class.
 */
class WebSocketClientReaderTest {
    /**
     * Tests that onOpen method prints "Connected".
     */
    @Test
    public void testOnOpen() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(mockDataStorage, URI.create("ws://localhost"));

        ServerHandshake mockHandshake = Mockito.mock(ServerHandshake.class);
        client.onOpen(mockHandshake);

        // Verify that "Connected" is printed
        assertEquals("Connected", outContent.toString().trim());
    }
    /**
     * Tests that onClose method prints the appropriate disconnection message.
     */
    @Test
    public void testOnClose() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        DataStorage mockDataStorage = Mockito.mock(DataStorage.class);
        WebSocketClientReader reader = new WebSocketClientReader(mockDataStorage, URI.create("ws://localhost"));

        reader.onClose(1000, "Normal closure", true);

        // Verify that "Disconnected from server: Normal closure" is printed
        assertEquals("Disconnected from server: Normal closure", outContent.toString().trim());
    }
    /**
     * Tests that a valid message is correctly parsed and stored.
     */
    @Test
    public void testValidMessage() {
        DataStorage storage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(storage, URI.create("ws://localhost"));

        client.onMessage("123,1621562400,ECG,25.5");

        verify(storage).addPatientData(123, 25.5, "ECG", 1621562400L);
    }
    /**
     * Tests that an invalid message does not result in data being stored.
     */

    @Test
    public void testInvalidMessage() {

        DataStorage storage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(storage, URI.create("ws://localhost"));

        client.onMessage("Invalid message");

        verify(storage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }
    /**
     * Tests that a message with parsing errors does not result in data being stored.
     */

    @Test
    public void testParsingError() {
        DataStorage storage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(storage, URI.create("ws://localhost"));

        String message = "Patient ID: not_number, Timestamp: 1716296978067, Label: ECG, Data: 0.25539439796331415";
        client.onMessage(message);
        verify(storage, never()).addPatientData(anyInt(), anyDouble(), anyString(), anyLong());
    }
    /**
     * Tests that the onError method handles exceptions correctly.
     */
    @Test
    void testOnError() {
        DataStorage storage = Mockito.mock(DataStorage.class);
        WebSocketClientReader client = new WebSocketClientReader(storage, URI.create("ws://localhost"));

        Exception exception = new Exception("Test exception");
        client.onError(exception);
    }
    /**
     * Tests that readData method throws an IOException when the connection fails.
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