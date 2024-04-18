//Wrong package name(no underscores). Consecutive words are concatenated together.
package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 * This class is responsible for writing the data into a file.
 * The output of this class will be in the form of a file.
 */
//Changed class name to FileOutputStrategy (Class names should be written in UpperCamelCase).
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory; // Changed variable name to camelCase convention.

    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();// Changed variable name to camelCase convention.
    /**
     * Constructs a FileOutputStrategy with the specified base directory.
     *
     * @param baseDirectory The base directory for storing files.
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    @Override
    /**
     * Outputs patient data to files.
     *
     * @param patientId The ID of the patient.
     * @param timeStamp The timestamp of the data.
     * @param label The label of the data.
     * @param data The data to be output.
     */
    //Renaming the variable timestamp to camelCase convention.
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