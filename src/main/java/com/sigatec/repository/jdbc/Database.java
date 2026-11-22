package com.sigatec.repository.jdbc;

import com.sigatec.repository.PersistenceException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {

    // One pool for the whole application. It is created the first time it is needed.
    private static HikariDataSource dataSource;

    private Database() {
    }

    // Returns a connection from the pool. Closing it gives it back to the pool instead of closing it for real.
    public static synchronized Connection getConnection() {
        try {
            return pool().getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("No se pudo conectar la base de datos.", e);
        }
    }

    // Creates the pool with the settings from db.properties, only once.
    private static HikariDataSource pool() {
        if (dataSource == null) {
            Properties config = loadConfig();
            HikariConfig hikari = new HikariConfig();
            hikari.setJdbcUrl(requiredProperty(config, "db.url"));
            hikari.setUsername(requiredProperty(config, "db.user"));
            hikari.setPassword(requiredProperty(config, "db.password"));
            hikari.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(hikari);
        }
        return dataSource;
    }

    // Reads db.properties from the SIGATEC folder inside the user's home directory.
    private static Properties loadConfig() {
        Path path = Path.of(System.getProperty("user.home"), "SIGATEC", "db.properties");
        Properties properties = new Properties();

        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        } catch (IOException e) {
            throw new PersistenceException("No se pudo leer el archivo " + path, e);
        }
        return properties;
    }

    // Returns the value of a required key, or fails with a clear message if it is missing.
    private static String requiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new PersistenceException("Falta la propiedad " + key + " en db.properties");
        }
        return value;
    }
}