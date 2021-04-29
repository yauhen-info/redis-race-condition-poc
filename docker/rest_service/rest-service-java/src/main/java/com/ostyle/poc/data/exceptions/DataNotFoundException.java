package com.ostyle.poc.data.exceptions;

/**
 * DataNotFoundException is thrown by certain methods
 * when data is not found in any type of storage,
 * including caching solutions if required by the logic.
 */
public class DataNotFoundException extends Exception {

    public DataNotFoundException(String message) {
        super(message);
    }
}
