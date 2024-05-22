package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
/**
 * WebSocketClientReader is a client that reads data from a WebSocket server and stores it in a DataStorage instance.
 */
public class WebSocketClientReader extends WebSocketClient implements DataReader2 {

    private DataStorage dataStorage;
    /**
     * Constructs a WebSocketClientReader with the specified DataStorage instance and server URI.
     *
     * @param dataStorage the DataStorage instance to store the received data
     * @param serverUri   the URI of the WebSocket server
     */
    public WebSocketClientReader(DataStorage dataStorage, URI serverUri){
        super(serverUri);
        this.dataStorage=dataStorage;
    }
    /**
     * Called when the WebSocket connection is opened.
     *
     * @param serverHandshake the handshake data received from the server
     */
    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected");
    }
    /**
     * Called when a message is received from the server. Parses the message and stores the data in the DataStorage instance.
     *
     * @param s the message received from the server
     */
    @Override
    public void onMessage(String s) {
        try {
            String[] parts = s.split(",");

            if (parts.length == 4) {

                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String recordType = parts[2];

                if(parts[3].contains("%")) {

                    parts[3] = parts[3].replace("%", " ");
                }

                if(parts[3].equals("triggered")){
                    parts[3]="1";
                }

                if(parts[3].equals("resolved")){
                    parts[3]="0";
                }

                double measurementValue = Double.parseDouble(parts[3]);

                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            } else {
                System.err.println("Invalid message format: " + s);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing message: " + s);
            e.printStackTrace();
        }
    }
    /**
     * Called when the WebSocket connection is closed.
     *
     * @param i the status code
     * @param s the reason for the closure
     * @param b indicates if the connection closed cleanly
     */

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("Disconnected from server: " + s);

    }

    /**
     * Called when an error occurs in the WebSocket connection.
     *
     * @param e the exception that was thrown
     */

    @Override
    public void onError(Exception e) {
        e.printStackTrace();

    }
    /**
     * Connects to the WebSocket server and starts reading data.
     *
     * @param dataStorage the DataStorage instance to store the received data
     * @param serverUri   the URI of the WebSocket server
     * @throws IOException if there is an error connecting to the WebSocket server
     */
    @Override
    public void readData(DataStorage dataStorage, URI serverUri) throws IOException {

        this.dataStorage = dataStorage;
        try{
            this.connect();
        }
        catch(Exception e){
            throw new IOException("Failed to connect to WebSocket server", e);
        }
    }
}
