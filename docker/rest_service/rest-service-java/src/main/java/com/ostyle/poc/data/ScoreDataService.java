package com.ostyle.poc.data;

import com.ostyle.poc.data.exceptions.DataNotFoundException;
import com.ostyle.poc.data.exceptions.InvalidRedisKeyValueState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.inject.Inject;

/**
 * Sets a way to read and write score data into any storage solution.
 */
public class ScoreDataService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScoreDataService.class);

    @Inject
    private RedisService redisService;

    @Inject
    private MySqlService mySqlService;

    /**
     * Tries to load data from cache; if not successful, looks it up in a read-only (mysql) storage.
     * Then puts into cache and returns to a user.
     *
     * @param key   to request the data by
     * @param delay a delay in milliseconds before writing value into cache (for testing purposes)
     * @return data of type String or null if not found
     */
    public String requestData(String key, int delay) throws DataNotFoundException {
        // From Redis docs: if there are race conditions and another client modifies the result of our key
        // in the time between our call to WATCH and our call to EXEC, the transaction will fail.
        try (Jedis jedis = redisService.getPool().getResource()) {
            // first, try to get data from Redis
            redisService.setJedis(jedis);
            String dataByKey = redisService.getDataByKeyMonitored(key);
            if (dataByKey != null) {
                return dataByKey;
            }
            // second, try to get data from mysql, put it into cache for potential successive requests
            // as we always expect the data to be in the read-only store, the exception is thrown if it's not the case
            String dataFromDB = mySqlService.getDataByKeyMonitored(key);
            LOGGER.debug("Retrieved database value '{}'", dataFromDB); // this message is helpful in race condition testing analysis
            if (dataFromDB != null) {
                holdExecution(delay); // not related to the business logic, introduced only for race condition testing purposes
                try {
                    return redisService.updateValueInRedis(key, dataFromDB); // todo: consider a separate thread to return the value to use quicker
                } catch (InvalidRedisKeyValueState e) {
                    return requestData(key, delay); // need to request again, as Redis key could have been expired by this time
                }
            }
            String errorMessage = String.format("Data not found in DB for key '%s'.", key);
            LOGGER.error(errorMessage);
            throw new DataNotFoundException(errorMessage);
        }
    }

    /**
     * Holds execution of the thread for a delay. Must only be considered for testing purposes.
     *
     * @param delay number of milliseconds to hold the execution
     */
    private void holdExecution(int delay) {
        if (delay > 0) {
            try {
                LOGGER.debug("Waiting for {}ms", delay);
                Thread.sleep(delay);
                LOGGER.debug("Woke up after {}ms", delay);
            } catch (InterruptedException e) {
                LOGGER.debug("Waiting for cache write was not successful", e);
            }
        }
    }
}
