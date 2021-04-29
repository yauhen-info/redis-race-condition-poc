package com.ostyle.poc.rest;

import com.ostyle.poc.data.exceptions.DataNotFoundException;
import com.ostyle.poc.data.ScoreDataService;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Provides HTTP API to receive score data.
 */
@Path("/")
public class ScoreDataApi {

    @Inject
    private ScoreDataService dataHandler;

    /**
     * Returns stub message only. It can be used as an application health check endpoint.
     */
    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public String get() {
        return "{\"status\": \"OK\"}";
    }

    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("key") String key, @DefaultValue("0") @QueryParam("delay") int delay) {
        try {
            String value = dataHandler.requestData(key, delay);
            return new KeyValueResultEntity(key, value).toString();
        } catch (DataNotFoundException e) {
            return new KeyValueResultEntity(key, null, e.getMessage()).toString();
        }
    }
}
