package com.ostyle.poc.data;

import org.slf4j.Logger;

/**
 * Defines an interface for services which load the data by a key from a storage.
 */
public interface MonitoredRetrievable {
    int MILLION = 1000000;

    Logger getLogger();

    String getStorageName();

    String getDataByKey(String key);

    /**
     * Wraps {@link this.getDataByKey} with calculation of the execution time.
     *
     * @param key to get the value by
     * @return retrieved value
     */
    default String getDataByKeyMonitored(String key) {
        getLogger().debug("{}: started value lookup by key '{}'", getStorageName(), key);
        long startTime = System.nanoTime();

        String valueForKey = getDataByKey(key);

        long elapsedNanos = System.nanoTime() - startTime;
        getLogger().debug("{}: finished value lookup [{} : {}] in {}ns ({}ms): {}",
                getStorageName(),
                key,
                valueForKey == null ? "" : valueForKey,
                elapsedNanos,
                elapsedNanos / MILLION,
                valueForKey == null ? "MISS" : "HIT");
        return valueForKey;
    }
}
