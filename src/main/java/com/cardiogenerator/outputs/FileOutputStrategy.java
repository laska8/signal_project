//Wrong package name (no underscores)
package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
//Changed class name to FileOutputStrategy
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory; // Changing variable name to camelCase convention

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();// Changing variable name to camelCase convention

    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    @Override
    // Renaming the variable timestamp
    public void output(int patientId, long timeStamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        String FilePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
            //Fix tabulation
            Files.newBufferedWriter(Paths.get(FilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timeStamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}