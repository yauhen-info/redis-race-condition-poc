package com.ostyle.poc.rest;

import javax.ws.rs.core.Response;

/**
 * Data Transfer Object (DTO) for Score API endpoint.
 */
public class KeyValueResultEntity {

    public String key;
    public String value;
    public Response.Status status;
    public String message;

    public KeyValueResultEntity(String key, String value) {
        this.key = key;
        this.value = value;
        this.status = value == null ? Response.Status.NOT_FOUND : Response.Status.OK;
        this.message = value == null ? "Not found in storage" : "Found";
    }

    public KeyValueResultEntity(String key, String value, String errorMessage) {
        this.key = key;
        this.value = value;
        this.status = value == null ? Response.Status.NOT_FOUND : Response.Status.OK;
        this.message = value == null ? errorMessage : "Found";
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("{\"key\":\"%s\", \"value\":\"%s\", status: \"%s\", message:\"%s\"}",
                getKey(), getValue(), getStatus(), getMessage());
    }
}
