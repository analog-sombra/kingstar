package com.librarymanagement.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {
    private static final String PROPERTIES_PATH = "/config/db.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(PROPERTIES_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Missing DB config file: " + PROPERTIES_PATH);
            }
            PROPERTIES.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load DB properties", ex);
        }
    }

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String username = PROPERTIES.getProperty("db.username");
        String password = PROPERTIES.getProperty("db.password");
        return DriverManager.getConnection(url, username, password);
    }
}
