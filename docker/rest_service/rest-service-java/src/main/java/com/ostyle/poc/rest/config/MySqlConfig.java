package com.ostyle.poc.rest.config;

/**
 * Maps configuration properties for Java Database Connectivity (JDBC) in YML file.
 */
public class MySqlConfig {

    private String jdbcUrl;
    private String password;
    private String username;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
