package com.example.weatherclient.service;

import com.example.weatherclient.client.ApiClient;
import com.example.weatherclient.config.ConfigLoader;
import com.example.weatherclient.exception.ApiException;
import com.example.weatherclient.model.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service to orchestrate retrieving and parsing weather information.
 */
public class WeatherService {
    private static final Logger LOGGER = Logger.getLogger(WeatherService.class.getName());
    private final ApiClient apiClient;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Retrieves the weather for a specified latitude and longitude.
     *
     * @param latitude  the latitude location
     * @param longitude the longitude location
     * @return the mapped WeatherResponse object
     * @throws ApiException if configuration, HTTP call, or JSON parsing fails
     */
    public WeatherResponse getCurrentWeather(double latitude, double longitude) throws ApiException {
        String baseUrl = ConfigLoader.getProperty("api.base.url");
        
        // Build URL structure: e.g. base?latitude=XX&longitude=YY&current_weather=true
        String fullUrl = String.format("%s?latitude=%.4f&longitude=%.4f&current_weather=true", baseUrl, latitude, longitude);
        
        // Execute request
        String jsonResponse = apiClient.executeGetRequest(fullUrl);
        
        // Parse Response
        try {
            LOGGER.info("Parsing weather JSON response to WeatherResponse object.");
            return objectMapper.readValue(jsonResponse, WeatherResponse.class);
        } catch (IOException e) {
            String errorMsg = "Failed to parse API response JSON data";
            LOGGER.log(Level.SEVERE, errorMsg, e);
            throw new ApiException(errorMsg, e);
        }
    }
}
