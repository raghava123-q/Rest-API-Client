package com.example.weatherclient.server;

import com.example.weatherclient.config.ConfigLoader;
import com.example.weatherclient.exception.ApiException;
import com.example.weatherclient.model.WeatherResponse;
import com.example.weatherclient.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Embedded HTTP server to serve frontend assets and expose API endpoint.
 */
public class WebServer {
    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());
    private final int port;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper;
    private HttpServer server;

    public WebServer() throws ApiException {
        this.port = Integer.parseInt(ConfigLoader.getProperty("server.port"));
        this.weatherService = new WeatherService();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Starts the HTTP Server on the configured port.
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Map Context routes
            server.createContext("/", new StaticFileHandler("web/index.html", "text/html"));
            server.createContext("/style.css", new StaticFileHandler("web/style.css", "text/css"));
            server.createContext("/app.js", new StaticFileHandler("web/app.js", "application/javascript"));
            server.createContext("/api/weather", new WeatherApiHandler());

            // Set executor to handle requests in multiple threads
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            LOGGER.info("Embedded Web Server started successfully on port: " + port);
            LOGGER.info("Open http://localhost:" + port + " in your browser to view the application UI.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start the embedded web server", e);
            throw new RuntimeException("Could not launch web server", e);
        }
    }

    /**
     * Stops the HTTP Server.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            LOGGER.info("Web Server stopped.");
        }
    }

    /**
     * Handler to serve static resource files.
     */
    private static class StaticFileHandler implements HttpHandler {
        private final String resourcePath;
        private final String contentType;

        public StaticFileHandler(String resourcePath, String contentType) {
            this.resourcePath = resourcePath;
            this.contentType = contentType;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("Request static file: " + exchange.getRequestURI().getPath());
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    LOGGER.severe("Resource not found: " + resourcePath);
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                    return;
                }

                byte[] fileBytes = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error serving static file", e);
                exchange.sendResponseHeaders(500, -1);
            }
        }
    }

    /**
     * Handler for the Weather API endpoint /api/weather
     */
    private class WeatherApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            LOGGER.info("API request received: " + exchange.getRequestURI());

            // Enable CORS for frontend flexibility
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            // Parse Query Params
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String latStr = params.get("latitude");
            String lonStr = params.get("longitude");

            if (latStr == null || lonStr == null) {
                sendErrorResponse(exchange, 400, "Missing required parameters: latitude and longitude");
                return;
            }

            try {
                double latitude = Double.parseDouble(latStr);
                double longitude = Double.parseDouble(lonStr);

                // Fetch Weather Data
                WeatherResponse weatherResponse = weatherService.getCurrentWeather(latitude, longitude);
                String jsonResult = objectMapper.writeValueAsString(weatherResponse);

                byte[] responseBytes = jsonResult.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (NumberFormatException e) {
                sendErrorResponse(exchange, 400, "Invalid coordinates format. Must be floating-point numbers.");
            } catch (ApiException e) {
                LOGGER.log(Level.SEVERE, "API Exception during request processing", e);
                sendErrorResponse(exchange, 502, "Error retrieving weather: " + e.getMessage());
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Unexpected exception during request processing", e);
                sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        }

        private void sendErrorResponse(HttpExchange exchange, int status, String message) throws IOException {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", message);
            String jsonError = objectMapper.writeValueAsString(errorMap);
            byte[] errorBytes = jsonError.getBytes(StandardCharsets.UTF_8);
            
            exchange.sendResponseHeaders(status, errorBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorBytes);
            }
        }

        private Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<>();
            if (query == null) {
                return result;
            }
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
            return result;
        }
    }
}
