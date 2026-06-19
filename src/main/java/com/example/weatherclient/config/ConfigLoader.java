package com.example.weatherclient.config;

import com.example.weatherclient.exception.ApiException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to load application configurations.
 */
public class ConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static final String PROPERTIES_FILE = "application.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Unable to find " + PROPERTIES_FILE);
                throw new RuntimeException("Sorry, unable to find " + PROPERTIES_FILE);
            }
            PROPERTIES.load(input);
            LOGGER.info("Configuration loaded successfully from " + PROPERTIES_FILE);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Exception occurred while loading properties", ex);
            throw new RuntimeException("Error loading application configuration", ex);
        }
    }

    /**
     * Gets a configuration value by key.
     *
     * @param key the property key
     * @return the property value
     * @throws ApiException if property is missing or empty
     */
    public static String getProperty(String key) throws ApiException {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new ApiException("Property '" + key + "' is not configured in " + PROPERTIES_FILE);
        }
        return value.trim();
    }
}
