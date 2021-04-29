package com.ostyle.poc.data.exceptions;

/**
 * It shall be thrown in cases when race condition detected in Redis.
 */
public class InvalidRedisKeyValueState extends Exception {
    private String key;

    public InvalidRedisKeyValueState(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
