package com.ostyle.poc.data;

import com.ostyle.poc.rest.config.MySqlConfig;
import io.bootique.config.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles data read and write from/to MySql database with a table 'scores'.
 */
public class MySqlService implements MonitoredRetrievable {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(MySqlService.class);

    private final MySqlConfig config;

    @Inject
    public MySqlService(Provider<ConfigurationFactory> configFactory) {
        this.config = configFactory.get().config(MySqlConfig.class, getStorageName());
    }

    @Override
    public String getStorageName() {
        return "mysql-db";
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    /**
     * Tries to load the data from 'scores' table by a given key.
     *
     * @param key tto load the data by
     * @return value of a 'counter' column for a given key (value of 'name' column), or null if not found
     */
    @Override
    public String getDataByKey(String key) {
        String query = String.format("select counter from scores where name='%s'", key);
        try (Connection con = DriverManager.getConnection(
                config.getJdbcUrl(), config.getUsername(), config.getPassword());
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getString("counter");
            }
            LOGGER.error("{}: No data for key '{}'", getStorageName(), key);
        } catch (SQLException e) {
            LOGGER.error("{} Could not process SQL properly.", getStorageName(), e);
        }
        return null;
    }
}
