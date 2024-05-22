package com.data_management;

import java.io.IOException;
import java.net.URI;
/**
 * DataReader2 is an interface that defines a method for reading data
 * from a specified server URI and storing it in a data storage.
 */
public interface DataReader2 {
    /**
     * Reads data from a specified server URI and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @param serverUri the URI of the server from which data will be read
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage, URI serverUri) throws IOException;
}
