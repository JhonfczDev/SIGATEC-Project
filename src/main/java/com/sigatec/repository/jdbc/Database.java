package com.sigatec.repository.jdbc;

import com.sigatec.repository.PersistenceException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {
    private Database(){
    }

    // The only method other classes call: returns a ready-to-use connection.
    public static Connection getConnection(){
        Properties config = loadConfig();
        String url = requiredProperty(config, "db.url");
        String user = requiredProperty(config, "db.user");
        String password = requiredProperty(config, "db.password");

        try {
            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new PersistenceException("No se pudo conectar la base de datos.",e);
        }
    }

    // Reads db.properties from the SIGATEC folder inside the user's home directory.
    private static Properties loadConfig(){
        Path path = Path.of(System.getProperty("user.home"), "SIGATEC", "db.properties");
        Properties properties = new Properties();

        try(InputStream in = Files.newInputStream(path)){
            properties.load(in);

        } catch (IOException e) {
            throw new PersistenceException("No se pudo leer el archivo " + path, e);
        }
        return properties;
    }

    // Returns the value of a required key, or fails with a clear message if it is missing.
    private static String requiredProperty(Properties properties, String key){
        String value = properties.getProperty(key);

        if (value == null || value.isBlank()){
            throw new PersistenceException("Falta la propiedad " + key + " en db.properties");
        }
        return value;
    }
}
