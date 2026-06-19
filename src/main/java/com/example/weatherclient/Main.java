package com.example.weatherclient;

import com.example.weatherclient.exception.ApiException;
import com.example.weatherclient.server.WebServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application entrypoint.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting REST API Weather Client Application...");

        try {
            WebServer server = new WebServer();
            server.start();
            
            // Register shutdown hook to stop server gracefully on exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutdown signal received. Stopping server...");
                server.stop();
            }));

            // Keep the application running
            Thread.currentThread().join();

        } catch (ApiException e) {
            LOGGER.log(Level.SEVERE, "Configuration error preventing server startup.", e);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Application execution was interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }
}

