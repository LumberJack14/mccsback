package org.mccheckers.mccheckers_backend;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found in classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getJwtSecret() {
        return getProperty("jwt.secret.key");
    }

    public static String getJwtExpirationTime() {
        return getProperty("jwt.expiration.time");
    }

    public static String getDbUrl() {
        return getProperty("db.url");
    }

    public static String getDbUsername() {
        return getProperty("db.username");
    }

    public static String getDbPassword() {
        return getProperty("db.password");
    }

    private static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Property " + key + " is missing from the configuration");
        }
        return value;
    }

    public static String getAdminUsername(){
        return getProperty("admin.username");
    }

    public static String getAdminPassword() {
        return getProperty("admin.password");
    }
}
