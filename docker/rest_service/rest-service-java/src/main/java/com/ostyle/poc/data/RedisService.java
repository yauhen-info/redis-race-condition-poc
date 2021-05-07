package com.ostyle.poc.data;

import com.ostyle.poc.data.exceptions.InvalidRedisKeyValueState;
import com.ostyle.poc.rest.config.RedisConfig;
import io.bootique.config.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

/**
 * Handles data read and write from/to Redis.
 */
public class RedisService implements MonitoredRetrievable {

    private static final int MILLION = 1_000_000;
    private static final String OK_STATUS = "OK";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private final Long keyExpireTime;
    private final JedisPool pool;
    private Jedis jedis; // jedis instance that shall be passed alongside

    @Inject
    public RedisService(Provider<ConfigurationFactory> configFactory) {
        super();
        RedisConfig config = configFactory.get().config(RedisConfig.class, getStorageName());

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(config.getMaxThreads());

        pool = new JedisPool(jedisPoolConfig, config.getHost(), config.getPort(), config.getTimeout(), null);
        keyExpireTime = config.getExpireTime();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.debug("Shutting the service down.");
            LOGGER.debug("Closing Redis connections pool...");
            pool.close();
            LOGGER.debug("Redis connections pool has been closed before exiting the service.");
        }));
    }

    @Override
    public String getStorageName() {
        return "redis";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    public JedisPool getPool() {
        return pool;
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public Jedis getJedis() {
        return jedis;
    }

    /**
     * Requests data from Redis by key and logs the execution time.
     *
     * @param key to request a value by
     * @return value for key, or null if not found
     */
    @Override
    public String getDataByKey(String key) {
        getJedis().watch(key);
        String dataByKey = getJedis().get(key);
        if (dataByKey != null) {
//            getJedis().unwatch();
            return dataByKey;
        }
        return null;
    }

    /**
     * Tries to write a key-value pair into Redis, employing optimistic locking.
     */
    public String updateValueInRedis(String key, String value) throws InvalidRedisKeyValueState {
        LOGGER.debug("{}: Started updating Redis with [{} : {}]", getStorageName(), key, value);
        String executionStatus = OK_STATUS;
        long startTime = System.nanoTime();

        Transaction transaction = getJedis().multi();
        transaction.setex(key, keyExpireTime, value);

        List<Object> executionResults = transaction.exec();
        if (executionResults != null && executionResults.size() != 1) {
            transaction.discard();
            executionStatus = "ERROR";
            LOGGER.error("Cannot put into Redis [{} : {}]", key, value);
        } else {
            LOGGER.debug("Potential concurrent modification of Redis key '{}' happened; transaction has been discarded.", key);
            throw new InvalidRedisKeyValueState("Race condition", key);
        }
        getJedis().unwatch();
        long elapsedNanos = System.nanoTime() - startTime;
        LOGGER.debug("{}: Finished updating Redis for key ({}) in {}ns ({}ms): {}",
                getStorageName(),
                key,
                elapsedNanos,
                elapsedNanos / MILLION,
                OK_STATUS.equals(executionStatus) ? "WRITE" : executionStatus
        );
        return value;
    }
}
