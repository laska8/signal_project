package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
/**
 * This class is responsible for writing the data into a tcp server.
 * It sets up a tcp server that get inputs from clients and sends data back to them.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    /**
     * Initialize a TCP output strategy to send data to clients over TCP
     *
     * @param port The port number to listen for client connections.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Outputs patient data.
     *
     * @param patientId The ID of the patient.
     * @param timestamp The timestamp of the data.
     * @param label The label of the data.
     * @param data The data to be output.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
